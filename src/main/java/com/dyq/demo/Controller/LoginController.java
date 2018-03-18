package com.dyq.demo.Controller;

import com.dyq.demo.domain.Authority;
import com.dyq.demo.domain.User;
import com.dyq.demo.service.AuthorityService;
import com.dyq.demo.service.UserService;
import com.dyq.demo.vo.Response;
import com.google.code.kaptcha.impl.DefaultKaptcha;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.*;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.WebAttributes;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;

import javax.imageio.ImageIO;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import javax.validation.ConstraintViolationException;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.util.HashSet;
import java.util.Optional;
import java.util.Set;

@Controller
public class LoginController {
    @Autowired
    DefaultKaptcha defaultKaptcha;
    @Autowired
    UserService userService;
    @Autowired
    AuthorityService authorityService;
    @Autowired
    @Qualifier("authenticationManagerBean")
    AuthenticationManager authenticationManager;

    @GetMapping("/login/reg")
    public String reg(){
        return "login/reg";
    }

    @PostMapping("/login/register")
    public ResponseEntity<Response> phone(HttpServletRequest httpServletRequest,HttpServletResponse httpServletResponse,User user) throws Exception{
        System.out.println("/login/register");
        Set<Authority> authorities = new HashSet<>();
        Long authorityId=2L;
        authorities.add(authorityService.getAuthorityById(authorityId).get());
        user.setAuthorities(authorities);
        user.setAvatar("/images/avatar/large/elliot.jpg");
        user.setName("文思小白");
        if(user.getId() == null) {
            user.setEncodePassword(user.getPassword()); // 加密密码
        }else {
            User originalUser = userService.getUserById(user.getId()).get();
            //重新赋值create_at
            user.setCreatedAt(originalUser.getCreatedAt());
            // 判断密码是否做了变更
            String rawPassword = originalUser.getPassword();
            PasswordEncoder encoder = new BCryptPasswordEncoder();
            String encodePasswd = encoder.encode(user.getPassword());
            boolean isMatch = encoder.matches(rawPassword, encodePasswd);
            if (!isMatch) {
                user.setEncodePassword(user.getPassword());
            }else {
                user.setPassword(user.getPassword());
            }
        }
        System.out.println("save:"+user.toString()+",userid:"+user.getId());
        try {
            userService.saveUser(user);
        }  catch (ConstraintViolationException e)  {
            e.printStackTrace();
            return ResponseEntity.ok().body(new Response(1L,"ok",0L,false));
        }
        System.out.println("user:"+user);
        return ResponseEntity.ok().body(new Response(0L,"ok",0L,true));
    }


    @GetMapping("/login")
    public String login(Model model,HttpServletRequest request,
                        @RequestParam(value = "error",required = false) boolean error){
        String header = request.getHeader("X-Requested-With");
        boolean isAjax = "XMLHttpRequest".equals(header);
        //登录失败
        if(error){
            System.out.println("faillogin isAjax:"+isAjax);
            if(isAjax){
                return "forward:/loginAjax?error=true";
            }else{
                model.addAttribute("loginError", true);
                model.addAttribute("errorMsg", "登陆失败，账号或者密码错误！");
            }
            HttpSession httpSession = request.getSession();
            Object e = httpSession.getAttribute(WebAttributes.AUTHENTICATION_EXCEPTION);
            if(e instanceof LockedException){
                httpSession.setAttribute(WebAttributes.AUTHENTICATION_EXCEPTION,new LockedException("用户输入密码错误3次，已被锁定，请联系管理员解锁"));
            }
            else if(e instanceof InternalAuthenticationServiceException){
                httpSession.setAttribute(WebAttributes.AUTHENTICATION_EXCEPTION,new InternalAuthenticationServiceException("该用户不存在"));
            }
            else if(e instanceof BadCredentialsException){
                httpSession.setAttribute(WebAttributes.AUTHENTICATION_EXCEPTION,new BadCredentialsException("用户名或密码错误"));
            }
        }
        if(isAjax){
            System.out.println("successlogin isAjax:"+isAjax);
            return "forward:/loginAjax";
        }else{
            return "login/login";
        }
    }

