package cl.usach.traffictweet.twitter;

import cl.usach.traffictweet.utils.Constant;
import com.mongodb.MongoClient;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.document.Field;
import org.apache.lucene.document.TextField;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.IndexWriterConfig;
import org.apache.lucene.index.Term;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.nio.file.Paths;

@Component
public class Lucene {
    public static final String LUCENE_INDEX_PATH = "lucene/index/";
    public Lucene() { }

    @Scheduled(cron = "0 0 3 * * *") // 03:00 todos los días
    private void updateIndex() {
        try {
            Directory dir = FSDirectory.open(Paths.get(LUCENE_INDEX_PATH));
            Analyzer analyzer = new StandardAnalyzer();
            IndexWriterConfig iwc = new IndexWriterConfig(analyzer);
            iwc.setOpenMode(IndexWriterConfig.OpenMode.CREATE_OR_APPEND);

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
        } catch(IOException ioe) {
            System.out.println("Caught a " + ioe.getClass() + " with message: " + ioe.getMessage());
        }

    }

    // TODO: Adecuar a nueva funcionalidad
    /*
    public List<Document> filtrarTweets(ConfigurableApplicationContext context){
        List<Document> listDocuments = new ArrayList<>();
        try{
            IndexReader reader = DirectoryReader.open(FSDirectory.open(Paths.get(LUCENE_INDEX_PATH)));
            IndexSearcher searcher = new IndexSearcher(reader);
            StandardAnalyzer analyzer = new StandardAnalyzer();
            TopScoreDocCollector collector = TopScoreDocCollector.create(100);

            BooleanQuery.Builder finalQuery = new BooleanQuery.Builder();
            QueryParser textParser = new QueryParser("text", analyzer);
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
    }*/
}
