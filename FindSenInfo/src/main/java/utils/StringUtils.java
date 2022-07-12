package utils;

import com.alibaba.fastjson.JSONObject;

public class StringUtils {

    /*
    * 判断是否是json格式的字符串
    * 使用fastjson解析，如果解析失败返回false
    * */
    public static boolean isJsonData(String content) {
        if (content.isEmpty()) {
            return false;
        }
        boolean isJsonObject = true;
        boolean isJsonArray = true;
        try {
            JSONObject.parseObject(content);
        } catch (Exception e) {
            isJsonObject = false;
        }
        try {
            JSONObject.parseArray(content);
        } catch (Exception e) {
            isJsonArray = false;
        }
        return isJsonObject || isJsonArray;
    }



}