    @PostMapping("login/modifyPwd")
    public ResponseEntity<Response> modifyPwd(HttpServletRequest request) {
        //得到请求参数
        String phone = request.getParameter("phone");
        String password = request.getParameter("password");
        System.out.println("login/modifyPwd==phone:"+phone+",password:"+password);
        User user = userService.findByTel(phone).orElse(null);
        if(user!=null){
            user.setEncodePassword(password);
            userService.saveUser(user);
            return ResponseEntity.ok().body(new Response(0L,"修改密码成功",0L,true));
        }else{
            return ResponseEntity.ok().body(new Response(0L,"修改密码失败",0L,false));
        }
    }

    //修改密码
    @PostMapping("login/success")
    public String loginSuccess(HttpServletRequest request) {
        String header = request.getHeader("X-Requested-With");
        boolean isAjax = "XMLHttpRequest".equals(header);
        if(isAjax){
            System.out.println("login/success isAjax:"+isAjax);
            return "forward:/loginAjax";//不能转到get loginAjax url 必须加一个post loginAjax
        }else{
            return "/index";
        }
    }

    @PostMapping("loginAjax")
    public ResponseEntity<Response> loginAjaxPost(@RequestParam(value = "error",required = false) boolean error,HttpServletRequest request) {
        System.out.println("loginAjax:error:"+error);
        HttpSession httpSession = request.getSession();
        Object o = null;
        String str = null;
        if(httpSession!=null){
            o = httpSession.getAttribute(WebAttributes.AUTHENTICATION_EXCEPTION);
            if(o!=null)
                str = o.toString();
        }
        return ResponseEntity.ok().body(new Response(0L,str==null?"ok":str,0L,error));
    }

    @GetMapping("loginAjax")
    public ResponseEntity<Response> loginAjax(@RequestParam(value = "error",required = false) boolean error,HttpServletRequest request) {
        System.out.println("loginAjax:error:"+error);
        HttpSession httpSession = request.getSession();
        Object o = null;
        String str = null;
        if(httpSession!=null){
            o = httpSession.getAttribute(WebAttributes.AUTHENTICATION_EXCEPTION);
            if(o!=null)
                str = o.toString();
        }
        return ResponseEntity.ok().body(new Response(0L,str==null?"ok":str,0L,error));
    }

    @PostMapping("/check/checkPhone")
    public ResponseEntity<Response> checkPhone(HttpServletRequest httpServletRequest,HttpServletResponse httpServletResponse){
        //得到请求参数
        String phone = httpServletRequest.getParameter("phone");
        //查询是否有这个手机号
        Optional<User> userOptional = userService.findByTel(phone);
        User user = null;
        if(userOptional.isPresent()){
            user = userOptional.get();
        }
        if(user!=null){
            System.out.println("此手机号已注册:"+phone);
            return ResponseEntity.ok().body(new Response(0L,"此手机号已注册",0L,true));
        }else{
            System.out.println("此手机号未注册>"+phone);
            return ResponseEntity.ok().body(new Response(1L,"此手机号未注册",0L,false));
        }
    }

    @PostMapping("/check/checkUsername")
    public ResponseEntity<Response> checkUsername(HttpServletRequest httpServletRequest,HttpServletResponse httpServletResponse){
        //得到请求参数
        String username = httpServletRequest.getParameter("username");
        //查询是否有这个用户名
        User user = userService.findByUsername(username);
        if(user!=null){
            System.out.println("此用户名已存在:"+username);
            return ResponseEntity.ok().body(new Response(0L,"此用户名已存在",0L,true));
        }else{
            System.out.println("此用户名不存在>"+username);
            return ResponseEntity.ok().body(new Response(1L,"此用户名不存在",0L,false));
        }
    }


    @PostMapping("/check/checkEmail")
    public ResponseEntity<Response> checkEmail(HttpServletRequest httpServletRequest,HttpServletResponse httpServletResponse){
        //得到请求参数
        String email = httpServletRequest.getParameter("email");
        //查询是否有这个手机号
        Optional<User> userOptional = userService.findByEmail(email);
        User user = null;
        if(userOptional.isPresent()){
            user = userOptional.get();
        }
        if(user!=null){
            System.out.println("此邮箱已注册:"+email);
            return ResponseEntity.ok().body(new Response(0L,"此邮箱已注册",0L,true));
        }else{
            System.out.println("此邮箱未注册>"+email);
            return ResponseEntity.ok().body(new Response(1L,"此邮箱未注册",0L,false));
        }
    }


