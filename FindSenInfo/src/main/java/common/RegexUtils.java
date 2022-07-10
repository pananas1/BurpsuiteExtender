package common;

import java.util.regex.Pattern;

public class RegexUtils {

    public static boolean isMatch(String str, String regex) {
        return Pattern.matches(regex, str);
    }


}
