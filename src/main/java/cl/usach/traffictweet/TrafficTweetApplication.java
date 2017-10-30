package cl.usach.traffictweet;
import cl.usach.traffictweet.utils.Constant;
import com.mongodb.MongoClient;
import com.mongodb.client.MongoDatabase;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import org.springframework.context.annotation.aspectj.EnableSpringConfigured;
import org.springframework.scheduling.annotation.EnableScheduling;

import javax.annotation.PostConstruct;
import java.util.Calendar;
import java.util.TimeZone;

@SpringBootApplication
@EnableScheduling
@EnableSpringConfigured
public class TrafficTweetApplication {
	public static void main(String[] args) {
		SpringApplication.run(TrafficTweetApplication.class,args);
	}

	@PostConstruct
	void started() {
		// Changes the default time zone to Santiago de Chile
		TimeZone.setDefault(TimeZone.getTimeZone("America/Santiago"));

		MongoClient mongo = new MongoClient(Constant.MONGO_HOST, Constant.MONGO_PORT);

		// Create collections
		MongoDatabase database = mongo.getDatabase(Constant.PRODUCTION_DB);

		if(database.getCollection(Constant.EVENTS_COLLECTION) == null)
			database.createCollection(Constant.EVENTS_COLLECTION);

		if(database.getCollection(Constant.POSSIBLE_COLLECTION) == null)
			database.createCollection(Constant.POSSIBLE_COLLECTION);

		if(database.getCollection(Constant.IGNORED_COLLECTION) == null)
			database.createCollection(Constant.IGNORED_COLLECTION);

		mongo.close();
	}
}