    @PostMapping("/login/phoneCode")
    public ResponseEntity<Response> phoneCode(HttpServletRequest httpServletRequest,HttpServletResponse httpServletResponse){
        String phoneCode = "111111";
        //得到请求参数
        String phone = httpServletRequest.getParameter("phone");
        httpServletRequest.getSession().setAttribute(phone, phoneCode);
        System.out.println("phone:"+phone+",phoneCode"+httpServletRequest.getSession().getAttribute(phone));
        return ResponseEntity.ok().body(new Response(0L,"ok",0L,true));
    }

    @PostMapping("/login/phone")
    public ResponseEntity<Response> phone(HttpServletRequest httpServletRequest,HttpServletResponse httpServletResponse) throws Exception{
        System.out.println("/login/phone");
        //得到参数
        String phone = httpServletRequest.getParameter("phone");
        String pcode = httpServletRequest.getParameter("pcode");
        //从session中得到
        String phoneCode = (String) httpServletRequest.getSession().getAttribute(phone);
        User user = (User) httpServletRequest.getSession().getAttribute(phone+"user");
        System.out.println("Session  pcode:"+pcode+" phone:"+phone+" form phoneCode:"+phoneCode+",user"+user);
        /*Optional<User> userOptional = userService.findByTel(phone);
        User user = null;
        if(userOptional.isPresent()){
            user = userOptional.get();
        }*/
        if (pcode.equals(phoneCode)) {
            return ResponseEntity.ok().body(new Response(0L,"ok",0L,true));
        } else {
            return ResponseEntity.ok().body(new Response(0L,"ok",0L,false));
        }
    }

    @PostMapping("/login/custom")
    public ResponseEntity<Response> loginCustom(HttpServletRequest httpServletRequest) {
        //得到参数
        String phone = httpServletRequest.getParameter("phone");
        String pcode = httpServletRequest.getParameter("pcode");
        String username = httpServletRequest.getParameter("username");
        String password = httpServletRequest.getParameter("password");
        String email = httpServletRequest.getParameter("email");
        System.out.println("loginCustom:phone:"+phone+",pcode:"+pcode+",username:"+username+",password:"+password+",email:"+email);
        //查询是否有这个手机号
        Optional<User> userOptional = userService.findByTel(phone);
        User user = null;
        if(userOptional.isPresent()){
            user = userOptional.get();
            if(user!=null){
                System.out.println("手机登录成功");
            }
        }
        if(user==null && StringUtils.isEmpty(email)){
            //查询是否有这个邮箱号
            user = userService.findByEmail(email).get();
            String originPwd = user.getPassword();
            user.setEncodePassword(password);
            if(user.getPassword().equalsIgnoreCase(originPwd)){
                //密码正确
                System.out.println("邮箱登录成功");
            }else{
                user = null;
            }
        }

        if(user==null && StringUtils.isEmpty(username)){
            //查询是否有这个用户名
            user = userService.findByUsername(username);
            String originPwd = user.getPassword();
            user.setEncodePassword(password);
            if(user.getPassword().equalsIgnoreCase(originPwd)){
                //密码正确
                System.out.println("用户名登录成功");
            }else{
                user = null;
            }
        }
        System.out.println(user);
        if(user!=null){
            //手动登录
            UsernamePasswordAuthenticationToken token = new UsernamePasswordAuthenticationToken(user.getUsername(), user.getPassword(),user.getAuthorities());
            System.out.println("token:"+token);
            Authentication authentication = authenticationManager.authenticate(token);
            System.out.println("authentication:"+authentication);
            SecurityContextHolder.getContext().setAuthentication(authentication);
            System.out.println("login/custom======>:");
            System.out.println("user:"+user);
            System.out.println("spring secury user:"+(User)SecurityContextHolder.getContext().getAuthentication().getPrincipal());
            return ResponseEntity.ok().body(new Response(0L,"登录成功",0L,true));
        }else{
            return ResponseEntity.ok().body(new Response(1L,"登录失败",0L,false));
        }
    }

