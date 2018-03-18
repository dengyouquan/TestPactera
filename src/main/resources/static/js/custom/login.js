layui.use('layer', function(){
    var $ = layui.jquery
        ,layer = layui.layer;
    $(".userlogin").click(function () {
        //iframe层
        layer.open({
            type: 2,
            title: '登录',
            shadeClose: true,
            shade: 0.8,
            area: ['450px', '650px'],
            fixed: true,//即鼠标滚动时，层是否固定在可视区域
            content: '/login' //iframe的url
        });
    });

    $(".userreg").click(function () {
        //iframe层
        layer.open({
            type: 2,
            title: '注册',
            shadeClose: true,
            shade: 0.8,
            area: ['450px', '500px'],
            content: '/login/reg' //iframe的url
        });
    });
});