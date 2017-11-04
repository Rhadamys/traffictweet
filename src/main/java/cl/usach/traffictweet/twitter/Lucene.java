package cl.usach.traffictweet.twitter;

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
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.ScoreDoc;
import org.apache.lucene.search.TopScoreDocCollector;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;
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
public class Lucene {
    private final static Logger LOGGER = Logger.getLogger(Lucene.class.getName());

    public Lucene() { }

    @Scheduled(cron = "0 0 * * * *") // Cada hora
    public static void createIndex() {
        Path lucenePath = Paths.get(System.getProperty("catalina.base") + "/webapps/traffictweet/lucene/");
        try {
            LOGGER.log(Level.INFO, "Creating index...");
            Directory dir = FSDirectory.open(lucenePath);

            Analyzer analyzer = new StandardAnalyzer();
            IndexWriterConfig iwc = new IndexWriterConfig(analyzer);
            iwc.setOpenMode(IndexWriterConfig.OpenMode.CREATE);

            IndexWriter writer = new IndexWriter(dir,iwc);

            MongoClient mongo = new MongoClient(Constant.MONGO_HOST, Constant.MONGO_PORT);
            MongoDatabase database = mongo.getDatabase(Constant.PRODUCTION_DB);
            MongoCollection<org.bson.Document> events = database.getCollection(Constant.EVENTS_COLLECTION);

            for(org.bson.Document document : events.find()) {
                String tweetId = document.get(Constant.TWEET_FIELD).toString();
                String text = Util.clean(document.get(Constant.TEXT_FIELD).toString());

                org.apache.lucene.document.Document docLucene = new org.apache.lucene.document.Document();
                docLucene.add(new TextField(Constant.TWEET_FIELD, tweetId, Field.Store.YES));
                docLucene.add(new TextField(Constant.TEXT_FIELD, text, Field.Store.YES));

                if (writer.getConfig().getOpenMode() == IndexWriterConfig.OpenMode.CREATE) {
                    writer.addDocument(docLucene);
                } else {
                    writer.updateDocument(new Term("path" + document.toString()), docLucene);
                }
            }

            writer.close();
            mongo.close();
            LOGGER.log(Level.INFO, "Index created successfully!");
        } catch(IOException ioe) {
            LOGGER.log(Level.WARNING, "Caught a " + ioe.getClass() + " with message: " + ioe.getMessage());
        }

    }

    public static List<Document> search(String query) {
        Path lucenePath = Paths.get(System.getProperty("catalina.base") + "/webapps/traffictweet/lucene/");
        List<org.apache.lucene.document.Document> listDocuments = new ArrayList<Document>();
        query = Util.clean(query);
        try{            
            IndexReader reader = DirectoryReader.open(FSDirectory.open(lucenePath));
            IndexSearcher searcher = new IndexSearcher(reader);
            StandardAnalyzer analyzer = new StandardAnalyzer();
            TopScoreDocCollector collector = TopScoreDocCollector.create(50);

            Query q = new QueryParser(Constant.TEXT_FIELD, analyzer).parse(query);
            searcher.search(q, collector);
            ScoreDoc[] hits = collector.topDocs().scoreDocs;

            LOGGER.log(Level.INFO, "Found " + hits.length + " hits.");
            for (ScoreDoc hit : hits) {
                int docId = hit.doc;
                Document d = searcher.doc(docId);
                listDocuments.add(d);
            }
            reader.close();
        } catch(IOException | ParseException ex) {
            ex.printStackTrace();
        }
        return listDocuments;
    }
}
