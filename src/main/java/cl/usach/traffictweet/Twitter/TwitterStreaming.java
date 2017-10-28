package cl.usach.traffictweet.Twitter;

import java.io.IOException;
import java.util.List;

import cl.usach.traffictweet.Models.Occurrence;
import cl.usach.traffictweet.utils.Constant;
import cl.usach.traffictweet.utils.Util;
import org.apache.commons.io.IOUtils;

import twitter4j.FilterQuery;
import twitter4j.StallWarning;
import twitter4j.Status;
import twitter4j.StatusDeletionNotice;
import twitter4j.StatusListener;
import twitter4j.TwitterStream;
import twitter4j.TwitterStreamFactory;
import com.mongodb.client.MongoCollection; 
import com.mongodb.client.MongoDatabase;

import org.bson.Document; 
import com.mongodb.MongoClient;

public class TwitterStreaming {
	private final TwitterStream twitterStream;
	private List<String> keywords;

	private TwitterStreaming() {
		this.twitterStream = new TwitterStreamFactory().getInstance();
		loadKeywords();
	}

	private void loadKeywords() {
		try {
			ClassLoader classLoader = getClass().getClassLoader();
			keywords = IOUtils.readLines(classLoader.getResourceAsStream("words.dat"), "UTF-8");
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	private void init() {
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

				// Creating document
				Document document = new Document(Constant.TWEET_FIELD, status.getId())
						.append(Constant.USER_FIELD, status.getUser().getScreenName())
						.append(Constant.IMAGE_FIELD, status.getUser().getOriginalProfileImageURL())
						.append(Constant.TEXT_FIELD, status.getText());

				MongoCollection<Document> collection;

				if(Util.match(status.getText(), keywords)) {
					//Creating a collection
					if(database.getCollection(Constant.EVENTS_COLLECTION) == null)
						database.createCollection(Constant.EVENTS_COLLECTION);

					/*
					int eventId = Occurrence.addOccurrence(
							status.getUser().getScreenName(),
							status.getUser().getOriginalProfileImageURL(),
							status.getText());

					document.append(Constant.EVENT_FIELD, eventId);*/

					// Retieving a collection
					collection = database.getCollection(Constant.EVENTS_COLLECTION);
				} else {
					//Creating a collection
					if(database.getCollection(Constant.IGNORED_COLLECTION) == null)
						database.createCollection(Constant.IGNORED_COLLECTION);

					// Retieving a collection
					collection = database.getCollection(Constant.IGNORED_COLLECTION);
				}

				collection.insertOne(document);
				System.out.println("Document inserted successfully");
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

	public static void main(String[] args) {
		new TwitterStreaming().init();
	}
}
