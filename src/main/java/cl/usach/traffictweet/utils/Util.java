package cl.usach.traffictweet.utils;

import java.util.List;

public class Util {
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

    public static String clean(String text) {
        text = text.toLowerCase();
        String replace = "ÁÉÍÓÚÜÑáéíóúüñ";
        String with = "AEIOUUNaeiouun";
        for(int i = 0; i < replace.length(); i++)
            text = text.replace(replace.charAt(i), with.charAt(i));
        return text;
    }
}
