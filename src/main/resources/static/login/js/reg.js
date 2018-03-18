function checkPhone(phone){
    var status = true;
    if (phone == '') {
        $('.tel-err').removeClass('hide').find("em").text('请输入手机号');
        return false;
    }
    var param = /^1[34578]\d{9}$/;
    if (!param.test(phone)) {
        // globalTip({'msg':'手机号不合法，请重新输入','setTime':3});
        $('.tel-err').removeClass('hide');
        $('.tel-err').text('手机号不合法，请重新输入');
        return false;
    }
    return status;
}

function checkPhoneJava(phone){
    var status = true;
    if (phone == '') {
        $('.tel-err').removeClass('hide').find("em").text('请输入手机号');
        return false;
    }
    var param = /^1[34578]\d{9}$/;
    if (!param.test(phone)) {
        // globalTip({'msg':'手机号不合法，请重新输入','setTime':3});
        $('.tel-err').removeClass('hide');
        $('.tel-err').text('手机号不合法，请重新输入');
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
            if (data.code == '0') {
                $('.tel-err').removeClass('hide').text("此手机号已注册");
                status = false;
            } else {
                $('.tel-err').addClass('hide');
            }
        },
        error:function(){
            status = false;
            // return false;
        }
    });
    return status;
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

function checkUserName(username){
    var status = true;
    if(username==""){
        $('.username-err').removeClass('hide').text('请输入用户名');
        return false;
    }
    var param = /^[a-zA-Z]\w*$/;
    if (!param.test(username)) {
        $('.username-err').removeClass('hide').text('请以字母开头，可包含字母，数字或下划线');
        return false;
    }
    $('.username-err').addClass('hide');

    // 获取CSRF Token
    var csrfToken = $("meta[name='_csrf']").attr("content");
    var csrfHeader = $("meta[name='_csrf_header']").attr("content");
    $.ajax({
        url: '/check/checkUsername',
        type: 'post',
        dataType: 'json',
        async: false,
        data: {username:username,type:"login"},
        beforeSend: function(request) {
            request.setRequestHeader(csrfHeader, csrfToken); // 添加CSRF Token
        },
        success:function(data){
            if (data.code == '0') {
                $('.username-err').removeClass('hide').text(data.msg);
                status = false;
            } else {
                $('.username-err').addClass('hide');
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

function checkProtocol(){
    var value = $("#protocol").val();
    if (value == '0') {
        $('.protocol-err').removeClass('hide').text('请同意协议');
        return false;
    } else {
        $('.protocol-err').addClass('hide');
        return true;
    }
}

function alertLayer(bol) {
    //示范一个公告层
    if(bol) {
        layer.open({
            type: 1
            ,
            title: false //不显示标题栏
            ,
            closeBtn: false
            ,
            area: '300px;'
            ,
            shade: 0.8
            ,
            id: 'LAY_layuipro' //设定一个id，防止重复弹出
            ,
            btn: ['速去登录', '我再等等']
            ,
            btnAlign: 'c'
            ,
            moveType: 1 //拖拽模式，0或者1
            ,
            content: '<div style="padding: 50px; line-height: 22px; background-color: #393D49; color: #fff; font-weight: 300;">你已经注册成功！</div>'
            ,
            success: function (layero) {
                var btn = layero.find('.layui-layer-btn');
                btn.find('.layui-layer-btn0').attr({
                    href: '/login'
                    , target: '_self'
                });
            }
        });
    }
    else{
        layer.msg("注册失败");
    }
}

function regVerifyPhone(senddata,phone,pcode) {
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
                register(senddata);
            }
        },
        error:function(){

        }
    });
}

function register(senddata) {
    // 获取CSRF Token
    var csrfToken = $("meta[name='_csrf']").attr("content");
    var csrfHeader = $("meta[name='_csrf_header']").attr("content");

    $.ajax({
        url: '/login/register',
        type: 'post',
        dataType: 'json',
        async: true,
        data: senddata,
        beforeSend: function(request) {
            request.setRequestHeader(csrfHeader, csrfToken); // 添加CSRF Token
        },
        success:function(data){
            //弹出层
            alertLayer(data.data);
        },
        error:function(){

        }
    });
}

// 注册点击事件
function sendBtn(){
    $(".lang-btn").click(function(){
        // var type = 'phone';
        var phone = $.trim($('#tel').val());
        var pcode = $.trim($('#veri-code').val());
        var passport = $.trim($('#passport').val());
        var passport2 = $.trim($('#passport2').val());
        var username = $.trim($('#username').val());
        if (checkPhone(phone) && checkUserName(username) && checkPass(passport,passport2) && checkCode(pcode) && checkProtocol()) {
            var senddata = {tel:phone,pcode:pcode,password:passport,username:username};
            regVerifyPhone(senddata,phone,pcode);
        }
    });
}

$(function () {
    $(".form-data").delegate(".send","click",function () {
        var phone = $.trim($('#tel').val());
        if (checkPhoneJava(phone)) {
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

