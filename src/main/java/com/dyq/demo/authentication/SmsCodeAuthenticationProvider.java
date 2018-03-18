package com.dyq.demo.authentication;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.InternalAuthenticationServiceException;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

//public class SmsCodeAuthenticationProvider implements AuthenticationProvider(没有setPasswordEncoder)
public class SmsCodeAuthenticationProvider implements AuthenticationProvider {

    @Autowired
    UserDetailsService userDetailsService;
    @Autowired
    PasswordEncoder passwordEncoder; //主要用来比对密码

    /*public SmsCodeAuthenticationProvider(UserDetailsService userDetailsService) {
        super();
        // 这个地方一定要对userDetailsService赋值，不然userDetailsService是null (这个坑有点深)
        setUserDetailsService(userDetailsService);
    }*/

    @Override
    public Authentication authenticate(Authentication authentication) throws AuthenticationException {
        System.out.println("SmsCodeAuthenticationProvider");
        SmsCodeAuthenticationToken authenticationToken = (SmsCodeAuthenticationToken) authentication;
        System.out.println("authenticationToken:"+authenticationToken.toString()+",getPrincipal:"+ authenticationToken.getPrincipal());
        //调用自定义的userDetailsService认证
        UserDetails user = userDetailsService.loadUserByUsername((String) authenticationToken.getPrincipal());
        System.out.println("authenticationToken:"+authenticationToken.toString()+",UserDetails:"+user.toString());
        if (user == null) {
            throw new InternalAuthenticationServiceException("无法获取用户信息");
        }
        //如果user不为空重新构建SmsCodeAuthenticationToken（已认证）
        SmsCodeAuthenticationToken authenticationResult = new SmsCodeAuthenticationToken(user, user.getAuthorities());

        authenticationResult.setDetails(authenticationToken.getDetails());

        return authenticationResult;
    }

    /**
     * 只有Authentication为SmsCodeAuthenticationToken使用此Provider认证
     * @param authentication
     * @return
     */
    @Override
    public boolean supports(Class<?> authentication) {
        return SmsCodeAuthenticationToken.class.isAssignableFrom(authentication);
    }

   public UserDetailsService getUserDetailsService() {
        return userDetailsService;
    }

    public void setUserDetailsService(UserDetailsService userDetailsService) {
        this.userDetailsService = userDetailsService;
    }

    public void setPasswordEncoder(PasswordEncoder passwordEncoder) {
        this.passwordEncoder = passwordEncoder;
    }

    public PasswordEncoder getPasswordEncoder() {
        return passwordEncoder;
    }
}