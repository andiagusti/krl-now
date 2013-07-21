package org.mufid.krlnow.etc;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by Mufid on 21/07/13.
 */
public class RegexRetrieve {
    /**
     * Retrieve the first matched group in string.
     * @return null if nothing retrieved. The string matched if found.
     */
    public static String first(String input, String regex) {
        Pattern pattern = Pattern.compile(regex);
        Matcher matcher = pattern.matcher(input);

        boolean found = false;
        String result = null;
        if (matcher.find()) {
            return matcher.group(1);
        } else {
            return null;
        }
    }
}
