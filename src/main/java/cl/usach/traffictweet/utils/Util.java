package cl.usach.traffictweet.utils;

import java.util.*;

public class Util {
    public static MatchCase match(String text, List<String> keywordsList) {
        text = " " + clean(text) + " ";
        int keyMatch;
        boolean possible = false;

        for(String keywordMatch: keywordsList) {
            String[] keywords = keywordMatch.split(Constant.CSV_SPLIT_BY);
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
        ArrayList<String> wordsText1 = new ArrayList<>();
        wordsText1.addAll(Arrays.asList(text1.split(" ")));

        ArrayList<String> wordsText2 = new ArrayList<>();
        wordsText2.addAll(Arrays.asList(text2.split(" ")));
        if(Math.abs(wordsText1.size() - wordsText2.size()) > 3) return false;

        int match = 0;
        for(String word: wordsText1)
            if(wordsText2.contains(word)) match++;

        return (match * 100.0 / wordsText1.size()) >= 80.0;
    }

    public static String clean(String text) {
        text = text.toLowerCase();
        String replace = "ÁÉÍÓÚÜÑáéíóúüñ";
        String with = "AEIOUUNaeiouun";
        for(int i = 0; i < replace.length(); i++)
            text = text.replace(replace.charAt(i), with.charAt(i));
        return text;
    }

    public static String getDateString(Date dateTime) {
        Calendar calendar = Calendar.getInstance();
        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH) + 1;
        int day = calendar.get(Calendar.DAY_OF_MONTH);
        return year + "-" + month + "-" + day;
    }
}
