package com.dyq.demo.authentication.discard;

import org.springframework.security.web.authentication.WebAuthenticationDetails;

import javax.servlet.http.HttpServletRequest;

public class CustomWebAuthenticationDetails extends WebAuthenticationDetails {
    private final String phone;//手机号
    private final String captcha;//验证码
    private final String userCaptcha;//用户填写验证码
    private final String captchaTime;//验证码时间

    /**
     * Records the remote address and will also set the session Id if a session already
     * exists (it won't create one).
     *
     * @param request that the authentication request was received from
     */
    public CustomWebAuthenticationDetails(HttpServletRequest request) {
        super(request);
        phone = request.getParameter("phone");
        captcha = (String) request.getSession().getAttribute(phone);//Session中取
        userCaptcha = request.getParameter("userCaptcha");
        captchaTime = request.getParameter("captchaTime");
        toString();
    }

    public String getCaptcha() {
        return captcha;
    }
    public String getPhone() {
        return phone;
    }
    public String getCaptchaTime() {
        return captchaTime;
    }
    public String getUserCaptcha() {
        return userCaptcha;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append(super.toString()).append("; Captcha: ").append(this.getCaptcha()).append("; phone: ").append(this.getPhone()).append("; userCaptcha: ").append(this.getUserCaptcha()).append("; CaptchaTime: ").append(this.getCaptchaTime());
        return sb.toString();
    }
}
