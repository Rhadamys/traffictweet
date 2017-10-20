package cl.usach.traffictweet.Models;

import cl.usach.traffictweet.Repositories.CategoryRepository;
import cl.usach.traffictweet.Repositories.CommuneRepository;
import cl.usach.traffictweet.Repositories.KeywordRepository;
import org.springframework.context.ConfigurableApplicationContext;

import java.sql.Timestamp;

public class Seed {
    public Seed() {

    }
    public void init(ConfigurableApplicationContext context) {
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
    }
}
