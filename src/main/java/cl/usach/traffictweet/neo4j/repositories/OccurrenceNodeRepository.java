package cl.usach.traffictweet.neo4j.repositories;

import cl.usach.traffictweet.neo4j.models.OccurrenceNode;
import org.springframework.data.neo4j.annotation.Query;
import org.springframework.data.neo4j.repository.Neo4jRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.format.annotation.DateTimeFormat;

import java.util.Date;
import java.util.List;

public interface OccurrenceNodeRepository extends Neo4jRepository<OccurrenceNode, Long> {
    @Query("MATCH (o:OccurrenceNode) WHERE o.millis = {millis} " +
            "MATCH (c:CommuneNode) WHERE (o)-[:REPORTED_AT]->(c) " +
            "MATCH (adj:CommuneNode) WHERE (adj)-[:ADJACENT_TO]->(c) " +
            "MATCH (x:OccurrenceNode) WHERE x <> o AND 0 < o.millis - x.millis <= 10800000 AND " +
            "((x)-[:REPORTED_AT]->(c) OR (x)-[:REPORTED_AT]->(adj)) RETURN x")
    List<OccurrenceNode> findPossibleCauses(@Param("millis") long millis);

    List<OccurrenceNode> findByDateOrderByDateDesc(Date date);

    List<OccurrenceNode> findByDateBetweenOrderByDateDesc(Date from, Date to);

    OccurrenceNode findByTweetId(String tweetId);
}
