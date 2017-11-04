package cl.usach.traffictweet.services;

import cl.usach.traffictweet.models.Occurrence;
import cl.usach.traffictweet.repositories.CategoryRepository;
import cl.usach.traffictweet.repositories.CommuneRepository;
import cl.usach.traffictweet.utils.Constant;
import com.mongodb.MongoClient;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import org.bson.Document;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.query.BasicQuery;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@RestController
@CrossOrigin
@RequestMapping("/occurrences")
public class OccurrenceService {
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
        return Occurrence.getAll(categoryRepository, communeRepository);
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

        // Accessing the database
        MongoDatabase database = mongo.getDatabase(Constant.PRODUCTION_DB);
        MongoCollection<Document> collection = database.getCollection(Constant.EVENTS_COLLECTION);

        Document filter = new Document(Constant.TWEET_FIELD, tweetId);
        Document document = collection.find(filter).first();
        return Occurrence.map(categoryRepository, communeRepository, document);
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
        // Accessing the database
        MongoDatabase database = mongo.getDatabase(Constant.PRODUCTION_DB);
        MongoCollection<Document> collection = database.getCollection(Constant.EVENTS_COLLECTION);
        Document filter = new Document(Constant.CATEGORIES_FIELD, categoryType);
        Document sort = new Document(Constant.DATE_FIELD, -1);
        Iterable<Document> documents = collection.find(filter).sort(sort);
        List<Occurrence> occurrencesType =  new ArrayList<>();
        for (Document document: documents)
            occurrencesType.add(Occurrence.map(categoryRepository, communeRepository, document));
        return occurrencesType;
    }

    /**
     * Get all occurrence by commune.
     * @param communeName Commune.
     * @return All occurrences that match the commune.
     */
    @RequestMapping(value = "/type", method = RequestMethod.GET, params = "commune")
    @ResponseBody
    public List<Occurrence> findByCommune(@RequestParam("commune") String communeName) {
        MongoClient mongo = new MongoClient(Constant.MONGO_HOST, Constant.MONGO_PORT);
        // Accessing the database
        MongoDatabase database = mongo.getDatabase(Constant.PRODUCTION_DB);
        MongoCollection<Document> collection = database.getCollection(Constant.EVENTS_COLLECTION);
        Document filter = new Document(Constant.COMMUNE_FIELD, communeName);
        Document sort = new Document(Constant.DATE_FIELD, -1);
        Iterable<Document> documents = collection.find(filter).sort(sort);
        List<Occurrence> occurrencesType =  new ArrayList<>();
        for (Document document: documents)
            occurrencesType.add(Occurrence.map(categoryRepository, communeRepository, document));
        return occurrencesType;
    }


    /**
     * Get an occurrence by category.
     * @param from Date, to Date.
     * @return All occurrences that match the category.
     */
    /*@RequestMapping(method = RequestMethod.GET, params = {"from", "to"})
    @ResponseBody
    public List<Occurrence> findByCommune(@RequestParam("from") @DateTimeFormat(pattern="yyyy-MM-dd") Date from,
                                          @RequestParam("to") @DateTimeFormat(pattern="yyyy-MM-dd") Date to) {
        MongoClient mongo = new MongoClient(Constant.MONGO_HOST, Constant.MONGO_PORT);
        // Accessing the database
        MongoDatabase database = mongo.getDatabase(Constant.PRODUCTION_DB);
        MongoCollection<Document> collection = database.getCollection(Constant.EVENTS_COLLECTION);
        Document filter = new Document("date", from).append("date", to);
        Iterable<Document> documents = collection.find(filter);
        List<Occurrence> occurrencesType =  new ArrayList<>();
        for (Document document: documents)
            occurrencesType.add(Occurrence.map(categoryRepository, communeRepository, document));
        return occurrencesType;
    }*/

    /*
    @RequestMapping(
            value = "/today",
            method = RequestMethod.GET)
    @ResponseBody
    public Iterable<Occurrence> getAllOfToday() {
        Date now = new Date();

        Calendar calendar = Calendar.getInstance();
        calendar.setTime(now);
        calendar.set(Calendar.HOUR, 0);
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.SECOND, 0);

        return occurrenceRepository.findByDateBetweenOrderByDateDesc(calendar.getTime(), now);
    }

    @RequestMapping(
            value = "/between",
            method = RequestMethod.GET)
    @ResponseBody
    public Iterable<Occurrence> getAllBetween(
            @RequestParam("from") @DateTimeFormat(pattern="yyyy-MM-dd") Date from,
            @RequestParam("to") @DateTimeFormat(pattern="yyyy-MM-dd") Date to) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(to);
        calendar.set(Calendar.HOUR, 23);
        calendar.set(Calendar.MINUTE, 59);
        calendar.set(Calendar.SECOND, 59);
        return occurrenceRepository.findByDateBetweenOrderByDateDesc(from, calendar.getTime());
    }


    @RequestMapping(value = "/{id}", method = RequestMethod.GET)
    @ResponseBody
    public Occurrence findOne(@PathVariable("id") Integer id) {
        return occurrenceRepository.findOne(id);
    }

    @RequestMapping(value = "/{id}/categories", method = RequestMethod.GET)
    @ResponseBody
    public Set<Category> findCategories(@PathVariable("id") Integer id) {
        return occurrenceRepository.findOne(id).getCategories();
    }

    @RequestMapping(
            value = "/{occurrenceID}/categories/{categoryID}",
            method = RequestMethod.POST)
    @ResponseBody
    public Set<Category> addCategory(
            @PathVariable("occurrenceID") Integer occurrenceID,
            @PathVariable("categoryID") Integer categoryID,
            HttpServletResponse httpResponse) {
        if(!occurrenceRepository.exists(occurrenceID)) {
            httpResponse.setStatus(HttpStatus.NOT_FOUND.value());
            return null;
        }
        Occurrence occurrence = occurrenceRepository.findOne(occurrenceID);

        for(Category occurrencesCategory: occurrence.getCategories()) {
            if(occurrencesCategory.getId() == categoryID) {
                httpResponse.setStatus(HttpStatus.NOT_MODIFIED.value());
                return null;
            }
        }

        if(!categoryRepository.exists(categoryID)) {
            httpResponse.setStatus(HttpStatus.NOT_FOUND.value());
            return null;
        }
        Category category = categoryRepository.findOne(categoryID);

        occurrence.addCategory(category);
        category.addOccurrence(occurrence);
        categoryRepository.save(category);
        httpResponse.setStatus(HttpStatus.CREATED.value());
        return occurrenceRepository.save(occurrence).getCategories();
    }

    @RequestMapping(method = RequestMethod.POST)
    @ResponseStatus(HttpStatus.CREATED)
    @ResponseBody
    public Occurrence create(@RequestBody Occurrence resource) {
        return occurrenceRepository.save(resource);
    }
*/
}
