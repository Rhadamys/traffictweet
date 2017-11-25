package cl.usach.traffictweet;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.aspectj.EnableSpringConfigured;
import org.springframework.scheduling.annotation.EnableScheduling;

import javax.annotation.PostConstruct;
import java.time.ZoneId;
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
		TimeZone.setDefault(TimeZone.getTimeZone(ZoneId.of("America/Santiago")));
	}
}
