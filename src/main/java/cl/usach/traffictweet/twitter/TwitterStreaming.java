package cl.usach.traffictweet.twitter;

import cl.usach.traffictweet.models.Category;
import cl.usach.traffictweet.models.*;
import cl.usach.traffictweet.repositories.*;
import cl.usach.traffictweet.utils.Constant;
import cl.usach.traffictweet.utils.MatchCase;
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
import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.List;

@Component
public class TwitterStreaming implements ApplicationRunner {
	private static final int MAX_LAST_TWEETS = 5;

	private CategoryRepository categoryRepository;
	private CommuneRepository communeRepository;
	private MetricRepository metricRepository;
	private CategoryMetricRepository categoryMetricRepository;
	private CommuneMetricRepository communeMetricRepository;

	private final TwitterStream twitterStream;
	private List<String> keywords;
	private List<String> lastTweets;

	@Autowired
	public TwitterStreaming(
			CategoryRepository categoryRepository,
			CommuneRepository communeRepository,
			MetricRepository metricRepository,
			CategoryMetricRepository categoryMetricRepository,
			CommuneMetricRepository communeMetricRepository) {
		this.categoryRepository = categoryRepository;
		this.communeRepository = communeRepository;
		this.metricRepository = metricRepository;
		this.categoryMetricRepository = categoryMetricRepository;
		this.communeMetricRepository = communeMetricRepository;
		this.twitterStream = new TwitterStreamFactory().getInstance();
		this.lastTweets = new ArrayList<>();
		loadKeywords();
	}

	private void loadKeywords() {
		try {
			ClassLoader classLoader = getClass().getClassLoader();
			keywords = IOUtils.readLines(
					classLoader.getResourceAsStream("words.dat"),
					"UTF-8");
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
				Document document = new Document(Constant.TWEET_FIELD, String.valueOf(status.getId()))
						.append(Constant.DATE_FIELD, new Date())
						.append(Constant.USER_FIELD, status.getUser().getScreenName())
						.append(Constant.IMAGE_FIELD, status.getUser().getOriginalProfileImageURL())
						.append(Constant.TEXT_FIELD, status.getText());

				boolean isTheSame = false;
				int i = 0;
				while(!isTheSame && i < lastTweets.size()) {
					String tweet = lastTweets.get(i);
					if(Util.isSameText(status.getText(), tweet)) isTheSame = true;
					i++;
				}

				if(!isTheSame) {
					if(lastTweets.size() == MAX_LAST_TWEETS)
						lastTweets.remove(0);
					lastTweets.add(status.getText());
				}

				MatchCase matchCase = isTheSame ?
						MatchCase.ALREADY_EXISTS :
						Util.match(status.getText(), keywords);
				System.out.println("Tweet, probability of be a valid event: " + matchCase);

				if(matchCase == MatchCase.MATCH) {
					System.out.println("Generating occurrence...");
					document = appendOccurrenceData(document);
					collection = database.getCollection(Constant.EVENTS_COLLECTION);
				} else {
					String collectionName = matchCase == MatchCase.POSSIBLE ?
							Constant.POSSIBLE_COLLECTION :
							Constant.IGNORED_COLLECTION;

					if(database.getCollection(collectionName).count() >= Constant.MAX_IGNORED_TWEETS) {
						System.out.println("Cleaning " + collectionName + " tweets collection...");
						database.getCollection(collectionName).drop();
					}

					collection = database.getCollection(collectionName);
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

	private Document appendOccurrenceData(Document document) {
		List<Category> categories = new ArrayList<>();
		categoryRepository.findAll().forEach(categories::add);
		Iterable<Commune> communes = communeRepository.findAll();
		Iterator<Commune> communeIterator = communes.iterator();

		String text = document.get(Constant.TEXT_FIELD).toString();
		Commune commune, occurrenceCommune = communeIterator.next();
		boolean found = false;
		while ((commune = communeIterator.next()) != null && !found) {
			List<String> keywords = new ArrayList<>();
			keywords.add(Util.clean(commune.getName()));
			for(Street street: commune.getStreets())
				keywords.add(Util.clean(street.getName()));

			if(Util.match(text, keywords) == MatchCase.MATCH) {
				occurrenceCommune = commune;
				found = true;
			}
		}

		document.append(Constant.COMMUNE_FIELD, occurrenceCommune.getName());

		List<String> occurrenceCategories = new ArrayList<String>();
		for (Category category : categories) {
			List<String> keywords = new ArrayList<>();
			for (Keyword keyword : category.getKeywords())
				keywords.add(keyword.getName());

			if (Util.match(text, keywords) == MatchCase.MATCH) {
				occurrenceCategories.add(category.getKey());
				Metric.update(metricRepository, category, occurrenceCommune);
				CategoryMetric.update(categoryMetricRepository,category);
			}
		}

		CommuneMetric.update(communeMetricRepository, occurrenceCommune);
		document.append(Constant.CATEGORIES_FIELD, occurrenceCategories);
		return document;
	}
}
