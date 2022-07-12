package utils;

import com.alibaba.fastjson.JSONObject;
import org.junit.jupiter.api.Test;

import java.io.IOException;

import static org.junit.jupiter.api.Assertions.*;

class RegexUtilsTest {
    FileUtils fileUtils = new FileUtils();
    RegexUtils regexUtils = new RegexUtils();
    @Test
    public void regex1(){
        String str = "private static void readerMethod(File file) throws IOException {\n" +
                "        FileReader fileReader = new FileReader(file);\n" +
                "        Reader reader = new InputStreamReader(new FileInputStream(file), \"Utf-8\");\n" +
                "        int ch = 0;\n" +
                "        StringBuffer sb = new Str,869609268@qq.com,ingBuffer();\n" +
                "        while ((ch = reader.read()) != -1) {\n" +
                "            sb.append((char) ch);\n" +
                "        }\n" +
                "        fileReader.close();\n" +
                "        reader.close();\n" +
                "        String jsonStr = sb.toString();\n" +
                "        System.out.println(JSON.parseObject(jsonStr));\n" +
                "    }\n";

        boolean match = regexUtils.isMatch("([\\w-]+(?:\\.[\\w-]+)*@(?:[\\w](?:[\\w-]*[\\w])?\\.)+[\\w](?:[\\w-]*[\\w])?)", str);
        System.out.println(match);

    }

}