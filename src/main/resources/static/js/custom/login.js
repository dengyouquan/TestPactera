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
            area: ['450px', '500px'],
            content: '/login' //iframe的url
        });
    });
});