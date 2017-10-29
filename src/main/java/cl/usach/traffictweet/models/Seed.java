package cl.usach.traffictweet.models;

import cl.usach.traffictweet.repositories.*;
import cl.usach.traffictweet.utils.Constant;
import cl.usach.traffictweet.utils.Util;
import com.mongodb.MongoClient;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import org.apache.commons.io.IOUtils;
import org.bson.Document;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;

import java.io.*;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

@Component
public class Seed implements ApplicationRunner {
    private CategoryRepository categoryRepository;
    private KeywordRepository keywordRepository;
    private CommuneRepository communeRepository;
    private OccurrenceRepository occurrenceRepository;
    private StreetRepository streetRepository;

    @Autowired
    public Seed(
            CategoryRepository categoryRepository,
            KeywordRepository keywordRepository,
            CommuneRepository communeRepository,
            OccurrenceRepository occurrenceRepository,
            StreetRepository streetRepository) {
        this.categoryRepository = categoryRepository;
        this.keywordRepository = keywordRepository;
        this.communeRepository = communeRepository;
        this.occurrenceRepository = occurrenceRepository;
        this.streetRepository = streetRepository;
    }

    private void initCommunes() {
        BufferedReader br = null;
        String line;
        try {
            ClassLoader classLoader = getClass().getClassLoader();
            InputStream file = classLoader.getResourceAsStream("communes.csv");
            br = new BufferedReader(new InputStreamReader(file, "UTF-8"));
            while ((line = br.readLine()) != null) {
                String data[] = line.split(Constant.CSV_SPLIT_BY);
                communeRepository.save(new Commune(data[0], data[1], data[2], data[3]));
                System.out.println("Comuna: " + data[0]);
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (br != null) {
                try {
                    br.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    private void initStreets() {
        BufferedReader br = null;
        String line;
        try {
            br = new BufferedReader(new FileReader("streets.csv"));
            while ((line = br.readLine()) != null) {
                String[] datos = line.split(Constant.CSV_SPLIT_BY);
                if (datos.length == 2 && streetRepository.findByName(datos[0]) == null) {
                    Commune commune = communeRepository.findByName(datos[1]);
                    streetRepository.save(new Street(datos[0], commune));
                    System.out.println("Calle: " + datos[0] + "\tComuna: " + datos[1]);
                } else {
                    System.out.println("Calle eliminada...");
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (br != null) {
                try {
                    br.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }


    }

    private void initOccurrences() {
        // Abrir conexión con MongoDB
        MongoClient mongo = new MongoClient(Constant.MONGO_HOST, Constant.MONGO_PORT);
        MongoDatabase database = mongo.getDatabase(Constant.PRODUCTION_DB);
        MongoCollection<Document> events = database.getCollection(Constant.EVENTS_COLLECTION);

        List<Category> categories = new ArrayList<>();
        categoryRepository.findAll().forEach(categories::add);
        Iterable<Commune> communes = communeRepository.findAll();
        for(Document document: events.find()) {
            String user = document.get(Constant.USER_FIELD).toString();
            String image = document.get(Constant.IMAGE_FIELD).toString();
            String text = document.get(Constant.TEXT_FIELD).toString();
            DateFormat dateFormat = new SimpleDateFormat("EEE MMM dd kk:mm:ss z yyyy", Locale.ENGLISH);
            Date date;
            try {
                date = dateFormat.parse(document.getString(Constant.DATE_FIELD));
            } catch (ParseException ex) {
                date = new Date();
            }

            Commune occurrenceCommune = null;
            for(Commune commune: communes) {
                List<String> keywords = new ArrayList<>();
                keywords.add(Util.clean(commune.getName()));
                for(Street street: commune.getStreets())
                    keywords.add(Util.clean(street.getName()));

                if(Util.match(text, keywords)) {
                    occurrenceCommune = commune;
                    break;
                }
            }

            Occurrence occurrence = occurrenceRepository.save(
                    new Occurrence(user, image, text, date, occurrenceCommune));

            for (Category category : categories) {
                List<String> keywords = new ArrayList<>();
                for (Keyword keyword : category.getKeywords())
                    keywords.add(keyword.getName());

                if (Util.match(text, keywords)) {
                    category.addOccurrence(occurrence);
                    occurrence.addCategory(category);
                }
            }
        }

        categoryRepository.save(categories);
        // Cerrar conexión con MongoDB
        mongo.close();
    }

    public void run(ApplicationArguments args) {
        Category accidente = categoryRepository.save(new Category("Accidente"));
        Category congestion = categoryRepository.save(new Category("Congestión"));
        Category desvio = categoryRepository.save(new Category("Desvío"));
        Category semaforos = categoryRepository.save(new Category("Semáforos"));

        keywordRepository.save(new Keyword("accidente", accidente));
        keywordRepository.save(new Keyword("colision", accidente));
        keywordRepository.save(new Keyword("volcamiento", accidente));
        keywordRepository.save(new Keyword("volcado", accidente));
        keywordRepository.save(new Keyword("choque", accidente));
        keywordRepository.save(new Keyword("chocado", accidente));
        keywordRepository.save(new Keyword("taco", congestion));
        keywordRepository.save(new Keyword("atochamiento", congestion));
        keywordRepository.save(new Keyword("congestion", congestion));
        keywordRepository.save(new Keyword("desvío", desvio));
        keywordRepository.save(new Keyword("calle,cerrada", desvio));
        keywordRepository.save(new Keyword("desvio,trabajo", desvio));
        keywordRepository.save(new Keyword("desvio,obra", desvio));
        keywordRepository.save(new Keyword("semaforo,apagado", semaforos));
        keywordRepository.save(new Keyword("semaforo,mal,estado", semaforos));
        keywordRepository.save(new Keyword("fallo,semaforo", semaforos));
        keywordRepository.save(new Keyword("falla,semaforo", semaforos));

        initCommunes();
        //initStreets();
        initOccurrences();
    }
}
