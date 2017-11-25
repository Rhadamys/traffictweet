package cl.usach.traffictweet.neo4j.models;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnore;
import org.hibernate.annotations.Fetch;
import org.hibernate.annotations.FetchMode;
import org.neo4j.ogm.annotation.NodeEntity;
import org.neo4j.ogm.annotation.Relationship;
import org.springframework.data.annotation.Id;
import org.springframework.format.annotation.DateTimeFormat;

import javax.persistence.GeneratedValue;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@NodeEntity(label = "Occurrence")
public class OccurrenceNode {
    public static final String REPORTED_AT = "REPORTED_AT";
    public static final String DUE_TO = "DUE_TO";

    @Id
    @GeneratedValue
    private Long id;

    private String tweetId;

    @Temporal(TemporalType.DATE)
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    @JsonFormat(
            pattern = "dd/MM/yyyy' a las 'HH:mm",
            timezone = "America/Santiago")
    private Date date;

    private long millis;
    private String text;

    @Relationship(type = DUE_TO)
    @JsonIgnore
    private List<OccurrenceNode> dueTo = new ArrayList<>();

    @Relationship(type = REPORTED_AT)
    private CommuneNode commune;

    @Relationship(type = UserNode.REPORTED, direction = Relationship.INCOMING)
    private UserNode reportedBy;

    public OccurrenceNode() {
    }

    public OccurrenceNode(
            String tweetId,
            Date date,
            long millis,
            String text,
            CommuneNode commune,
            UserNode reportedBy) {
        this.tweetId = tweetId;
        this.date = date;
        this.millis = millis;
        this.text = text;
        this.commune = commune;
        this.reportedBy = reportedBy;
        this.dueTo = new ArrayList<>();
    }

    public Long getId() {
        return id;
    }

    public String getTweetId() {
        return tweetId;
    }

    public Date getDate() {
        return date;
    }

    public long getMillis() {
        return millis;
    }

    public String getText() {
        return text;
    }

    public List<OccurrenceNode> getDueTo() {
        return dueTo;
    }

    public void setDueTo(List<OccurrenceNode> dueTo) {
        this.dueTo = dueTo;
    }

    public CommuneNode getCommune() {
        return commune;
    }

    public UserNode getReportedBy() {
        return reportedBy;
    }
}
