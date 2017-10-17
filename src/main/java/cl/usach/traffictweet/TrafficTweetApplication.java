package cl.usach.traffictweet;

import cl.usach.traffictweet.twitter.Lucene;
import cl.usach.traffictweet.twitter.TwitterStreaming;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class TrafficTweetApplication {

	public static void main(String[] args) {
		SpringApplication.run(TrafficTweetApplication.class, args);
	}
}
