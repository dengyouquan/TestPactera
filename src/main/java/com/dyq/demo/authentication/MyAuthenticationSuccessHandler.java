package com.dyq.demo.authentication;

import com.dyq.demo.vo.Response;
import org.json.JSONException;
import org.json.JSONObject;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@Component
public class MyAuthenticationSuccessHandler extends SimpleUrlAuthenticationSuccessHandler {
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response, Authentication auth)
    throws IOException, ServletException {
        String ajaxHeader = ((HttpServletRequest) request).getHeader("X-Requested-With");
        boolean isAjax = "XMLHttpRequest".equals(ajaxHeader);
        if (isAjax) {
            String principal = auth.getPrincipal().toString();
            /*JSONObject returnObj = new JSONObject();
            returnObj.put("status", "1");
            returnObj.put("data", principal);*/
            Response r = new Response(0L,"短信验证码登录成功",0L,true);
            JSONObject returnObj = new JSONObject();
            try {
                returnObj.put("code",0);
                returnObj.put("msg","短信验证码登录成功");
                returnObj.put("count",0);
                returnObj.put("data",true);
            } catch (JSONException e) {
                e.printStackTrace();
            }
            response.getWriter().print(returnObj);
            response.getWriter().flush();
        } else {
            super.onAuthenticationSuccess(request, response, auth);
        }
    }
}