package cl.usach.traffictweet.Models;

import cl.usach.traffictweet.Repositories.*;
import cl.usach.traffictweet.Twitter.Lucene;
import org.apache.lucene.document.Document;
import org.springframework.context.ConfigurableApplicationContext;

import javax.validation.constraints.Null;
import java.io.*;
import java.sql.Timestamp;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class Seed {
    public Seed() {

    }

    public void initStreet(ConfigurableApplicationContext context){
        StreetRepository repositorySt = context.getBean(StreetRepository.class);
        CommuneRepository communeRepository = context.getBean(CommuneRepository.class);
        Timestamp timestamp = new Timestamp(System.currentTimeMillis());
        String csvFile = "callesYcomunas.csv";
        BufferedReader br = null;
        String line = "";
        String cvsSplitBy = ";";
        try {
            br = new BufferedReader(new FileReader(csvFile));
            while ((line = br.readLine()) != null) {
                String[] datos = line.split(cvsSplitBy);

                if (datos.length == 2){
                    Iterable<Commune> communes = communeRepository.findByName(datos[1]);
                    for (Commune commune: communes) {
                        repositorySt.save(new Street(datos[0], timestamp, timestamp, commune));
                    }

                }
                else {
                    System.out.println("calle eliminada");
                }
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
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

    public void initCommunes(ConfigurableApplicationContext context){

        CommuneRepository repositoryCom = context.getBean(CommuneRepository.class);
        Timestamp timestamp = new Timestamp(System.currentTimeMillis());
        String csvFile = "comunas.csv";
        BufferedReader br = null;
        String line = "";
        String cvsSplitBy = ";";
        try {
            br = new BufferedReader(new FileReader(csvFile));
            while ((line = br.readLine()) != null) {
                String[] datos = line.split(cvsSplitBy);
                repositoryCom.save(new Commune(datos[0], timestamp, timestamp));
                System.out.println(datos[0]);
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
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

    public void init(ConfigurableApplicationContext context) {
        CategoryRepository repositoryC = context.getBean(CategoryRepository.class);
        KeywordRepository repositoryK = context.getBean(KeywordRepository.class);
        OccurrenceRepository repositoryO = context.getBean(OccurrenceRepository.class);
        Timestamp timestamp = new Timestamp(System.currentTimeMillis());

        Category congestion=(new Category("Congestion",timestamp,timestamp));
        Category incidente=(new Category("Incidente",timestamp,timestamp));
        Category desvios=(new Category("Desvíos",timestamp,timestamp));
        Category calle=(new Category("Calle cerrada",timestamp,timestamp));
        Category accidente=(new Category("Accidente",timestamp,timestamp));
        Category varios=(new Category("Varios",timestamp,timestamp));



        repositoryC.save(congestion);
        repositoryC.save(incidente);
        repositoryC.save(desvios);
        repositoryC.save(calle);
        repositoryC.save(accidente);
        repositoryC.save(varios);

        repositoryK.save(new Keyword("Taco",timestamp,timestamp,congestion));
        repositoryK.save(new Keyword("Accidente",timestamp,timestamp,congestion));
        repositoryK.save(new Keyword("Choque",timestamp,timestamp,accidente));
        repositoryK.save(new Keyword("Desvío",timestamp,timestamp,desvios));
        repositoryK.save(new Keyword("Intersección",timestamp,timestamp,calle));
        repositoryK.save(new Keyword("Atochamiento",timestamp,timestamp,congestion));



        Iterable<Category> categories = repositoryC.findAll();
        for (Category category: categories) {
            List<Document> documents = new Lucene().filtrarTweets(category.getKeywords(),context);
            for (Document document : documents) {
                String contenido = document.get("text");
                String url = "";
                int idx = contenido.indexOf("https://");
                if (idx != -1) {
                    url = contenido.substring(idx, contenido.length());
                    contenido = contenido.substring(0, idx - 1);
                }

                Occurrence evento =
                        new Occurrence(
                                document.get("user"),
                                category.getId(),
                                Long.parseLong(document.get("id")),
                                document.get("image"),
                                document.get("location"),
                                contenido,
                                url,
                                timestamp,
                                timestamp);
                repositoryO.save(evento);

            }
            System.out.println(category.getName());
            System.out.println( category.getId());

        }


    }
}
