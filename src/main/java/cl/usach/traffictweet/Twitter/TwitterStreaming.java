package cl.usach.traffictweet.Twitter;

import java.io.IOException;
import java.util.HashSet;
import java.util.Set;

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
import com.mongodb.MongoCredential; 

public class TwitterStreaming {
	private final TwitterStream twitterStream;
	private Set<String> keywords;

	private TwitterStreaming() {
		this.twitterStream = new TwitterStreamFactory().getInstance();
		this.keywords = new HashSet<>();
		loadKeywords();
	}

	private void loadKeywords() {
		try {
			ClassLoader classLoader = getClass().getClassLoader();
			keywords.addAll(IOUtils.readLines(classLoader.getResourceAsStream("words.dat"), "UTF-8"));
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
			public void onStallWarning(StallWarning arg0) {

			}

			@Override
			public void onStatus(Status status) {
				MongoClient mongo = new MongoClient( "localhost" , 27017 );

      			// Creating Credentials
				MongoCredential credential = MongoCredential.createCredential("traffic-tweet", "tweetsDB", "password".toCharArray());
				System.out.println("Connected to the database successfully");  
				
				// Accessing the database 
				MongoDatabase database = mongo.getDatabase("tweetsDB");
				//Creating a collection
				if(database.getCollection("tweet") == null)
				{
					database.createCollection("tweet");
				}
				// Retieving a collection
				MongoCollection<Document> tweets = database.getCollection("tweet");
				Document document = new Document("id", status.getId())
					.append("user","@" + status.getUser().getScreenName ())
					.append("location", status.getUser().getLocation())
					.append("image", status.getUser().getOriginalProfileImageURL())
					.append("text", status.getText());

				tweets.insertOne(document); 
				System.out.println("Document inserted successfully");
			}
		};

		FilterQuery fq = new FilterQuery();
		fq.language(new String[]{"es"});
		fq.track(keywords.toArray(new String[0]));

		this.twitterStream.addListener(listener);
		this.twitterStream.filter(fq);
	}
	
	public static void main(String[] args) {
		new TwitterStreaming().init();
	}
}
