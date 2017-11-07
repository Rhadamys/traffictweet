package cl.usach.traffictweet.neo4j.repositories;

import cl.usach.traffictweet.neo4j.models.CommuneNode;
import org.springframework.data.neo4j.repository.Neo4jRepository;

public interface CommuneNodeRepository extends Neo4jRepository<CommuneNode, Long> {
    CommuneNode findByName(String name);
}
