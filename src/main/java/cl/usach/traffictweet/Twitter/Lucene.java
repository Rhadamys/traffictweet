package cl.usach.traffictweet.Twitter;

import cl.usach.traffictweet.Repositories.CommuneRepository;
import cl.usach.traffictweet.Repositories.KeywordRepository;
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

import java.io.IOException;
import java.nio.file.Paths;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import cl.usach.traffictweet.Models.*;
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
            MongoCollection<org.bson.Document> tweets = database.getCollection("tweet");

            for(org.bson.Document d : tweets.find()) {
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
                docLucene.add(new TextField("text", text, Field.Store.YES));

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

    public List<Document> filtrarTweets(ConfigurableApplicationContext context){
        List<Document> listDocuments = new ArrayList<>();
        try{
            IndexReader reader = DirectoryReader.open(FSDirectory.open(Paths.get("indice/")));
            IndexSearcher searcher = new IndexSearcher(reader);
            StandardAnalyzer analyzer = new StandardAnalyzer();
            TopScoreDocCollector collector = TopScoreDocCollector.create(100);

            BooleanQuery.Builder finalQuery = new BooleanQuery.Builder();
            QueryParser textParser = new QueryParser("text", analyzer);
            Iterable<Keyword> keywords = context.getBean(KeywordRepository.class).findAll();
            BooleanQuery.Builder textQueryBuilder = new BooleanQuery.Builder();
            for(Keyword keyword: keywords) {
                textQueryBuilder.add(textParser.parse(keyword.getName()), BooleanClause.Occur.SHOULD);
            }
            finalQuery.add(textQueryBuilder.build(), BooleanClause.Occur.MUST);

            QueryParser locationParser = new QueryParser("location", analyzer);
            Iterable<Commune> communes = context.getBean(CommuneRepository.class).findAll();
            BooleanQuery.Builder locationQueryBuilder = new BooleanQuery.Builder();
            for(Commune commune: communes) {
                BooleanQuery.Builder communeQueryBuilder = new BooleanQuery.Builder();
                communeQueryBuilder.add(locationParser.parse("Chile"), BooleanClause.Occur.MUST);
                String[] partes = commune.getName().split(" ");
                if(partes.length == 1)
                    communeQueryBuilder.add(locationParser.parse(partes[0]), BooleanClause.Occur.MUST);
                else {
                    PhraseQuery phraseQuery = new PhraseQuery(0, "location", partes);
                    communeQueryBuilder.add(phraseQuery, BooleanClause.Occur.MUST);
                }
                locationQueryBuilder.add(communeQueryBuilder.build(), BooleanClause.Occur.SHOULD);
            }
            finalQuery.add(locationQueryBuilder.build(), BooleanClause.Occur.MUST);
            searcher.search(finalQuery.build(), collector);

            ScoreDoc[] hits = collector.topDocs().scoreDocs;
            System.out.println("Found " + hits.length + " hits.");
            for (ScoreDoc hit : hits) {
                int docId = hit.doc;
                Document document = searcher.doc(docId);
                listDocuments.add(document);
            }
            reader.close();

        } catch(IOException | ParseException ex) {
            Logger.getLogger(Lucene.class.getName()).log(Level.SEVERE,null,ex);
        }
        return listDocuments;
    }

    public static void main(String[] args) {
        new Lucene().crearIndice();
    }
}
