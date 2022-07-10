package service;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import common.RegexUtils;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.*;

public class MessageService {

    // 后续通过burpsuite配置正则
    private static final String keyRegex = "password|pwd|key|cookie";
    private static final String valueRegex = "^[a-zA-Z0-9_.-]+@[a-zA-Z0-9-]+(\\\\.[a-zA-Z0-9-]+)*\\.[a-zA-Z0-9]{2,6}$";

    public static boolean isSenInfoInUrl(String requestMethod, String requestUrl) {
        if (!"GET".equals(requestMethod.toUpperCase())) {
            return false;
        }
        if (!requestUrl.contains("?")){
            return false;
        }
        int index = requestUrl.indexOf("?");
        String requestParams = requestUrl.substring(index + 1); // 获取get请求的参数
        String[] paramsArray = requestParams.split("&");
        for (String str : paramsArray) {
            String[] tmpArray = str.split("=");
            String paramKey = tmpArray[0];
            String paramValue = tmpArray[1];
            if (RegexUtils.isMatch(paramKey, keyRegex)) { // key中包含敏感信息
                return true;
            } /*else if (RegexUtils.isMatch(paramValue, valueRegex)) { // value中包含敏感信息
                return true;
            }*/
        }
        return false;
    }


    public static boolean isSenInfoInBody(String body) {
        try {
            JSONObject jsonObject = JSON.parseObject(body);
            if (jsonObject.containsKey("password") | jsonObject.containsValue("123456")) {
                return true;
            }
        } catch (Exception e) {
            return RegexUtils.isMatch(body, valueRegex);
        }
        return false;
    }


}
