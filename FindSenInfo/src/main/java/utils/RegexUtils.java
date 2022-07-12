package utils;

import burp.BurpExtender;
import burp.IBurpExtenderCallbacks;

import java.io.PrintWriter;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class RegexUtils {
    public static boolean isMatch(String regex, String content){
        Pattern p = Pattern.compile(regex);
        Matcher m = p.matcher(content);
        return m.find();
    }

//    public static boolean isMatch(String regex,String content){
//        Pattern p = Pattern.compile(regex);
//        Matcher m = p.matcher(content);
//        if (m.find()){
//            return true;
//        }
//        while (m.find()){
//            m.group();
//        }
//        return true;
//    }

}
