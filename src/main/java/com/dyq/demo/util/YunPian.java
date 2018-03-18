package com.dyq.demo.util;

import java.util.HashMap;
import java.util.Map;

public class YunPian {

    /**单条短信发送,智能匹配短信模板
     * @param apikey成功注册后登录云片官网,进入后台可查看
     * @param text需要使用已审核通过的模板或者默认模板
     * @param mobile接收的手机号,仅支持单号码发送
     * @return json格式字符串
     */

    public static String singleSend(String apikey, String text, String mobile) {
        Map<String, String> params = new HashMap<String, String>();
        params.put("apikey", apikey);
        params.put("text", text);
        params.put("mobile", mobile);
        String paramsStr="apikey="+apikey+"&text="+text+"&mobile"+mobile;
        return HttpRequest.sendPost("https://sms.yunpian.com/v2/sms/single_send.json", paramsStr);
    }
}
