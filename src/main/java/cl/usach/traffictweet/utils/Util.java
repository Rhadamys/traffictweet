package cl.usach.traffictweet.utils;

import java.util.List;

public class Util {
    public static int match(String text, List<String> keywordsList) {
        text = clean(text);
        int possible = 0, keyMatch;

        for(String keywordMatch: keywordsList) {
            String[] keywords = keywordMatch.split(",");
            keyMatch = 0;

            for(String keyword: keywords)
                if(text.contains(keyword)) keyMatch++;

            if(keyMatch == keywords.length)
                return 100;
            else if(keyMatch >= keywords.length / 2)
                possible++;
        }

        return possible * 100 / keywordsList.size();
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
