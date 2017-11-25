package cl.usach.traffictweet.neo4j.services;

import cl.usach.traffictweet.mongo.models.Occurrence;
import cl.usach.traffictweet.neo4j.models.CommuneNode;
import cl.usach.traffictweet.neo4j.models.OccurrenceNode;
import cl.usach.traffictweet.neo4j.models.UserNode;
import cl.usach.traffictweet.neo4j.repositories.OccurrenceNodeRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

@RestController
@CrossOrigin
@RequestMapping("/nodes/occurrences")
public class OccurrenceNodeService {
    @Autowired
    private OccurrenceNodeRepository occurrenceNodeRepository;

    @RequestMapping(method = RequestMethod.GET)
    @ResponseBody
    public List<OccurrenceNode> getAll() {
        return occurrenceNodeRepository.findByDateOrderByDateDesc(new Date());
    }

    @RequestMapping(
            method = RequestMethod.GET,
            params = {"from", "to"})
    @ResponseBody
    public List<OccurrenceNode> getBetween(
            @RequestParam("from") @DateTimeFormat(pattern = "yyyy-MM-dd") Date from,
            @RequestParam("to") @DateTimeFormat(pattern = "yyyy-MM-dd") Date to) {
        return occurrenceNodeRepository.findByDateBetweenOrderByDateDesc(from, to);
    }

    @RequestMapping(
            value = "/{tweetId}",
            method = RequestMethod.GET)
    @ResponseBody
    public HashMap<String, Object> getByTweetId(@PathVariable("tweetId") String tweetId) {
        OccurrenceNode occurrenceNode = occurrenceNodeRepository.findByTweetId(tweetId);
        if(occurrenceNode == null) return null;

        ArrayList<Object> nodes = new ArrayList<>();
        ArrayList<HashMap<String, Object>> links = new ArrayList<>();

        nodes.add(occurrenceNode);
        nodes.add(occurrenceNode.getCommune());
        nodes.add(occurrenceNode.getReportedBy());

        links.add(getLink(0, 1, OccurrenceNode.REPORTED_AT));
        links.add(getLink(2, 0, UserNode.REPORTED));

        for(OccurrenceNode cause: occurrenceNode.getDueTo()) {
            cause = occurrenceNodeRepository.findByTweetId(cause.getTweetId());
            CommuneNode communeNode = cause.getCommune();
            UserNode userNode = cause.getReportedBy();

            int causeIndex = nodes.size();
            nodes.add(cause);
            links.add(getLink(0, causeIndex, OccurrenceNode.DUE_TO));

            if(nodes.contains(communeNode)) {
                links.add(getLink(causeIndex, nodes.indexOf(communeNode), OccurrenceNode.REPORTED_AT));
            } else {
                nodes.add(communeNode);
                links.add(getLink(causeIndex, causeIndex + 1, OccurrenceNode.REPORTED_AT));

                int communeIndex = nodes.size() - 1;
                for(CommuneNode adjacent: communeNode.getAdjacent()) {
                    if(!nodes.contains(adjacent)) continue;
                    int adjacentIndex = nodes.indexOf(adjacent);
                    links.add(getLink(communeIndex, adjacentIndex, CommuneNode.ADJACENT_TO));
                }
            }

            if(nodes.contains(userNode)) {
                links.add(getLink(nodes.indexOf(userNode), causeIndex, UserNode.REPORTED));
            } else {
                nodes.add(userNode);
                links.add(getLink(nodes.size() - 1, causeIndex, UserNode.REPORTED));
            }
        }

        HashMap<String, Object> graph = new HashMap<>();
        graph.put("nodes", nodes);
        graph.put("links", links);
        return graph;
    }

    private HashMap<String, Object> getLink(int source, int target, String type) {
        HashMap<String, Object> link = new HashMap<>();
        link.put("source", source);
        link.put("target", target);
        link.put("type", type);
        return link;
    }
}
