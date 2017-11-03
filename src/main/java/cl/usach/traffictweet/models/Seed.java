package cl.usach.traffictweet.models;

import cl.usach.traffictweet.repositories.*;
import cl.usach.traffictweet.utils.Constant;
import cl.usach.traffictweet.utils.Month;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;

import java.io.*;
import java.util.*;

@Component
public class Seed implements ApplicationRunner {
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
        keywordRepository.save(new Keyword("luz,verde", semaforos));
        keywordRepository.save(new Keyword("luz,roj", semaforos));

        initCommunes();
        initStreets();
        initMetrics();
    }
}
