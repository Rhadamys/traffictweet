package cl.usach.traffictweet.utils;

import java.util.List;

public class Util {
    private static final int CHARS_TO_MATCH = 15;

    public static MatchCase match(String text, List<String> keywordsList) {
        text = clean(text);
        int keyMatch;
        boolean possible = false;

        for(String keywordMatch: keywordsList) {
            String[] keywords = keywordMatch.split(",");
            keyMatch = 0;

            for(String keyword: keywords)
                if(text.contains(keyword)) keyMatch++;

            if(keyMatch == keywords.length)
                return MatchCase.MATCH;
            else if(text.contains(keywords[0]))
                possible = true;
        }

        return possible ? MatchCase.POSSIBLE : MatchCase.UNPROBABLE;
    }

    public static boolean isSameText(String text1, String text2) {
        String sub1 = clean(text1.substring(0, CHARS_TO_MATCH));
        String sub2 = clean(text2.substring(0, CHARS_TO_MATCH));
        return sub1.equals(sub2);
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
