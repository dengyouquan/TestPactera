layui.use(['layer','carousel'], function() {
    var $ = layui.jquery
        ,layer = layui.layer
        ,carousel = layui.carousel;

    //轮播
    carousel.render({
        elem: '#carousel'
        ,width: '100%' //设置容器宽度
        ,height: '500px' //设置容器宽度
        ,arrow: 'always' //始终显示箭头
        //,anim: 'updown' //切换动画方式
    });

    //jQuery
    $(function () {
        $(".courseItem").click(function () {
            window.location.href="/course";
        });
    });
});
