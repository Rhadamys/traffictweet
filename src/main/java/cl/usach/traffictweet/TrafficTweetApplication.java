package cl.usach.traffictweet;
import cl.usach.traffictweet.Models.Commune;
import cl.usach.traffictweet.Models.Keyword;
import cl.usach.traffictweet.Repositories.*;
import cl.usach.traffictweet.Models.Category;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.annotation.Configuration;

import java.sql.Timestamp;

@Configuration
@EnableAutoConfiguration

@SpringBootApplication
public class TrafficTweetApplication {

	public static void main(String[] args) {
		ConfigurableApplicationContext context = SpringApplication.run(TrafficTweetApplication.class,args);
		CategoryRepository repositoryC = context.getBean(CategoryRepository.class);
		KeywordRepository repositoryK = context.getBean(KeywordRepository.class);
		CommuneRepository repositoryCom = context.getBean(CommuneRepository.class);
		Timestamp timestamp = new Timestamp(System.currentTimeMillis());

		repositoryC.save(new Category("congestion",timestamp,timestamp));
		repositoryC.save(new Category("incidente",timestamp,timestamp));
		repositoryC.save(new Category("desvíos",timestamp,timestamp));
		repositoryC.save(new Category("calle cerrada",timestamp,timestamp));
		repositoryC.save(new Category("accidente",timestamp,timestamp));
		repositoryC.save(new Category("varios",timestamp,timestamp));

		repositoryK.save(new Keyword("taco",timestamp,timestamp));
		repositoryK.save(new Keyword("accidente",timestamp,timestamp));
		repositoryK.save(new Keyword("choque",timestamp,timestamp));
		repositoryK.save(new Keyword("desvío",timestamp,timestamp));
		repositoryK.save(new Keyword("intersección",timestamp,timestamp));
		repositoryK.save(new Keyword("calle",timestamp,timestamp));
		repositoryK.save(new Keyword("comuna",timestamp,timestamp));
		repositoryK.save(new Keyword("atochamiento",timestamp,timestamp));

		repositoryCom.save(new Commune("Puente Alto",timestamp,timestamp));
		repositoryCom.save(new Commune("Qunta Normal",timestamp,timestamp));
		repositoryCom.save(new Commune("Quilicura",timestamp,timestamp));
		repositoryCom.save(new Commune("Buin",timestamp,timestamp));
		repositoryCom.save(new Commune("San Bernardo",timestamp,timestamp));
		repositoryCom.save(new Commune("Estación Central",timestamp,timestamp));
	/*
		Iterable<Category> todos = repository.findAll();
		System.out.println("Listar todos los Usuarios:",timestamp,timestamp);
		for (Category categoria : todos) {
			System.out.println("\t" + categoria);
		}
		System.out.println();

		context.close();
		*/
	}
}
