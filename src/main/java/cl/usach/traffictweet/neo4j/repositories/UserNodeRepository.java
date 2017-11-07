package cl.usach.traffictweet.neo4j.repositories;

import cl.usach.traffictweet.neo4j.models.UserNode;
import org.springframework.data.neo4j.repository.Neo4jRepository;

public interface UserNodeRepository extends Neo4jRepository<UserNode, Long> {
    UserNode findByUsername(String username);
}
