package cl.usach.traffictweet.utils;

import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.TimeZone;

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
        int charsToMatch = Math.min(text1.length(), text2.length()) / 2;
        String sub1 = clean(text1.substring(0, charsToMatch));
        String sub2 = clean(text2.substring(0, charsToMatch));
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

    public static String getDateString(Date dateTime) {
        Calendar calendar = Calendar.getInstance();
        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH) + 1;
        int day = calendar.get(Calendar.DAY_OF_MONTH);
        return year + "-" + month + "-" + day;
    }
}
