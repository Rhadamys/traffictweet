package cl.usach.traffictweet;
import cl.usach.traffictweet.models.Commune;
import cl.usach.traffictweet.models.Keyword;
import cl.usach.traffictweet.repositories.*;
import cl.usach.traffictweet.models.Category;
import cl.usach.traffictweet.twitter.Lucene;
import cl.usach.traffictweet.twitter.TwitterStreaming;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.annotation.Configuration;

@Configuration
@EnableAutoConfiguration

@SpringBootApplication
public class TrafficTweetApplication {

	public static void main(String[] args) {
		ConfigurableApplicationContext context = SpringApplication.run(TrafficTweetApplication.class,args);
		CategoryRepository repositoryC = context.getBean(CategoryRepository.class);
		KeywordRepository repositoryK = context.getBean(KeywordRepository.class);
		CommuneRepository repositoryCom = context.getBean(CommuneRepository.class);
		repositoryC.save(new Category("congestion"));
		repositoryC.save(new Category("incidente"));
		repositoryC.save(new Category("desvíos"));
		repositoryC.save(new Category("calle cerrada"));
		repositoryC.save(new Category("accidente"));
		repositoryC.save(new Category("varios"));

		repositoryK.save(new Keyword("taco"));
		repositoryK.save(new Keyword("accidente"));
		repositoryK.save(new Keyword("choque"));
		repositoryK.save(new Keyword("desvío"));
		repositoryK.save(new Keyword("intersección"));
		repositoryK.save(new Keyword("calle"));
		repositoryK.save(new Keyword("comuna"));
		repositoryK.save(new Keyword("atochamiento"));

		repositoryCom.save(new Commune("Puente Alto"));
		repositoryCom.save(new Commune("Qunta Normal"));
		repositoryCom.save(new Commune("Quilicura"));
		repositoryCom.save(new Commune("Buin"));
		repositoryCom.save(new Commune("San Bernardo"));
		repositoryCom.save(new Commune("Estación Central"));
	/*
		Iterable<Category> todos = repository.findAll();
		System.out.println("Listar todos los Usuarios:");
		for (Category categoria : todos) {
			System.out.println("\t" + categoria);
		}
		System.out.println();

		context.close();
		*/
	}
}
