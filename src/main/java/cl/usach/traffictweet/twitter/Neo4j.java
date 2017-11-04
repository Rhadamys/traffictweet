package cl.usach.traffictweet.twitter;

import cl.usach.traffictweet.utils.Constant;
import com.mongodb.MongoClient;
import com.mongodb.client.FindIterable;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoCursor;
import com.mongodb.client.MongoDatabase;
import org.bson.Document;
import org.neo4j.driver.v1.AuthTokens;
import org.neo4j.driver.v1.Driver;
import org.neo4j.driver.v1.GraphDatabase;
import org.neo4j.driver.v1.Session;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.sql.*;


@Component
public class Neo4j {

    @PostConstruct
    public void runNeo4j(){

        System.out.println("Running neo4j module...");
        Driver graphDriver = GraphDatabase.driver("bolt://localhost", AuthTokens.basic("neo4j", "secret"));
        Session session = graphDriver.session();
        session.run("match (a)-[r]->(b) delete r");
        session.run("match (n) delete n");


        String url = "jdbc:mysql://localhost:3306/traffictweet?useSSL=false";
        String user = "traffictweet";
        String password = "secret";
        Connection conn;
        String commune;

        try {
            conn = DriverManager.getConnection(url, user, password);
            Statement st = conn.createStatement();
            ResultSet rs = st.executeQuery("SELECT name FROM communes");

            while (rs.next()) {

                commune = rs.getString("name");
                session.run("CREATE (a:Commune {name:'"+commune+"'})");
            }

            st.close();

        } catch (SQLException ex) {
            System.out.println("An error occurred. Maybe user/password is invalid");
            ex.printStackTrace();

        }

        MongoClient mongo = new MongoClient(Constant.MONGO_HOST, Constant.MONGO_PORT);
        MongoDatabase docDatabase = mongo.getDatabase(Constant.PRODUCTION_DB);
        MongoCollection<Document> collection;
        collection = docDatabase.getCollection(Constant.EVENTS_COLLECTION);

        FindIterable<Document> findIterable = collection.find(new Document());
        MongoCursor<Document> cursor = findIterable.iterator();

        while (cursor.hasNext()){

            Document doc = cursor.next();
            String text = (String)doc.get("text");
            String comuna = (String)doc.get("commune");
            String string_occurrence_date = doc.get("occurrence_date").toString();
            Date occurrence_date = (Date)doc.get("occurrence_date");
            Long occurrence_milliseconds = occurrence_date.getTime();
            if(comuna == null){
                session.run("CREATE (a:Occurrence {occurrence_date:'"+string_occurrence_date+"', occurrence_milliseconds:'"+occurrence_milliseconds+"', text:'"+text+"'})");
            }
            else{
                session.run("CREATE (a:Occurrence {occurrence_date:'"+string_occurrence_date+"', occurrence_milliseconds:'"+occurrence_milliseconds+"', text:'"+text+"', commune: '"+comuna+"'})");
            }
        }

        session.run("MATCH (a:Occurrence) where true MATCH (b:Commune) where a.commune=b.name" +
                " create (a)-[r:Location]->(b)");

        session.run("MATCH (a:Occurrence) where true MATCH (b:Occurrence) where a.occurrence_milliseconds - b.occurrence_milliseconds <= 86400000" +
                " AND a.commune = b.commune CREATE (a)-[r:Nearness]->(b)");

        session.run("match (a)-[r]->(a) delete r"); // Se borran las relaciones de los nodos con si mismos

        mongo.close();
        session.close();
        graphDriver.close();

    }
}