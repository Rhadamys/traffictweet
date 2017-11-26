package cl.usach.traffictweet.neo4j;

import cl.usach.traffictweet.mongo.models.Occurrence;
import cl.usach.traffictweet.mongo.repositories.OccurrenceRepository;
import cl.usach.traffictweet.neo4j.models.CommuneNode;
import cl.usach.traffictweet.neo4j.models.OccurrenceNode;
import cl.usach.traffictweet.neo4j.models.UserNode;
import cl.usach.traffictweet.neo4j.repositories.CommuneNodeRepository;
import cl.usach.traffictweet.neo4j.repositories.OccurrenceNodeRepository;
import cl.usach.traffictweet.neo4j.repositories.UserNodeRepository;
import cl.usach.traffictweet.sql.models.Commune;
import cl.usach.traffictweet.sql.repositories.CommuneRepository;
import cl.usach.traffictweet.utils.Constant;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

@Component
public class Neo4j implements ApplicationRunner {
    private final static Logger LOGGER = Logger.getLogger(Neo4j.class.getName());

    private static OccurrenceNodeRepository occurrenceNodeRepository;
    private static OccurrenceRepository occurrenceRepository;
    private static CommuneNodeRepository communeNodeRepository;
    private static UserNodeRepository userNodeRepository;
    private static CommuneRepository communeRepository;

    @Autowired
    public Neo4j(
            OccurrenceNodeRepository occurrenceNodeRepository,
            OccurrenceRepository occurrenceRepository,
            CommuneNodeRepository communeNodeRepository,
            UserNodeRepository userNodeRepository,
            CommuneRepository communeRepository) {
        Neo4j.occurrenceNodeRepository = occurrenceNodeRepository;
        Neo4j.occurrenceRepository = occurrenceRepository;
        Neo4j.communeNodeRepository = communeNodeRepository;
        Neo4j.userNodeRepository = userNodeRepository;
        Neo4j.communeRepository = communeRepository;
    }

    public void run(ApplicationArguments args) {
        LOGGER.log(Level.INFO,"Getting beans repositories...");
    }

    public static void seed() {
        LOGGER.log(Level.INFO,"Running neo4j module...");

        userNodeRepository.deleteAll();
        communeNodeRepository.deleteAll();
        occurrenceNodeRepository.deleteAll();

        Iterable<Commune> communes = communeRepository.findAll();
        HashMap<String, CommuneNode> communeNodes = new HashMap<>();
        // Create communes nodes
        for(Commune commune: communes) {
            if(commune.getName().equals(Constant.UNKNOWN_COMMUNE)) continue;
            CommuneNode communeNode = new CommuneNode(commune.getName());
            communeNode = communeNodeRepository.save(communeNode);
            communeNodes.put(communeNode.getName(), communeNode);
        }

        // Create relationship ADJACENT_TO
        for(Commune commune: communes) {
            if(commune.getName().equals(Constant.UNKNOWN_COMMUNE)) continue;
            CommuneNode communeNode = communeNodes.get(commune.getName());
            for(Commune adjacent: commune.getAdjacentCommunes())
                communeNode.addAdjacent(communeNodes.get(adjacent.getName()));
            communeNodeRepository.save(communeNode);
        }

        Iterable<Occurrence> occurrences = occurrenceRepository.findAllByOrderByDateAsc();
        LOGGER.log(Level.INFO,"Creating nodes...");
        for(Occurrence occurrence : occurrences)
            insertNode(occurrence);

        LOGGER.log(Level.INFO,"Neo4j module finished successfully!");
    }
    
    public static void insertNode(Occurrence occurrence) {
        if(occurrence.getCommune().equals(Constant.UNKNOWN_COMMUNE))
            LOGGER.log(Level.INFO,"Node has unknown commune. Will be ignored...");
        else {
            LOGGER.log(Level.INFO,"Creating node...");
            String tweetId = occurrence.getTweetId();
            String username = occurrence.getUsername();
            String text = occurrence.getText();
            String commune = occurrence.getCommune();
            Date date = occurrence.getDate();
            Long millis = date.getTime();

            // User wich reported the occurrence
            UserNode userNode = userNodeRepository.findByUsername(username);
            if(userNode == null) userNode = new UserNode(username);

            // Commune where occurrence was reported
            CommuneNode communeNode = communeNodeRepository.findByName(commune);

            // Create occurrence node
            OccurrenceNode occurrenceNode = new OccurrenceNode(
                    tweetId, date, millis, text, communeNode, userNode);
            occurrenceNodeRepository.save(occurrenceNode);

            // Possible occurrences that may cause this occurrence
            List<OccurrenceNode> dueTo = occurrenceNodeRepository.findPossibleCauses(millis);
            occurrenceNode.setDueTo(dueTo);
            occurrenceNodeRepository.save(occurrenceNode);

            LOGGER.log(Level.INFO,"Relationships created successfully!");
            LOGGER.log(Level.INFO,"Node created successfully!");
        }
    }
}