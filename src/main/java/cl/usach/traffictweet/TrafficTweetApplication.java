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

		repositoryC.save(new Category("Congestion",timestamp,timestamp));
		repositoryC.save(new Category("Incidente",timestamp,timestamp));
		repositoryC.save(new Category("Desvíos",timestamp,timestamp));
		repositoryC.save(new Category("Calle cerrada",timestamp,timestamp));
		repositoryC.save(new Category("Accidente",timestamp,timestamp));
		repositoryC.save(new Category("Varios",timestamp,timestamp));

		repositoryK.save(new Keyword("Taco",timestamp,timestamp));
		repositoryK.save(new Keyword("Accidente",timestamp,timestamp));
		repositoryK.save(new Keyword("Choque",timestamp,timestamp));
		repositoryK.save(new Keyword("Desvío",timestamp,timestamp));
		repositoryK.save(new Keyword("Intersección",timestamp,timestamp));
		repositoryK.save(new Keyword("Calle",timestamp,timestamp));
		repositoryK.save(new Keyword("Comuna",timestamp,timestamp));
		repositoryK.save(new Keyword("Atochamiento",timestamp,timestamp));

		repositoryCom.save(new Commune("Cerrillos", timestamp, timestamp));
		repositoryCom.save(new Commune("La Reina", timestamp, timestamp));
		repositoryCom.save(new Commune("Pudahuel", timestamp, timestamp));
		repositoryCom.save(new Commune("Cerro Navia", timestamp, timestamp));
		repositoryCom.save(new Commune("Las Condes", timestamp, timestamp));
		repositoryCom.save(new Commune("Quilicura", timestamp, timestamp));
		repositoryCom.save(new Commune("Conchalí", timestamp, timestamp));
		repositoryCom.save(new Commune("Lo Barnechea", timestamp, timestamp));
		repositoryCom.save(new Commune("Quinta Normal", timestamp, timestamp));
		repositoryCom.save(new Commune("El Bosque", timestamp, timestamp));
		repositoryCom.save(new Commune("Lo Espejo", timestamp, timestamp));
		repositoryCom.save(new Commune("Recoleta", timestamp, timestamp));
		repositoryCom.save(new Commune("Estación Central", timestamp, timestamp));
		repositoryCom.save(new Commune("Lo Prado", timestamp, timestamp));
		repositoryCom.save(new Commune("Renca", timestamp, timestamp));
		repositoryCom.save(new Commune("Huechuraba", timestamp, timestamp));
		repositoryCom.save(new Commune("Macul", timestamp, timestamp));
		repositoryCom.save(new Commune("San Miguel", timestamp, timestamp));
		repositoryCom.save(new Commune("Independencia", timestamp, timestamp));
		repositoryCom.save(new Commune("Maipú", timestamp, timestamp));
		repositoryCom.save(new Commune("San Joaquín", timestamp, timestamp));
		repositoryCom.save(new Commune("La Cisterna", timestamp, timestamp));
		repositoryCom.save(new Commune("Ñuñoa", timestamp, timestamp));
		repositoryCom.save(new Commune("San Ramón", timestamp, timestamp));
		repositoryCom.save(new Commune("La Florida", timestamp, timestamp));
		repositoryCom.save(new Commune("Pedro Aguirre Cerda", timestamp, timestamp));
		repositoryCom.save(new Commune("Santiago", timestamp, timestamp));
		repositoryCom.save(new Commune("La Pintana", timestamp, timestamp));
		repositoryCom.save(new Commune("Peñalolén", timestamp, timestamp));
		repositoryCom.save(new Commune("Vitacura", timestamp, timestamp));
		repositoryCom.save(new Commune("La Granja", timestamp, timestamp));
		repositoryCom.save(new Commune("Providencia", timestamp, timestamp));
		repositoryCom.save(new Commune("Padre Hurtado", timestamp, timestamp));
		repositoryCom.save(new Commune("San Bernardo", timestamp, timestamp));
		repositoryCom.save(new Commune("Puente Alto", timestamp, timestamp));
		repositoryCom.save(new Commune("Pirque", timestamp, timestamp));
		repositoryCom.save(new Commune("San José de Maipo", timestamp, timestamp));
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
