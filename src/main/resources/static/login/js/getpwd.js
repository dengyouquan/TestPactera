
function checkPhone(phone){
    var status = true;
    if (phone == '') {
        $('.num2-err').removeClass('hide').find("em").text('请输入手机号');
        return false;
    }
    var param = /^1[34578]\d{9}$/;
    if (!param.test(phone)) {
        // globalTip({'msg':'手机号不合法，请重新输入','setTime':3});
        $('.num2-err').removeClass('hide');
        $('.num2-err').text('手机号不合法，请重新输入');
        return false;
    }
    // 获取CSRF Token
    var csrfToken = $("meta[name='_csrf']").attr("content");
    var csrfHeader = $("meta[name='_csrf_header']").attr("content");
    $.ajax({
        url: '/check/checkPhone',
        type: 'post',
        dataType: 'json',
        async: false,
        data: {phone:phone,type:"login"},
        beforeSend: function(request) {
            request.setRequestHeader(csrfHeader, csrfToken); // 添加CSRF Token
        },
        success:function(data){
            //已注册
            if (data.code == '0') {
                $('.pc_tel-err').addClass('hide');
                // console.log('aa');
                // return true;
            } else {
                //未注册
                $('.pc_tel-err').removeClass('hide').text(data.msg);
                // console.log('bb');
                status = false;
                // return false;
            }
        },
        error:function(){
            status = false;
            // return false;
        }
    });
    return status;
}

function checkCode(code){
    if (code == '') {
        $('.error').removeClass('hide').text('请输入验证码');
        return false;
    } else {
        $('.error').addClass('hide');
        return true;
    }
}

function checkPass(passport,passport2){
    if(passport==""){
        $('.confirmpwd-err').addClass('hide');
        $('.pwd-err').removeClass('hide').text('请输入密码');
        return false;
    }
    else if (passport2=="") {
        $('.pwd-err').addClass('hide');
        $('.confirmpwd-err').removeClass('hide').text('请输入确认密码');
        return false;
    }
    else if (passport != passport2) {
        $('.pwd-err').addClass('hide');
        $('.confirmpwd-err').removeClass('hide').text('确认密码不一致');
        return false;
    } else {
        //密码强度正则，最少6位，包括至少1个大写字母，1个小写字母，1个数字，1个特殊字符
        //var param = /^.*(?=.{6,})(?=.*\d)(?=.*[A-Z])(?=.*[a-z])(?=.*[!@#$%^&*? ]).*$/;
        var param = /^\w{6,16}$/;
        if(!param.test(passport)) {
            $('.pwd-err').removeClass('hide').text('密码应在6-16个字符之间');
            return false;
        }
        $('.confirmpwd-err').addClass('hide');
        $('.pwd-err').addClass('hide');
        return true;
    }
}

function getPwdPhone(phone,pcode) {
    // 获取CSRF Token
    var csrfToken = $("meta[name='_csrf']").attr("content");
    var csrfHeader = $("meta[name='_csrf_header']").attr("content");

    $.ajax({
        url: '/login/phone',
        type: 'post',
        dataType: 'json',
        async: true,
        data: {phone:phone,pcode:pcode},
        beforeSend: function(request) {
            request.setRequestHeader(csrfHeader, csrfToken); // 添加CSRF Token
        },
        success:function(data){
            if(data.data==false){
                //与服务器不相等
                $('.error').removeClass('hide').text('验证码错误');
            }else{
                $('.error').addClass('hide');
                $(".p-input-1").addClass('hide');
                $(".p-input-2").removeClass('hide');
                $("#next-button").addClass('hide');
                $("#modify-button").removeClass('hide');
            }
        },
        error:function(){

        }
    });
}

function modifyPwd(phone,passport) {
    // 获取CSRF Token
    var csrfToken = $("meta[name='_csrf']").attr("content");
    var csrfHeader = $("meta[name='_csrf_header']").attr("content");

    $.ajax({
        url: '/login/modifyPwd',
        type: 'post',
        dataType: 'json',
        async: true,
        data: {phone:phone,password:passport},
        beforeSend: function(request) {
            request.setRequestHeader(csrfHeader, csrfToken); // 添加CSRF Token
        },
        success:function(data){
            if(data.data==false){
                layer.msg("修改密码失败");
            }else{
                layer.msg("修改密码成功");
                window.location.href="/login"
            }
        },
        error:function(){

        }
    });
}

$('#pc_tel').keyup(function(event) {
    $('.tel-warn').addClass('hide');
    var phone = $('#pc_tel').val();
    if(checkPhone(phone)){
        $(".p-input-1").css("display","block");
    }
});

$("#verifyImg").click(function () {
    $(this).attr('src', '/login/verifyCode?'+Math.random());
});

//得到密码点击事件
function sendBtn(){
    $("#next-button").click(function(){
        var phone = $.trim($('#pc_tel').val());
        var pcode = $.trim($('#veri-code').val());
        if (checkCode(pcode)) {
            getPwdPhone(phone,pcode);
        }
    });
    $("#modify-button").click(function(){
        var phone = $.trim($('#pc_tel').val());
        var passport = $.trim($('#passport').val());
        var passport2 = $.trim($('#passport2').val());
        if (checkPass(passport,passport2)) {
            //alert("修改密码")
            modifyPwd(phone,passport);
        }
    });
}

$(function () {
    $(".form-data").delegate(".send","click",function () {
        var phone = $.trim($('#pc_tel').val());
        if (checkPhone(phone)) {
            // 获取CSRF Token
            var csrfToken = $("meta[name='_csrf']").attr("content");
            var csrfHeader = $("meta[name='_csrf_header']").attr("content");
            $.ajax({
                url: '/login/phoneCode',
                type: 'post',
                dataType: 'json',
                async: true,
                data: {phone:phone,type:"login"},
                beforeSend: function(request) {
                    request.setRequestHeader(csrfHeader, csrfToken); // 添加CSRF Token
                },
                success:function(data){
                    if (data.code == '0') {

                    } else {

                    }
                },
                error:function(){

                }
            });
            var oTime = $(".form-data .time"),
                oSend = $(".form-data .send"),
                num = parseInt(oTime.text()),
                oEm = $(".form-data .time em");
            $(this).hide();
            oTime.removeClass("hide");
            var timer = setInterval(function () {
                var num2 = num-=1;
                oEm.text(num2);
                if(num2==0){
                    clearInterval(timer);
                    oSend.text("重新发送验证码");
                    oSend.show();
                    oEm.text("120");
                    oTime.addClass("hide");
                }
            },1000);
        }
    });
    sendBtn();
});

