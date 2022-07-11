package common;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class JsonUtils {

    private final Map<String, Object> messageMap = new HashMap<>();
    private final ArrayList<String> messageList = new ArrayList<>();

    private static final Logger log = LogManager.getLogger(JsonUtils.class);

    /**
     * 将json格式的数据转换成Map或者List
     * */
    public Object parseJsonStr(String jsonStr, String resultType) {
        Map<String, Object> messageMap = new HashMap<>();
        ArrayList<String> messageList = new ArrayList<>();
        if (jsonStr.startsWith("[")) {
            JSONArray jsonArray = JSON.parseArray(jsonStr);
            if ("map".equals(resultType)) {
                for (int i = 0; i < jsonArray.size(); i++) {
                    String str = jsonArray.getJSONObject(i).toString();
                    messageMap = parseJsonStrToMap(str);
                }
                return messageMap;
            } else {
                for (int i = 0; i < jsonArray.size(); i++) {
                    String str = jsonArray.getJSONObject(i).toString();
                    messageList = parseJsonStrToList(str);
                }
                return messageList;
            }

        } else {
            if ("map".equals(resultType)) {
                return parseJsonStrToMap(jsonStr);
            } else {
                return parseJsonStrToList(jsonStr);
            }
        }

    }

    /**
     * 将Json格式的字符串转换成key-value的形式，返回一个map
     */
    public Map<String, Object> parseJsonStrToMap(String jsonStr) {
        Map<String, Object> parseObject = JSON.parseObject(jsonStr);
        for (String key : parseObject.keySet()) {
            String tempValue = parseObject.get(key).toString();
            try {
                parseJsonStrToMap(tempValue);
                messageMap.put(key, "value");
//                messageMap.put(key, tempValue);
            } catch (Exception e) {
                messageMap.put(key, tempValue);
            }
        }
        return messageMap;
    }

    /**
     * 将Json格式的字符串转换成List，形如[key,value,key,value......]
     */
    public ArrayList<String> parseJsonStrToList(String jsonStr) {
        Map<String, Object> parseObject = JSON.parseObject(jsonStr);
        for (String key : parseObject.keySet()) {
            String tempValue = parseObject.get(key).toString();
            try {
                parseJsonStrToList(tempValue);
                messageList.add(key);
                messageList.add("value");
            } catch (Exception e) {
                messageList.add(key);
                messageList.add(tempValue);
            }
        }
        return messageList;
    }


}
