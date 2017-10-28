package cl.usach.traffictweet;
import cl.usach.traffictweet.Models.Seed;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.aspectj.EnableSpringConfigured;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
@EnableSpringConfigured
public class TrafficTweetApplication {
	public static void main(String[] args) {
		SpringApplication.run(TrafficTweetApplication.class,args);
	}
}
