package cl.usach.traffictweet.neo4j.services;

import cl.usach.traffictweet.neo4j.models.OccurrenceNode;
import cl.usach.traffictweet.neo4j.repositories.OccurrenceNodeRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.Date;
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
    public OccurrenceNode getByTweetId(@PathVariable("tweetId") String tweetId) {
        return occurrenceNodeRepository.findByTweetId(tweetId);
    }

    @RequestMapping(
            value = "/{tweetId}/causes",
            method = RequestMethod.GET)
    @ResponseBody
    public List<OccurrenceNode> getCausesByTweetId(@PathVariable("tweetId") String tweetId) {
        OccurrenceNode occurrenceNode = getByTweetId(tweetId);
        if(occurrenceNode == null || occurrenceNode.getDueTo() == null) return null;

        List<OccurrenceNode> mappedCauses = new ArrayList<>();
        for(OccurrenceNode cause: occurrenceNode.getDueTo())
            mappedCauses.add(occurrenceNodeRepository.findByTweetId(cause.getTweetId()));
        return mappedCauses;
    }
}
