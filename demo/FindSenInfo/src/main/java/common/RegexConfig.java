package common;

import java.util.Map;

public class RegexConfig {
    public static String regexJson = "{\n" +
            "  \"phone\":\"[^0-9]+(1[3-9]\\\\zd{9})[^0-9]+\",\n" +
            "  \"email\":\"([\\\\w-]+(?:\\\\.[\\\\w-]+)*@(?:[\\\\w](?:[\\\\w-]*[\\\\w])?\\\\.)+[\\\\w](?:[\\\\w-]*[\\\\w])?)\"\n" +
            "}";
}