    @GetMapping("/login/verifyCode")
    public void verifyCode(HttpServletRequest httpServletRequest,HttpServletResponse httpServletResponse) throws Exception{
        byte[] captchaChallengeAsJpeg = null;
        ByteArrayOutputStream jpegOutputStream = new ByteArrayOutputStream();
        try {
            //生产验证码字符串并保存到session中
            String createText = defaultKaptcha.createText();
            System.out.println("verifyCode:"+createText);
            httpServletRequest.getSession().setAttribute("verifyCode", createText);
            //使用生产的验证码字符串返回一个BufferedImage对象并转为byte写入到byte数组中
            BufferedImage challenge = defaultKaptcha.createImage(createText);
            ImageIO.write(challenge, "jpg", jpegOutputStream);
        } catch (IllegalArgumentException e) {
            httpServletResponse.sendError(HttpServletResponse.SC_NOT_FOUND);
            return;
        }

        //定义response输出类型为image/jpeg类型，使用response输出流输出图片的byte数组
        captchaChallengeAsJpeg = jpegOutputStream.toByteArray();
        httpServletResponse.setHeader("Cache-Control", "no-store");
        httpServletResponse.setHeader("Pragma", "no-cache");
        httpServletResponse.setDateHeader("Expires", 0);
        httpServletResponse.setContentType("image/jpeg");
        ServletOutputStream responseOutputStream =
                httpServletResponse.getOutputStream();
        responseOutputStream.write(captchaChallengeAsJpeg);
        responseOutputStream.flush();
        responseOutputStream.close();
    }


    @GetMapping("/login/verify")
    public ResponseEntity<Response> verify(HttpServletRequest httpServletRequest,HttpServletResponse httpServletResponse){
        String captchaId = (String) httpServletRequest.getSession().getAttribute("verifyCode");
        String parameter = httpServletRequest.getParameter("verifyCode");
        System.out.println("Session  verifyCode "+captchaId+" form verifyCode "+parameter);
        if (!captchaId.equals(parameter)) {
            return ResponseEntity.ok().body(new Response(0L,"ok",0L,false));
        } else {
            return ResponseEntity.ok().body(new Response(0L,"ok",0L,true));
        }
    }

    @GetMapping("/login/getpwd")
    public String getpwd(){
        return "login/getpwd";
    }
    @GetMapping("/login/protocol")
    public String protocol(){
        return "login/protocol";
    }


    //废弃区
    /*@PostMapping("/login/custom")
    public ResponseEntity<Response> loginCustom(HttpServletRequest httpServletRequest) {
        //得到参数
        String phone = httpServletRequest.getParameter("phone");
        String pcode = httpServletRequest.getParameter("pcode");
        String username = httpServletRequest.getParameter("username");
        String password = httpServletRequest.getParameter("password");
        String email = httpServletRequest.getParameter("email");
        System.out.println("loginCustom:phone:"+phone+",pcode:"+pcode+",username:"+username+",password:"+password+",email:"+email);
        //查询是否有这个手机号
        Optional<User> userOptional = userService.findByTel(phone);
        User user = null;
        if(userOptional.isPresent()){
            user = userOptional.get();
            if(user!=null){
                System.out.println("手机登录成功");
            }
        }
        if(user==null && StringUtils.isEmpty(email)){
            //查询是否有这个邮箱号
            user = userService.findByEmail(email).get();
            String originPwd = user.getPassword();
            user.setEncodePassword(password);
            if(user.getPassword().equalsIgnoreCase(originPwd)){
                //密码正确
                System.out.println("邮箱登录成功");
            }else{
                user = null;
            }
        }

        if(user==null && StringUtils.isEmpty(username)){
            //查询是否有这个用户名
            user = userService.findByUsername(username);
            String originPwd = user.getPassword();
            user.setEncodePassword(password);
            if(user.getPassword().equalsIgnoreCase(originPwd)){
                //密码正确
                System.out.println("用户名登录成功");
            }else{
                user = null;
            }
        }
        System.out.println(user);
        if(user!=null){
            //手动登录
            UsernamePasswordAuthenticationToken token = new UsernamePasswordAuthenticationToken(user.getUsername(), user.getPassword());
            System.out.println("token:"+token);
            Authentication authentication = authenticationManager.authenticate(token);
            System.out.println("authentication:"+authentication);
            SecurityContextHolder.getContext().setAuthentication(authentication);
            System.out.println("login/custom======>:");
            System.out.println("user:"+user);
            System.out.println("spring secury user:"+(User)SecurityContextHolder.getContext().getAuthentication().getPrincipal());
            return ResponseEntity.ok().body(new Response(0L,"登录成功",0L,true));
        }else{
            return ResponseEntity.ok().body(new Response(1L,"登录失败",0L,false));
        }
    }*/
}
