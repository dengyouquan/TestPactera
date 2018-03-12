package com.dyq.demo.Controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class LoginController {
    @GetMapping("/login/reg")
    public String reg(){
        return "login/layer/reg";
    }
    @GetMapping("/login")
    public String login(){
        return "login/layer/login";
    }
    @GetMapping("/login/getpwd")
    public String getpwd(){
        return "login/layer/getpwd";
    }
    @GetMapping("/login/protocol")
    public String protocol(){
        return "login/protocol";
    }
}
