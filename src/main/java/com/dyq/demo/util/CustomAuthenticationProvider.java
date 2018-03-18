package com.dyq.demo.util;

import com.dyq.demo.authentication.discard.CustomWebAuthenticationDetails;
import com.dyq.demo.domain.User;
import com.dyq.demo.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.util.StringUtils;

import java.util.Collection;


public class CustomAuthenticationProvider extends DaoAuthenticationProvider {
    private final static Long CAPTCHA_EXPIRE_TIME=1000*60*2L;//120秒过期
    @Autowired
    UserService userService;
    private static final int MAX_FAILTIME = 3;

    @Autowired
    UserDetailsService userDetailsService; //主要用来检查用户名
    @Autowired
    BCryptPasswordEncoder passwordEncoder; //主要用来比对密码
    //@Autowired
    //SmsSendRecordService smsSendRecordService; //短信验证码服务

    @Override
    public Authentication authenticate(Authentication authentication) throws AuthenticationException {
        String username = authentication.getName();
        String password = (String) authentication.getCredentials();
        CustomWebAuthenticationDetails  customWebAuthenticationDetails;
        UserDetails userDetails;
        String captcha;
        String userCaptcha;
        String captchaTime;
        System.out.println("CustomAuthenticationProvider:username:"+username+",password:"+password);
        //检查用户名有效性
        try {
            customWebAuthenticationDetails = (CustomWebAuthenticationDetails) authentication.getDetails();
            captcha = customWebAuthenticationDetails.getCaptcha();
            userCaptcha = customWebAuthenticationDetails.getUserCaptcha();
            captchaTime = customWebAuthenticationDetails.getCaptchaTime();
            userDetails = userDetailsService.loadUserByUsername(username);
        } catch (UsernameNotFoundException e) {
            throw new UsernameNotFoundException("CustomAuthenticationProvider authenticate 用户不存在");
        }
        System.out.println("CustomAuthenticationProvider authenticate CaptchaTime:"+captchaTime+",Captcha:"+captcha+",userCaptcha:"+userCaptcha);
        if(!StringUtils.isEmpty(captchaTime) && !StringUtils.isEmpty(captcha) && !StringUtils.isEmpty(userCaptcha)){
            Long time = System.currentTimeMillis()-Long.valueOf(captchaTime);
            if(time>CAPTCHA_EXPIRE_TIME){
                //过期验证码
                throw new BadCredentialsException("CustomAuthenticationProvider authenticate 过期验证码");
            }else{
                if(captcha.equalsIgnoreCase(userCaptcha)){
                    //验证码正确

                }else{
                    //验证码不正确
                    throw new BadCredentialsException("CustomAuthenticationProvider authenticate 验证码不正确");
                }
            }
        }

        //匹配密码
        if (passwordEncoder.matches(password, userDetails.getPassword())) {
            Collection<? extends GrantedAuthority> authorities = userDetails.getAuthorities();
            try{//验证成功，重置密码错误次数。
                User user = (User) userService.getUserByUsername(authentication.getName());
                user.setFailtime(0);
                this.userService.updateUser(user);
            }
            catch(Exception exp){
                exp.printStackTrace();
            }
            return new UsernamePasswordAuthenticationToken(userDetails, password, authorities);
        } else {
            //密码错误,增加密码错误次数，达到最大次数时锁定账户。
            try{
                User user = (User) userService.getUserByUsername(authentication.getName());
                int num = user.getFailtime()+1;
                if(num==MAX_FAILTIME){
                    user.setFailtime(0);
                    user.setAccountNonLocked(false);
                }else{
                    user.setFailtime(num);
                }
                this.userService.updateUser(user);
            }
            catch(Exception exp){
                //没有此账号
                exp.printStackTrace();
            }
            throw new BadCredentialsException("CustomAuthenticationProvider authenticate 验证码不正确");
        }
    }

    @Override
    public boolean supports(Class<?> authentication) {
        return (UsernamePasswordAuthenticationToken.class.isAssignableFrom(authentication));
    }
}
