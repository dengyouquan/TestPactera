package com.dyq.demo.config;

import com.dyq.demo.authentication.MyAuthenticationSuccessHandler;
import com.dyq.demo.authentication.SmsCodeAuthenticationProvider;
import com.dyq.demo.authentication.SmsCodeAuthenticationSecurityConfig;
import com.dyq.demo.authentication.discard.CustomAuthenticationDetailsSource;
import com.dyq.demo.util.CustomAuthenticationProvider;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.security.authentication.AuthenticationDetailsSource;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.builders.WebSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.security.web.authentication.WebAuthenticationDetails;

import javax.servlet.http.HttpServletRequest;

@EnableWebSecurity
@EnableGlobalMethodSecurity(prePostEnabled = true)
public class SecurityConfig extends WebSecurityConfigurerAdapter {
    private static final String KEY = "www.dengyouquan.cn";

    @Autowired
    private UserDetailsService userDetailsService;

    @Autowired
    private PasswordEncoder passwordEncoder;
    @Autowired
    CustomAuthenticationDetailsSource customAuthenticationDetailsSource;
    @Autowired
    SmsCodeAuthenticationSecurityConfig smsCodeAuthenticationSecurityConfig;
    @Autowired
    MyAuthenticationSuccessHandler myAuthenticationSuccessHandler;

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();   // 使用 BCrypt 加密
    }

    @Bean
    public UserDetailsService userDetailsService() {
        return super.userDetailsService();
    }

    @Bean
    public AuthenticationProvider authenticationProvider() {
        //CustomAuthenticationProvider authenticationProvider = new CustomAuthenticationProvider();
        SmsCodeAuthenticationProvider authenticationProvider = new SmsCodeAuthenticationProvider();
        authenticationProvider.setUserDetailsService(userDetailsService);
        authenticationProvider.setPasswordEncoder(passwordEncoder); // 设置密码加密方式
        return authenticationProvider;
    }

    @Override
    protected void configure(AuthenticationManagerBuilder auth) throws Exception {
        /*auth.inMemoryAuthentication().withUser("dyq").password("123456").roles("user")
                .and().withUser("admin").password("123456").roles("admin");*/
        auth.userDetailsService(userDetailsService);
        auth.authenticationProvider(authenticationProvider());
    }

    @Override
    public void configure(WebSecurity web) throws Exception {
        web.ignoring().mvcMatchers("/static/**");
        //权限控制需要忽略所有静态资源，不然登录页面未登录状态无法加载css等静态资源
    }

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http.headers().frameOptions().disable();//Refused to display 'http://localhost:8080/login' in a frame because it set 'X-Frame-Options' to 'DENY'.
        http.authorizeRequests().antMatchers("/index","/","/login/**","/course/**","/register","/check/**",
                "/favicon.ico","/js/**","/css/**","/semantic/**","/images/**", "/layer/**","/layui/**","/font/**","/video/**","/jQueryShare/**","/videojs/**").permitAll()
                //"/**/*.js","/**/*.css","/**/*.jpg","/**/*.png","/**/*.woff2",
                //.antMatchers("/users/**").hasRole("ADMIN") // 需要相应的角色才能访问
                // 其他地址的访问均需验证权限（需要登录）
                .anyRequest().authenticated()
                .and()
                //基于 Form 表单登录验证
                .formLogin()
                    .loginPage("/login")
                    //设置默认登录成功跳转页面
                    //.defaultSuccessUrl("/index")
                    // 自定义登录界面
                    .failureUrl("/login?error=true")
                    .successForwardUrl("/login/success")//需要post方法
                    //.successHandler(myAuthenticationSuccessHandler)
                    // 可以应对账号登录的ajax登录，无法应对短信登录ajax
                    //需要在SmsCodeAuthenticationSecurityConfig设置
                    .permitAll()
                    .and()
                    .apply(smsCodeAuthenticationSecurityConfig)
                    //在SmsCodeAuthenticationSecurityConfig设置successForwardUrl("/login/success") 方法不行，设置myAuthenticationSuccessHandler
                    //.authenticationDetailsSource(customAuthenticationDetailsSource)//自定义authenticationDetails源，login可以传递验证码
                .and()
                .logout()
                    //默认注销行为为logout，可以通过下面的方式来修改
                    .logoutUrl("/logout")
                    //设置注销成功后跳转页面，默认是跳转到登录页面
                    .logoutSuccessUrl("/index")
                    .permitAll()
                .and()
                //开启cookie保存用户数据
                .rememberMe()
                    //设置cookie有效期
                    .tokenValiditySeconds(60 * 60 * 24 * 7)
                    //设置cookie的私钥
                    .key("www.dengyouquan.cn")
                    .rememberMeCookieName("testpactera");
                //.and().exceptionHandling().accessDeniedPage("/403");  // 处理异常，拒绝访问就重定向到 403 页面
        //只允许一个用户登录,如果同一个账户两次登录,那么第一个账户将被踢下线,跳转到登录页面
        http.sessionManagement().maximumSessions(1).expiredUrl("/login");
    }

    @Override
    @Bean
    public AuthenticationManager authenticationManagerBean() throws Exception {
        return super.authenticationManagerBean();
    }
}
