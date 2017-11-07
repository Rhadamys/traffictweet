package cl.usach.traffictweet.neo4j.models;

import com.fasterxml.jackson.annotation.JsonIgnore;
import org.neo4j.ogm.annotation.NodeEntity;
import org.neo4j.ogm.annotation.Relationship;
import org.springframework.data.annotation.Id;

import javax.persistence.GeneratedValue;
import java.util.ArrayList;
import java.util.List;

@NodeEntity
public class CommuneNode {
    public static final String ADJACENT_TO = "ADJACENT_TO";

    @Id
    @GeneratedValue
    Long id;

    String name;

    @Relationship(type = ADJACENT_TO)
    @JsonIgnore
    List<CommuneNode> adjacent;

    @Relationship(type = OccurrenceNode.REPORTED_AT, direction = Relationship.INCOMING)
    @JsonIgnore
    List<OccurrenceNode> occurrenceNodes;

    public CommuneNode() {}

    public CommuneNode(String name) {
        this.name = name;
        this.adjacent = new ArrayList<>();
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

    public List<OccurrenceNode> getOccurrenceNodes() {
        return occurrenceNodes;
    }
}
