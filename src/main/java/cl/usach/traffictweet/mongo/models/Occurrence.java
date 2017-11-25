package cl.usach.traffictweet.mongo.models;

import com.fasterxml.jackson.annotation.JsonFormat;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

import javax.persistence.Id;
import java.util.Date;
import java.util.List;

@Document(collection = "occurrences")
public class Occurrence {
    @Id
    private String id;

    @Field("tweet_id")
    private String tweetId;

    @Field("user")
    private String username;

    private String image;
    private String text;

    @JsonFormat(
            pattern="dd/MM/yyyy' a las 'HH:mm",
            timezone="America/Santiago")
    @Field("occurrence_date")
    private Date date;

    private String commune;
    private List<String> categories;

    public Occurrence() {}

    public Occurrence(String tweetId,
                      String username,
                      String image,
                      String text,
                      Date date,
                      String commune,
                      List<String> categories) {
        this.tweetId = tweetId;
        this.username = username;
        this.image = image;
        this.text = text;
        this.date = date;
        this.commune = commune;
        this.categories = categories;
    }

    public String getId() {
        return id;
    }

    public String getTweetId() {
        return tweetId;
    }

    public String getUsername() {
        return username;
    }

    public String getImage() {
        return image;
    }

    public String getText() {
        return text;
    }

    public Date getDate() {
        return date;
    }

    public String getCommune() {
        return commune;
    }

    public List<String> getCategories() {
        return categories;
    }
}
