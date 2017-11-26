package cl.usach.traffictweet.twitter;

import cl.usach.traffictweet.mongo.models.Occurrence;
import cl.usach.traffictweet.mongo.repositories.OccurrenceRepository;
import cl.usach.traffictweet.utils.Constant;
import cl.usach.traffictweet.utils.Util;
import com.mongodb.MongoClient;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.apache.lucene.document.TextField;
import org.apache.lucene.index.*;
import org.apache.lucene.queryparser.classic.ParseException;
import org.apache.lucene.queryparser.classic.QueryParser;
import org.apache.lucene.search.*;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

@Component
public class Lucene implements ApplicationRunner {
    private final static Logger LOGGER = Logger.getLogger(Lucene.class.getName());

    private static OccurrenceRepository occurrenceRepository;

    @Autowired
    public Lucene(OccurrenceRepository occurrenceRepository) {
        Lucene.occurrenceRepository = occurrenceRepository;
    }

    public void run(ApplicationArguments args) {
        createIndex();
    }

    @Scheduled(cron = "0 0 * * * *") // Cada hora
    public void createIndex() {
        Path lucenePath = Paths.get(System.getProperty("catalina.base") + "/webapps/traffictweet/lucene/");
        try {
            LOGGER.log(Level.INFO, "Creating index...");
            Directory dir = FSDirectory.open(lucenePath);

            Analyzer analyzer = new StandardAnalyzer();
            IndexWriterConfig iwc = new IndexWriterConfig(analyzer);
            iwc.setOpenMode(IndexWriterConfig.OpenMode.CREATE);

            IndexWriter writer = new IndexWriter(dir,iwc);

            List<Occurrence> occurrences = occurrenceRepository.findAll();
            for(Occurrence occurrence: occurrences) {
                org.apache.lucene.document.Document docLucene = new org.apache.lucene.document.Document();
                docLucene.add(new TextField(Constant.TWEET_FIELD, occurrence.getTweetId(), Field.Store.YES));
                docLucene.add(new TextField(Constant.TEXT_FIELD, occurrence.getText(), Field.Store.YES));
                docLucene.add(new TextField(Constant.COMMUNE_FIELD, occurrence.getCommune(), Field.Store.YES));
                for(String category: occurrence.getCategories())
                    docLucene.add(new TextField(Constant.CATEGORIES_FIELD, category, Field.Store.YES));

                if (writer.getConfig().getOpenMode() == IndexWriterConfig.OpenMode.CREATE) {
                    writer.addDocument(docLucene);
                } else {
                    writer.updateDocument(new Term("path" + occurrence.toString()), docLucene);
                }
            }

            writer.close();
            LOGGER.log(Level.INFO, "Index created successfully!");
        } catch(IOException ioe) {
            LOGGER.log(Level.WARNING, "Caught a " + ioe.getClass() + " with message: " + ioe.getMessage());
        }

    }

    public static List<Occurrence> search(String query, String commune, String category) {
        Path lucenePath = Paths.get(System.getProperty("catalina.base") + "/webapps/traffictweet/lucene/");
        List<Occurrence> occurrences = new ArrayList<>();
        query = Util.clean(query);
        try {
            IndexReader reader = DirectoryReader.open(FSDirectory.open(lucenePath));
            IndexSearcher searcher = new IndexSearcher(reader);
            StandardAnalyzer analyzer = new StandardAnalyzer();
            TopScoreDocCollector collector = TopScoreDocCollector.create(50);

            Query luceneQuery;
            BooleanQuery.Builder finalQuery = new BooleanQuery.Builder();
            if(query.length() > 0) {
                luceneQuery = new QueryParser(Constant.TEXT_FIELD, analyzer).parse(query);
                finalQuery.add(luceneQuery, BooleanClause.Occur.MUST);
            }
            if(commune.length() > 0) {
                luceneQuery = new QueryParser(Constant.COMMUNE_FIELD, analyzer).parse(commune);
                finalQuery.add(luceneQuery, BooleanClause.Occur.MUST);
            }
            if(category.length() > 0) {
                luceneQuery = new QueryParser(Constant.CATEGORIES_FIELD, analyzer).parse(category);
                finalQuery.add(luceneQuery, BooleanClause.Occur.MUST);
            }

            searcher.search(finalQuery.build(), collector);
            ScoreDoc[] hits = collector.topDocs().scoreDocs;

            LOGGER.log(Level.INFO, "Found " + hits.length + " hits.");
            for (ScoreDoc hit : hits) {
                Document document = searcher.doc(hit.doc);
                String tweetId = document.get(Constant.TWEET_FIELD);
                Occurrence occurrence = occurrenceRepository.findByTweetId(tweetId);
                occurrences.add(occurrence);
            }

            reader.close();
            occurrences.sort((o1, o2) -> -o1.getDate().compareTo(o2.getDate()));
        } catch(IOException | ParseException ex) {
            ex.printStackTrace();
        }
        return occurrences;
    }
}
