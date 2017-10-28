package cl.usach.traffictweet.Models;

import cl.usach.traffictweet.Repositories.CategoryRepository;
import cl.usach.traffictweet.Repositories.CommuneRepository;
import cl.usach.traffictweet.Repositories.KeywordRepository;
import cl.usach.traffictweet.Repositories.OccurrenceRepository;
import cl.usach.traffictweet.utils.Constant;
import cl.usach.traffictweet.utils.Util;
import com.mongodb.MongoClient;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import org.bson.Document;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Component
public class Seed implements ApplicationRunner {
    private CategoryRepository categoryRepository;
    private KeywordRepository keywordRepository;
    private CommuneRepository communeRepository;
    private OccurrenceRepository occurrenceRepository;

    @Autowired
    public Seed(
            CategoryRepository categoryRepository,
            KeywordRepository keywordRepository,
            CommuneRepository communeRepository,
            OccurrenceRepository occurrenceRepository) {
        this.categoryRepository = categoryRepository;
        this.keywordRepository = keywordRepository;
        this.communeRepository = communeRepository;
        this.occurrenceRepository = occurrenceRepository;
    }

    public void run(ApplicationArguments args) {
        Category accidente = categoryRepository.save(new Category("Accidente"));
        Category congestion = categoryRepository.save(new Category("Congestión"));
        Category desvio = categoryRepository.save(new Category("Desvío"));
        Category semaforos = categoryRepository.save(new Category("Semáforos"));

        Keyword taco = keywordRepository.save(new Keyword("taco", congestion));
        Keyword atochamiento = keywordRepository.save(new Keyword("atochamiento", congestion));
        Keyword kCongestion = keywordRepository.save(new Keyword("congestion", congestion));
        congestion.addKeyword(taco);
        congestion.addKeyword(atochamiento);
        congestion.addKeyword(kCongestion);

        Keyword kAccidente = keywordRepository.save(new Keyword("accidente", accidente));
        Keyword colision = keywordRepository.save(new Keyword("colision", accidente));
        Keyword volcamiento = keywordRepository.save(new Keyword("volcamiento", accidente));
        Keyword choque = keywordRepository.save(new Keyword("choque", accidente));
        accidente.addKeyword(kAccidente);
        accidente.addKeyword(colision);
        accidente.addKeyword(volcamiento);
        accidente.addKeyword(choque);

        Keyword apagado = keywordRepository.save(new Keyword("apagado", semaforos));
        Keyword fallo = keywordRepository.save(new Keyword("fallo,semaforo", semaforos));
        Keyword falla = keywordRepository.save(new Keyword("falla,semaforo", semaforos));
        semaforos.addKeyword(apagado);
        semaforos.addKeyword(fallo);
        semaforos.addKeyword(falla);

        Keyword kDesvio = keywordRepository.save(new Keyword("desvío", desvio));
        Keyword cerrada = keywordRepository.save(new Keyword("calle,cerrada", desvio));
        Keyword trabajos = keywordRepository.save(new Keyword("desvio,trabajo", desvio));
        Keyword obras = keywordRepository.save(new Keyword("desvio,obra", desvio));
        desvio.addKeyword(kDesvio);
        desvio.addKeyword(cerrada);
        desvio.addKeyword(trabajos);
        desvio.addKeyword(obras);

        categoryRepository.save(congestion);
        categoryRepository.save(desvio);
        categoryRepository.save(accidente);
        categoryRepository.save(semaforos);

        communeRepository.save(new Commune("Cerrillos"));
        communeRepository.save(new Commune("Cerro Navia"));
        communeRepository.save(new Commune("Conchalí"));
        communeRepository.save(new Commune("El Bosque"));
        communeRepository.save(new Commune("Estación Central"));
        communeRepository.save(new Commune("Huechuraba"));
        communeRepository.save(new Commune("Independencia"));
        communeRepository.save(new Commune("La Cisterna"));
        communeRepository.save(new Commune("La Florida"));
        communeRepository.save(new Commune("La Pintana"));
        communeRepository.save(new Commune("La Reina"));
        communeRepository.save(new Commune("Las Condes"));
        communeRepository.save(new Commune("Lo Barnechea"));
        communeRepository.save(new Commune("Lo Espejo"));
        communeRepository.save(new Commune("La Granja"));
        communeRepository.save(new Commune("Lo Prado"));
        communeRepository.save(new Commune("Macul"));
        communeRepository.save(new Commune("Maipú"));
        communeRepository.save(new Commune("Ñuñoa"));
        communeRepository.save(new Commune("Padre Hurtado"));
        communeRepository.save(new Commune("Pedro Aguirre Cerda"));
        communeRepository.save(new Commune("Peñalolén"));
        communeRepository.save(new Commune("Pirque"));
        communeRepository.save(new Commune("Providencia"));
        communeRepository.save(new Commune("Pudahuel"));
        communeRepository.save(new Commune("Puente Alto"));
        communeRepository.save(new Commune("Quilicura"));
        communeRepository.save(new Commune("Quinta Normal"));
        communeRepository.save(new Commune("Recoleta"));
        communeRepository.save(new Commune("Renca"));
        communeRepository.save(new Commune("Santiago"));
        communeRepository.save(new Commune("San Bernardo"));
        communeRepository.save(new Commune("San Joaquín"));
        communeRepository.save(new Commune("San José de Maipo"));
        communeRepository.save(new Commune("San Miguel"));
        communeRepository.save(new Commune("San Ramón"));
        communeRepository.save(new Commune("Vitacura"));

        // Abrir conexión con MongoDB
        MongoClient mongo = new MongoClient(Constant.MONGO_HOST, Constant.MONGO_PORT);
        MongoDatabase database = mongo.getDatabase(Constant.PRODUCTION_DB);
        MongoCollection<Document> events = database.getCollection(Constant.EVENTS_COLLECTION);

        List<Category> categories = new ArrayList<>();
        categoryRepository.findAll().forEach(categories::add);
        for(Document document: events.find()) {
            String user = document.get(Constant.USER_FIELD).toString();
            String image = document.get(Constant.IMAGE_FIELD).toString();
            String text = document.get(Constant.TEXT_FIELD).toString();

            Occurrence occurrence = occurrenceRepository.save(new Occurrence(user, image, text));
            for(int i = 0; i < categories.size(); i++) {
                Category category = categories.get(i);
                List<String> keywords = new ArrayList<>();
                for(Keyword keyword: category.getKeywords())
                    keywords.add(keyword.getName());

                if(Util.match(text, keywords)) {
                    category.addOccurrence(occurrence);
                    occurrence.addCategory(category);
                }
            }
        }

        categoryRepository.save(categories);
        // Cerrar conexión con MongoDB
        mongo.close();
    }
}
