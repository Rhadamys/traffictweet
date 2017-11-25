package cl.usach.traffictweet.mongo.services;

import cl.usach.traffictweet.mongo.models.Occurrence;
import cl.usach.traffictweet.mongo.repositories.OccurrenceRepository;
import cl.usach.traffictweet.sql.models.Category;
import cl.usach.traffictweet.sql.models.Commune;
import cl.usach.traffictweet.sql.repositories.CategoryRepository;
import cl.usach.traffictweet.sql.repositories.CommuneRepository;
import cl.usach.traffictweet.twitter.Lucene;
import cl.usach.traffictweet.utils.Constant;
import com.mongodb.MongoClient;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import org.bson.Document;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.*;

import java.util.*;

@RestController
@CrossOrigin
@RequestMapping("/occurrences")
public class OccurrenceService {
    @Autowired
    private OccurrenceRepository occurrenceRepository;

    /**
     * Return all occurrences.
     * @return All occurrences.
     */
    @RequestMapping(method = RequestMethod.GET)
    @ResponseBody
    public List<Occurrence> getAll() {
        Date now = new Date();
        Calendar calendar = Calendar.getInstance(TimeZone.getTimeZone("America/Santiago"));
        calendar.setTime(now);
        calendar.set(Calendar.HOUR, 0);
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.SECOND, 0);
        return findBetweenDates(calendar.getTime(), now);
    }

    /**
     * Get an occurrence by tweet ID.
     * @param tweetId Tweet ID.
     * @return An occurrence matching given tweet ID.
     */
    @RequestMapping(
            value = "/{tweetId}",
            method = RequestMethod.GET)
    @ResponseBody
    public Occurrence getTweetById(@PathVariable("tweetId") String tweetId) {
        return occurrenceRepository.findByTweetId(tweetId);
    }

    /**
     * Get all occurrences by category.
     * @param categoryKey Category.
     * @return All occurrences that match the category.
     */
    @RequestMapping(
            method = RequestMethod.GET,
            params = "category")
    @ResponseBody
    public List<Occurrence> findByCategory(@RequestParam("category") String categoryKey) {
        return occurrenceRepository.findAllByCategoriesContainsOrderByDateDesc(categoryKey);
    }

    /**
     * Get all occurrence by commune.
     * @param communeName CommuneNode.
     * @return All occurrences that match the commune.
     */
    @RequestMapping(
            method = RequestMethod.GET,
            params = "commune")
    @ResponseBody
    public List<Occurrence> findByCommune(@RequestParam("commune") String communeName) {
        return occurrenceRepository.findAllByCommuneOrderByDateDesc(communeName);
    }

    @RequestMapping(
            method = RequestMethod.GET,
            params = "search")
    @ResponseBody
    public List<Occurrence> search(@RequestParam("search") String search) {
        List<org.apache.lucene.document.Document> hits = Lucene.search(search);

        List<Occurrence> occurrences =  new ArrayList<>();
        for (org.apache.lucene.document.Document hit: hits) {
            Occurrence occurrence = occurrenceRepository.findByTweetId(hit.get(Constant.TWEET_FIELD));
            occurrences.add(occurrence);
        }

        occurrences.sort((o1, o2) -> -o1.getDate().compareTo(o2.getDate()));
        return occurrences;
    }

    /**
     * Get an occurrence by category.
     * @param from Date
     * @param to Date.
     * @return All occurrences that match the category.
     */
    @RequestMapping(
            method = RequestMethod.GET,
            params = {"from", "to"})
    @ResponseBody
    public List<Occurrence> findBetweenDates(
            @RequestParam("from") @DateTimeFormat(pattern="yyyy-MM-dd") Date from,
            @RequestParam("to") @DateTimeFormat(pattern="yyyy-MM-dd") Date to) {
        Calendar calendar = Calendar.getInstance(TimeZone.getTimeZone("America/Santiago"));
        calendar.setTime(to);
        calendar.set(Calendar.HOUR_OF_DAY, 23);
        calendar.set(Calendar.MINUTE, 59);
        calendar.set(Calendar.SECOND, 59);
        return occurrenceRepository.findAllByDateBetweenOrderByDateDesc(from, calendar.getTime());
    }
}
