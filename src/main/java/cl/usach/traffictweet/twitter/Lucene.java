package cl.usach.traffictweet.twitter;

import cl.usach.traffictweet.utils.Constant;
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
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

@Component
public class Lucene {
    public Lucene() { }

    @Scheduled(cron = "0 0 * * * *") // Cada hora
    private void updateIndex() {
        try {
            System.out.println("LUCENE: Updating index...");

            String catalina = System.getProperty("catalina.base");
            String lucene = catalina + "/webapps/traffictweet/lucene/";
            Directory dir = FSDirectory.open(Paths.get(lucene));

            Analyzer analyzer = new StandardAnalyzer();
            IndexWriterConfig iwc = new IndexWriterConfig(analyzer);
            iwc.setOpenMode(IndexWriterConfig.OpenMode.CREATE);

            IndexWriter writer = new IndexWriter(dir,iwc);

            // Abrir conexión con MongoDB
            MongoClient mongo = new MongoClient(Constant.MONGO_HOST, Constant.MONGO_PORT);
            MongoDatabase database = mongo.getDatabase(Constant.PRODUCTION_DB);
            MongoCollection<org.bson.Document> events = database.getCollection(Constant.EVENTS_COLLECTION);

            for(org.bson.Document document : events.find()) {
                String id = document.get(Constant.TWEET_FIELD).toString();
                String user = document.get(Constant.USER_FIELD).toString();
                String image = document.get(Constant.IMAGE_FIELD).toString();
                String text = document.get(Constant.TEXT_FIELD).toString();

                org.apache.lucene.document.Document docLucene = new org.apache.lucene.document.Document();
                docLucene.add(new TextField(Constant.TWEET_FIELD, id, Field.Store.YES));
                docLucene.add(new TextField(Constant.USER_FIELD, user, Field.Store.YES));
                docLucene.add(new TextField(Constant.IMAGE_FIELD, image, Field.Store.YES));
                docLucene.add(new TextField(Constant.TEXT_FIELD, text, Field.Store.YES));

                if (writer.getConfig().getOpenMode() == IndexWriterConfig.OpenMode.CREATE) {
                    writer.addDocument(docLucene);
                } else {
                    writer.updateDocument(new Term("path" + document.toString()), docLucene);
                }
            }

            writer.close();

            // Cerrar conexión con MongoDB
            mongo.close();

            System.out.println("LUCENE: Index updated successfully...");
        } catch(IOException ioe) {
            System.out.println("Caught a " + ioe.getClass() + " with message: " + ioe.getMessage());
        }

    }

    public List<Document> buscarIndice(String query){
        List<org.apache.lucene.document.Document> listDocuments = new ArrayList<Document>();
        try{
            String catalina = System.getProperty("catalina.base");
            String lucene = catalina + "/webapps/traffictweet/lucene/";


            IndexReader reader = DirectoryReader.open(FSDirectory.open(Paths.get(lucene)));
            IndexSearcher searcher = new IndexSearcher(reader);
            StandardAnalyzer analyzer = new StandardAnalyzer();
            TopScoreDocCollector collector = TopScoreDocCollector.create(10);

            Query q = new QueryParser(Constant.TEXT_FIELD, analyzer).parse(query);
            searcher.search(q, collector);
            ScoreDoc[] hits = collector.topDocs().scoreDocs;
            System.out.println("Found " + hits.length + " hits.");
            for(int i=0; i < hits.length; ++i)
            {
                int docId = hits[i].doc;
                org.apache.lucene.document.Document d = searcher.doc(docId);
                listDocuments.add(d);
                System.out.println("Tweet: " + d.get(Constant.TWEET_FIELD));
                System.out.println("User: " + d.get(Constant.USER_FIELD));
                System.out.println("Image: " + d.get(Constant.IMAGE_FIELD));
                System.out.println("Text: " + d.get(Constant.TEXT_FIELD));
            }
            reader.close();

        }

        catch(IOException ex){
//            Logger.getLogger(Lucene.class.getName()).log(Level.SEVERE,null,ex);
        }
        catch(ParseException ex){
//            Logger.getLogger(Lucene.class.getName()).log(Level.SEVERE,null,ex);
        }
        return listDocuments;
    }
}
