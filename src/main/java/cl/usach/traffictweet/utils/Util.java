package cl.usach.traffictweet.utils;

import java.util.List;

public class Util {
    public static boolean match(String text, List<String> keywordsList) {
        text = clean(text);
        if(text.startsWith("rt")) return false;
        boolean matches;
        for(String keywordMatch: keywordsList) {
            String[] keywords = keywordMatch.split(" ");
            matches = true;
            for(String keyword: keywords) {
                if(!text.contains(keyword)) {
                    matches = false;
                    break;
                }
            }
            if(matches) return true;
        }
        return false;
    }

    public static String clean(String text) {
        text = text.toLowerCase();
        String replace = "ÁÉÍÓÚÜÑáéíóúüñ";
        String with = "AEIOUUNaeiouun";
        for(int i = 0; i < replace.length(); i++)
            text = text.replace(replace.charAt(i), with.charAt(i));
        return text;
    }
}
