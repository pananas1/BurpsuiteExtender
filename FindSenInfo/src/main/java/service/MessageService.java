package service;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import common.RegexConfig;
import common.Rules;
import utils.FileUtils;
import utils.RegexUtils;

import java.io.IOException;
import java.util.Map;

import static utils.RegexUtils.isMatch;

public class MessageService {

    private static final String keyRegex = "password|pwd|key|cookie";

    public static boolean isSenInfoInUrl(String requestMethod, String requestUrl) {
        if (!"GET".equals(requestMethod.toUpperCase())) {
            return false;
        }
        if (!requestUrl.contains("?")) {
            return false;
        }
        int index = requestUrl.indexOf("?");
        String requestParams = requestUrl.substring(index + 1); // 获取get请求的参数
        String[] paramsArray = requestParams.split("&");
        for (String str : paramsArray) {
            String[] tmpArray = str.split("=");
            String paramKey = tmpArray[0];
            String paramValue = tmpArray[1];
            if (isMatch(paramKey, keyRegex)) { // key中包含敏感信息
                return true;
            }
        }
        return false;
    }



    public static boolean isSenInfoInBody(String body) {
        boolean hasSenInfo;
        // 加载到burpsuite上时，读取文件会出异常，不知道原因，
//            Map<String, Object> map = FileUtils.readJson("src/main/resources/config.json");
        Map<String,Object> map = JSON.parseObject(RegexConfig.regexJson);
        for (String key:map.keySet()){
            String regex = map.get(key).toString();
            hasSenInfo = isMatch(regex, body);
            if (hasSenInfo){
                return true;
            }
        }
        return false;
    }


}
