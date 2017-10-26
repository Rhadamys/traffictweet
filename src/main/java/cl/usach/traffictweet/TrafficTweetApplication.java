package cl.usach.traffictweet;
import cl.usach.traffictweet.Models.Seed;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableScheduling;


@Configuration
@EnableAutoConfiguration
@SpringBootApplication
@EnableScheduling
public class TrafficTweetApplication {

	public static void main(String[] args) {
		ConfigurableApplicationContext context = SpringApplication.run(TrafficTweetApplication.class,args);
		new Seed().init(context);

	}
}
