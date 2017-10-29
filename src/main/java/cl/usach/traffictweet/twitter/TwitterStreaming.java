package cl.usach.traffictweet.twitter;

import cl.usach.traffictweet.models.Category;
import cl.usach.traffictweet.models.*;
import cl.usach.traffictweet.repositories.CategoryRepository;
import cl.usach.traffictweet.repositories.CommuneRepository;
import cl.usach.traffictweet.repositories.OccurrenceRepository;
import cl.usach.traffictweet.utils.Constant;
import cl.usach.traffictweet.utils.Util;
import com.mongodb.MongoClient;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import org.apache.commons.io.IOUtils;
import org.bson.Document;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;
import twitter4j.*;

import java.io.IOException;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

@Component
public class TwitterStreaming implements ApplicationRunner {
	private OccurrenceRepository occurrenceRepository;
	private CategoryRepository categoryRepository;
	private CommuneRepository communeRepository;


	private final TwitterStream twitterStream;
	private List<String> keywords;

	@Autowired
	public TwitterStreaming(
			CategoryRepository categoryRepository,
			OccurrenceRepository occurrenceRepository,
			CommuneRepository communeRepository) {
		this.categoryRepository = categoryRepository;
		this.occurrenceRepository = occurrenceRepository;
		this.communeRepository = communeRepository;
		this.twitterStream = new TwitterStreamFactory().getInstance();
		loadKeywords();
	}

	private void loadKeywords() {
		try {
			ClassLoader classLoader = getClass().getClassLoader();
			keywords = IOUtils.readLines(
					classLoader.getResourceAsStream("words.dat"),
					"UTF-8");
			System.out.println(keywords);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public void run(ApplicationArguments args) {
		StatusListener listener = new StatusListener() {

			public void onDeletionNotice(StatusDeletionNotice statusDeletionNotice) {
				System.out.println("Got a status deletion notice id:" + statusDeletionNotice.getStatusId());
			}

			public void onTrackLimitationNotice(int numberOfLimitedStatuses) {
				System.out.println("Got track limitation notice:" + numberOfLimitedStatuses);
			}

			public void onScrubGeo(long userId, long upToStatusId) {
				System.out.println("Got scrub_geo event userId:" + userId + " upToStatusId:" + upToStatusId);
			}

			public void onException(Exception ex) {
				ex.printStackTrace();
			}

			@Override
			public void onStallWarning(StallWarning arg0) {	}

			@Override
			public void onStatus(Status status) {
				MongoClient mongo = new MongoClient(Constant.MONGO_HOST, Constant.MONGO_PORT);

				// Accessing the database
				MongoDatabase database = mongo.getDatabase(Constant.PRODUCTION_DB);
				MongoCollection<Document> collection;

				// Creating document
				System.out.println("Generating document...");
				Document document = new Document(Constant.TWEET_FIELD, status.getId())
						.append(Constant.DATE_FIELD, new Date())
						.append(Constant.USER_FIELD, status.getUser().getScreenName())
						.append(Constant.IMAGE_FIELD, status.getUser().getOriginalProfileImageURL())
						.append(Constant.TEXT_FIELD, status.getText());

				if(Util.match(status.getText(), keywords)) {
					System.out.println("Generating occurrence...");
					int eventId = storeOccurrence(document);
					document.append(Constant.EVENT_FIELD, eventId);

					// Retieving a collection
					collection = database.getCollection(Constant.EVENTS_COLLECTION);
				} else {
					if(database.getCollection(Constant.IGNORED_COLLECTION).count() >= Constant.MAX_IGNORED_TWEETS) {
						System.out.println("Cleaning ignored tweets collection...");
						database.getCollection(Constant.IGNORED_COLLECTION).drop();
						database.createCollection(Constant.IGNORED_COLLECTION);
					}

					// Retieving a collection
					collection = database.getCollection(Constant.IGNORED_COLLECTION);
				}

				collection.insertOne(document);
				System.out.println("Document inserted successfully!");

				mongo.close();
				System.out.println("Connection closed...");
			}
		};

		FilterQuery fq = new FilterQuery()
				.language("es")
				.locations(
						new double[] { -71.7088, -34.2878 },
						new double[] { -69.769, -32.9195 });

		this.twitterStream.addListener(listener);
		this.twitterStream.filter(fq);
	}

	private int storeOccurrence(Document document) {
		List<Category> categories = new ArrayList<>();
		categoryRepository.findAll().forEach(categories::add);
		Iterable<Commune> communes = communeRepository.findAll();

		String user = document.get(Constant.USER_FIELD).toString();
		String image = document.get(Constant.IMAGE_FIELD).toString();
		String text = document.get(Constant.TEXT_FIELD).toString();
		DateFormat dateFormat = new SimpleDateFormat("EEE MMM dd kk:mm:ss z yyyy", Locale.ENGLISH);
		Date date = document.getDate(Constant.DATE_FIELD);

		Commune occurrenceCommune = null;
		for(Commune commune: communes) {
			List<String> keywords = new ArrayList<>();
			keywords.add(Util.clean(commune.getName()));
			for(Street street: commune.getStreets())
				keywords.add(Util.clean(street.getName()));

			if(Util.match(text, keywords)) {
				occurrenceCommune = commune;
				break;
			}
		}

		Occurrence occurrence = occurrenceRepository.save(
				new Occurrence(user, image, text, date, occurrenceCommune));

		for (Category category : categories) {
			List<String> keywords = new ArrayList<>();
			for (Keyword keyword : category.getKeywords())
				keywords.add(keyword.getName());

			if (Util.match(text, keywords)) {
				category.addOccurrence(occurrence);
				occurrence.addCategory(category);
			}
		}

		categoryRepository.save(categories);
		return occurrence.getId();
	}
}
