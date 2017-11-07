package cl.usach.traffictweet.sql.models;

import cl.usach.traffictweet.sql.repositories.CategoryRepository;
import cl.usach.traffictweet.sql.repositories.CommuneRepository;
import cl.usach.traffictweet.utils.Constant;
import cl.usach.traffictweet.utils.Month;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.mongodb.MongoClient;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import org.bson.Document;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

public class Occurrence {
    private String tweetId;
    private String username;
    private String image;
    private String text;

    @JsonFormat(pattern="dd'/'MM'/'yyyy 'a las' HH:mm")
    private Date date;

    private Commune commune;
    private List<Category> categories;
    
    public Occurrence(String tweetId,
                      String username,
                      String imageUrl,
                      String text,
                      Date date,
                      Commune commune,
                      List<Category> categories) {
        this.tweetId = tweetId;
        this.username = username;
        this.image = imageUrl;
        this.text = text;
        this.date = date;
        this.commune = commune;
        this.categories = categories;
    }

    public String getTweetId() {
        return tweetId;
    }

    public String getUsername() {
        return username;
    }

    public String getImage() {
        return image;
    }

    public String getText() {
        return text;
    }

    public Date getDate() {
        return date;
    }

    public Commune getCommune() {
        return commune;
    }

    public List<Category> getCategories() {
        return categories;
    }

    public static List<Occurrence> getAll(
            CategoryRepository categoryRepository,
            CommuneRepository communeRepository) {
        MongoClient mongo = new MongoClient(Constant.MONGO_HOST, Constant.MONGO_PORT);

        // Accessing the database
        MongoDatabase database = mongo.getDatabase(Constant.PRODUCTION_DB);
        MongoCollection<Document> collection = database.getCollection(Constant.EVENTS_COLLECTION);

        Document sort = new Document(Constant.DATE_FIELD, -1);
        Iterable<Document> documents = collection.find().sort(sort);
        List<Occurrence> occurrences = new ArrayList<>();

        for(Document document: documents)
            occurrences.add(map(categoryRepository, communeRepository, document));

        mongo.close();
        return occurrences;
    }

    public static Occurrence map(
            CategoryRepository categoryRepository,
            CommuneRepository communeRepository,
            Document document) {
        Commune commune = communeRepository.findByName(document.getString(Constant.COMMUNE_FIELD));

        List<Category> categories = new ArrayList<>();
        Object categoriesObject = document.get(Constant.CATEGORIES_FIELD);
        if(categoriesObject != null) {
            if(categoriesObject instanceof String) {
                categories.add(categoryRepository.findByKey(categoriesObject.toString()));
            } else {
                List<String> categoriesList = (ArrayList<String>) categoriesObject;
                for(String categoryKey: categoriesList)
                    categories.add(categoryRepository.findByKey(categoryKey));
            }
        }

        return new Occurrence(
                document.getString(Constant.TWEET_FIELD),
                document.getString(Constant.USER_FIELD),
                document.getString(Constant.IMAGE_FIELD),
                document.getString(Constant.TEXT_FIELD),
                document.getDate(Constant.DATE_FIELD),
                commune,
                categories);
    }
}
