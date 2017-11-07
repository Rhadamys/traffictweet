package cl.usach.traffictweet.sql;

import cl.usach.traffictweet.neo4j.Neo4j;
import cl.usach.traffictweet.sql.models.*;
import cl.usach.traffictweet.sql.repositories.*;
import cl.usach.traffictweet.twitter.Lucene;
import cl.usach.traffictweet.utils.Constant;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Component;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

@Component
public class Seed implements ApplicationRunner {
    private final static java.util.logging.Logger LOGGER = Logger.getLogger(Seed.class.getName());

    private CategoryRepository categoryRepository;
    private KeywordRepository keywordRepository;
    private CommuneRepository communeRepository;
    private StreetRepository streetRepository;
    private MetricRepository metricRepository;
    private CategoryMetricRepository categoryMetricRepository;
    private CommuneMetricRepository communeMetricRepository;

    @Autowired
    public Seed(
            CategoryRepository categoryRepository,
            KeywordRepository keywordRepository,
            CommuneRepository communeRepository,
            StreetRepository streetRepository,
            MetricRepository metricRepository,
            CategoryMetricRepository categoryMetricRepository,
            CommuneMetricRepository communeMetricRepository) {
        this.categoryRepository = categoryRepository;
        this.keywordRepository = keywordRepository;
        this.communeRepository = communeRepository;
        this.streetRepository = streetRepository;
        this.metricRepository = metricRepository;
        this.categoryMetricRepository = categoryMetricRepository;
        this.communeMetricRepository = communeMetricRepository;
    }

    /**
     * Populates table "Communes" using data from file "communes.csv"
     */
    private void initCommunes() {
        BufferedReader br = null;
        String line;
        HashMap<String, Commune> communes = new HashMap<>();
        try {
            ClassLoader classLoader = getClass().getClassLoader();
            InputStream file = classLoader.getResourceAsStream("communes.csv");
            br = new BufferedReader(new InputStreamReader(file, "UTF-8"));
            while ((line = br.readLine()) != null) {
                String data[] = line.split(Constant.CSV_SPLIT_BY);
                Commune commune = communeRepository.save(new Commune(data[0], data[1], data[2], data[3]));
                LOGGER.log(Level.INFO,"Nueva comuna: " + data[0]);
                communes.put(commune.getName(), commune);
            }

            file = classLoader.getResourceAsStream("adjacency.csv");
            br.close();
            br = new BufferedReader(new InputStreamReader(file, "UTF-8"));
            while ((line = br.readLine()) != null) {
                String data[] = line.split(Constant.CSV_SPLIT_BY);
                Commune commune = communes.get(data[0]);
                for(int i = 1; i < data.length; i++) {
                    commune.addAdjacentCommune(communes.get(data[i]));
                    LOGGER.log(Level.INFO, "Comuna " + commune.getName() + " <--- " + data[i]);
                }
                communeRepository.save(commune);
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

    /**
     * Populates table "Streets" using data from file "streets.csv"
     */
    private void initStreets() {
        BufferedReader br = null;
        String line;
        try {
            ClassLoader classLoader = getClass().getClassLoader();
            InputStream file = classLoader.getResourceAsStream("streets.csv");
            br = new BufferedReader(new InputStreamReader(file, "UTF-8"));
            while ((line = br.readLine()) != null) {
                String[] datos = line.split(Constant.CSV_SPLIT_BY);
                if (datos.length == 2 && streetRepository.findByName(datos[0]) == null) {
                    Commune commune = communeRepository.findByName(datos[1]);
                    streetRepository.save(new Street(datos[0], commune));
                    LOGGER.log(Level.INFO,"Nueva calle: " + datos[0] + "... Comuna: " + datos[1]);
                } else {
                    LOGGER.log(Level.INFO,"Calle eliminada...");
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

    private void initMetrics() {
        List<Occurrence> occurrences = Occurrence.getAll(categoryRepository, communeRepository);
        for(Occurrence occurrence: occurrences) {
            Date occurrenceDate = occurrence.getDate();
            for(Category category: occurrence.getCategories()) {
                // Metrics by category and commune
                Metric metric = metricRepository
                        .findByMetricDateAndCategoryAndCommune(occurrenceDate, category, occurrence.getCommune());

                if(metric == null)
                    metric = new Metric(
                            category,
                            occurrence.getCommune(),
                            occurrenceDate);

                metric.incrementCount();
                metricRepository.save(metric);

                // Metrics globally by category
                CategoryMetric categoryMetric = categoryMetricRepository
                        .findByMetricDateAndCategory(occurrenceDate, category);

                if(categoryMetric == null)
                    categoryMetric = new CategoryMetric(category, occurrenceDate);

                categoryMetric.incrementCount();
                categoryMetricRepository.save(categoryMetric);
            }

            // Metrics globally by commune
            CommuneMetric communeMetric = communeMetricRepository
                    .findByMetricDateAndCommune(occurrenceDate, occurrence.getCommune());

            if(communeMetric == null)
                communeMetric = new CommuneMetric(occurrence.getCommune(), occurrenceDate);

            communeMetric.incrementCount();
            communeMetricRepository.save(communeMetric);
        }
    }

    private void initKeywords(){
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
        keywordRepository.save(new Keyword("luz,verde", semaforos));
        keywordRepository.save(new Keyword("luz,roj", semaforos));
    }

    public void run(ApplicationArguments args) {
        try {
            initCommunes();
            initKeywords();

            // TODO: Mejorar reconocimiento de comunas por calle. NO descomentar hasta entonces.
            // initStreets();

            initMetrics();
            Neo4j.seed();
        } catch(DataIntegrityViolationException ex) {
            LOGGER.log(Level.WARNING, "Data already populated! If you want to do this again, change configuration to \"create\" in \"application.properties\" file.");
        }

        Lucene.createIndex();
    }
}
