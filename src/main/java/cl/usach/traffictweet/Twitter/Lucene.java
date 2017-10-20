package cl.usach.traffictweet.Twitter;

import cl.usach.traffictweet.Repositories.OccurrenceRepository;
import com.mongodb.MongoClient;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;

import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.document.Field;
import org.apache.lucene.document.TextField;
import org.apache.lucene.index.*;
import org.apache.lucene.queryparser.classic.ParseException;
import org.apache.lucene.queryparser.classic.QueryParser;
import org.apache.lucene.search.*;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;

import org.bson.Document;

import java.io.IOException;
import java.nio.file.Paths;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

import cl.usach.traffictweet.Models.*;
import cl.usach.traffictweet.Repositories.OccurrenceRepository;
import org.springframework.context.ConfigurableApplicationContext;

public class Lucene {
    public Lucene() { }

    private void crearIndice() {
        try {
            Directory dir = FSDirectory.open(Paths.get("indice/"));
            Analyzer analyzer = new StandardAnalyzer();
            IndexWriterConfig iwc = new IndexWriterConfig(analyzer);
            iwc.setOpenMode(IndexWriterConfig.OpenMode.CREATE_OR_APPEND);

            IndexWriter writer = new IndexWriter(dir,iwc);

            MongoClient mongo = new MongoClient("localhost", 27017 );
            MongoDatabase database = mongo.getDatabase("tweetsDB");
            MongoCollection<Document> tweets = database.getCollection("tweet");

            for(Document d : tweets.find()) {
                String id = d.get("id").toString();
                String user = d.get("user").toString();
                String location;
                if (d.get("location") == null) {
                    location = "-";
                } else {
                    location = d.get("location").toString();
                }
                String image = d.get("image").toString();
                String text = d.get("text").toString();

                org.apache.lucene.document.Document docLucene = new org.apache.lucene.document.Document();
                docLucene.add(new TextField("id", id, Field.Store.YES));
                docLucene.add(new TextField("user", user, Field.Store.YES));
                docLucene.add(new TextField("location", location, Field.Store.YES));
                docLucene.add(new TextField("image", image, Field.Store.YES));
                docLucene.add(new TextField("tweet", text, Field.Store.YES));

                if (writer.getConfig().getOpenMode() == IndexWriterConfig.OpenMode.CREATE) {
                    writer.addDocument(docLucene);
                } else {
                    writer.updateDocument(new Term("path" + d.toString()), docLucene);
                }
            }

            writer.close();
        } catch(IOException ioe) {
            System.out.println(" caught a "+ ioe.getClass() + "\n with message: " + ioe.getMessage());
        }

    }

    public  List<org.apache.lucene.document.Document> buscarIndice(String query, ConfigurableApplicationContext context){
        OccurrenceRepository repositoryO = context.getBean(OccurrenceRepository.class);
        List<org.apache.lucene.document.Document> listDocuments = new ArrayList<org.apache.lucene.document.Document>();
        try{
            IndexReader reader = DirectoryReader.open(FSDirectory.open(Paths.get("indice/")));
            IndexSearcher searcher = new IndexSearcher(reader);
            StandardAnalyzer analyzer = new StandardAnalyzer();
            TopScoreDocCollector collector = TopScoreDocCollector.create(100);

            Query q = new QueryParser("tweet", analyzer).parse(query);
            searcher.search(q, collector);
            ScoreDoc[] hits = collector.topDocs().scoreDocs;
            System.out.println("Found " + hits.length + " hits.");
            for(int i=0;i<hits.length;++i)
            {
                int docId = hits[i].doc;
                org.apache.lucene.document.Document d = searcher.doc(docId);
                listDocuments.add(d);
                System.out.println("ID: " + d.get("id"));
                System.out.println("User: " + d.get("user"));
                System.out.println("Location: " + d.get("location"));
                System.out.println("Image: " + d.get("image"));
                System.out.println("Text: " + d.get("tweet"));
                Timestamp timestamp = new Timestamp(System.currentTimeMillis());
                repositoryO.save(new Occurrence(d.get("user"), Long.parseLong(d.get("id")), d.get("image"), d.get("location"), d.get("tweet"), timestamp, timestamp));
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

    public static void main(String[] args) {
        new Lucene().crearIndice();
    }
}
