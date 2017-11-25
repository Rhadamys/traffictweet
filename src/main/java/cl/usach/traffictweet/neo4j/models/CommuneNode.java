package cl.usach.traffictweet.neo4j.models;

import com.fasterxml.jackson.annotation.JsonIgnore;
import org.neo4j.ogm.annotation.NodeEntity;
import org.neo4j.ogm.annotation.Relationship;
import org.springframework.data.annotation.Id;

import javax.persistence.GeneratedValue;
import java.util.ArrayList;
import java.util.List;

@NodeEntity(label = "Commune")
public class CommuneNode {
    public static final String ADJACENT_TO = "ADJACENT_TO";

    @Id
    @GeneratedValue
    private Long id;

    private String name;

    @Relationship(type = ADJACENT_TO)
    @JsonIgnore
    private List<CommuneNode> adjacent = new ArrayList<>();

    @Relationship(type = OccurrenceNode.REPORTED_AT, direction = Relationship.INCOMING)
    @JsonIgnore
    private List<OccurrenceNode> occurrences = new ArrayList<>();

    public CommuneNode() {}

    public CommuneNode(String name) {
        this.name = name;
        this.adjacent = new ArrayList<>();
        this.occurrences = new ArrayList<>();
    }

    public Long getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public List<CommuneNode> getAdjacent() {
        return adjacent;
    }

    public void addAdjacent(CommuneNode communeNode) {
        this.adjacent.add(communeNode);
    }

    public List<OccurrenceNode> getOccurrences() {
        return occurrences;
    }
}
