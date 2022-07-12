package utils;

import com.alibaba.fastjson.JSONObject;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

class FileUtilsTest {
    FileUtils fileUtils = new FileUtils();
    @Test
    public void readFile(){
        Map<String, Object> map = fileUtils.readYaml("src/main/resources/config.json");
        for (String key:map.keySet()){
            System.out.println(key);
            System.out.println(map.get(key));
        }
    }

    @Test
    public void readFile2(){
        Map<String,Object> jsonObject = null;
        try {
            jsonObject = fileUtils.readJson("src/main/resources/config.json");
        } catch (IOException e) {
            e.printStackTrace();
        }

        System.out.println(jsonObject.toString());
    }

}