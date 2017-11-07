package cl.usach.traffictweet.sql.services;

import cl.usach.traffictweet.sql.models.Occurrence;
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
    private static final Document SORT_BY_DATE_DESC = new Document(Constant.DATE_FIELD, -1);

    @Autowired
    private CategoryRepository categoryRepository;

    @Autowired
    private CommuneRepository communeRepository;

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
        MongoClient mongo = new MongoClient(Constant.MONGO_HOST, Constant.MONGO_PORT);
        MongoDatabase database = mongo.getDatabase(Constant.PRODUCTION_DB);
        MongoCollection<Document> collection = database.getCollection(Constant.EVENTS_COLLECTION);

        Document filter = new Document(Constant.TWEET_FIELD, tweetId);
        Document document = collection.find(filter).first();

        Occurrence occurrence = Occurrence.map(categoryRepository, communeRepository, document);

        mongo.close();
        return occurrence;
    }

    /**
     * Get all occurrences by category.
     * @param categoryType Category.
     * @return All occurrences that match the category.
     */
    @RequestMapping(value = "/type", method = RequestMethod.GET, params = "category")
    @ResponseBody
    public List<Occurrence> findByCategory(@RequestParam("category") String categoryType) {
        MongoClient mongo = new MongoClient(Constant.MONGO_HOST, Constant.MONGO_PORT);
        MongoDatabase database = mongo.getDatabase(Constant.PRODUCTION_DB);
        MongoCollection<Document> collection = database.getCollection(Constant.EVENTS_COLLECTION);

        Document filter = new Document(Constant.CATEGORIES_FIELD, categoryType);
        Iterable<Document> documents = collection.find(filter).sort(SORT_BY_DATE_DESC);

        List<Occurrence> occurrences =  new ArrayList<>();
        for (Document document: documents)
            occurrences.add(Occurrence.map(categoryRepository, communeRepository, document));

        mongo.close();
        return occurrences;
    }

    /**
     * Get all occurrence by commune.
     * @param communeName CommuneNode.
     * @return All occurrences that match the commune.
     */
    @RequestMapping(
            value = "/type",
            method = RequestMethod.GET,
            params = "commune")
    @ResponseBody
    public List<Occurrence> findByCommune(@RequestParam("commune") String communeName) {
        MongoClient mongo = new MongoClient(Constant.MONGO_HOST, Constant.MONGO_PORT);
        MongoDatabase database = mongo.getDatabase(Constant.PRODUCTION_DB);
        MongoCollection<Document> collection = database.getCollection(Constant.EVENTS_COLLECTION);

        Document filter = new Document(Constant.COMMUNE_FIELD, communeName);
        Iterable<Document> documents = collection.find(filter).sort(SORT_BY_DATE_DESC);

        List<Occurrence> occurrences =  new ArrayList<>();
        for (Document document: documents)
            occurrences.add(Occurrence.map(categoryRepository, communeRepository, document));

        mongo.close();
        return occurrences;
    }

    @RequestMapping(
            method = RequestMethod.GET,
            params = "search")
    @ResponseBody
    public List<Occurrence> search(@RequestParam("search") String search) {
        List<org.apache.lucene.document.Document> hits = Lucene.search(search);

        MongoClient mongo = new MongoClient(Constant.MONGO_HOST, Constant.MONGO_PORT);
        MongoDatabase database = mongo.getDatabase(Constant.PRODUCTION_DB);
        MongoCollection<Document> collection = database.getCollection(Constant.EVENTS_COLLECTION);

        List<Occurrence> occurrences =  new ArrayList<>();
        for (org.apache.lucene.document.Document hit: hits) {
            Document filter = new Document(Constant.TWEET_FIELD, hit.get(Constant.TWEET_FIELD));
            Document document = collection.find(filter).first();
            occurrences.add(Occurrence.map(categoryRepository, communeRepository, document));
        }

        mongo.close();
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

        MongoClient mongo = new MongoClient(Constant.MONGO_HOST, Constant.MONGO_PORT);
        MongoDatabase database = mongo.getDatabase(Constant.PRODUCTION_DB);
        MongoCollection<Document> collection = database.getCollection(Constant.EVENTS_COLLECTION);

        Document dates = new Document("$gte", from).append("$lt", calendar.getTime());
        Document filter = new Document(Constant.DATE_FIELD, dates);
        Iterable<Document> documents = collection.find(filter).sort(SORT_BY_DATE_DESC);

        List<Occurrence> occurrences =  new ArrayList<>();
        for (Document document: documents)
            occurrences.add(Occurrence.map(categoryRepository, communeRepository, document));

        mongo.close();
        return occurrences;
    }
}
