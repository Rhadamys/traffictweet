package cl.usach.traffictweet.neo4j.models;

import com.fasterxml.jackson.annotation.JsonIgnore;
import org.neo4j.ogm.annotation.NodeEntity;
import org.neo4j.ogm.annotation.Relationship;
import org.springframework.data.annotation.Id;

import javax.persistence.GeneratedValue;
import java.util.ArrayList;
import java.util.List;

@NodeEntity(label = "User")
public class UserNode {
    public static final String REPORTED = "REPORTED";

    @Id
    @GeneratedValue
    private Long id;

    private String username;

    @Relationship(type = REPORTED)
    @JsonIgnore
    private List<OccurrenceNode> reportedOccurrences = new ArrayList<>();

    public UserNode() {}

    public UserNode(String username) {
        this.username = username;
        this.reportedOccurrences = new ArrayList<>();
    }

    public Long getId() {
        return id;
    }

    public String getUsername() {
        return username;
    }

    public List<OccurrenceNode> getReportedOccurrences() {
        return reportedOccurrences;
    }
}
