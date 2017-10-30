package cl.usach.traffictweet.utils;

public class Constant {
    public static final String PRODUCTION_DB = "traffictweet";
    public static final String MONGO_HOST = "localhost";
    public static final int MONGO_PORT = 27017;

    public static final String EVENTS_COLLECTION = "occurrences";
    public static final String POSSIBLE_COLLECTION = "possible";
    public static final String IGNORED_COLLECTION = "ignored";
    public static final int MAX_IGNORED_TWEETS = 1000;
    public static final int POSSIBLE_RATIO = 80;

    public static final String TWEET_FIELD = "tweet_id";
    public static final String DATE_FIELD = "occurrence_date";
    public static final String USER_FIELD = "user";
    public static final String IMAGE_FIELD = "image";
    public static final String TEXT_FIELD = "text";
    public static final String EVENT_FIELD = "event_id";

    public static final String CSV_SPLIT_BY = ";";
}
