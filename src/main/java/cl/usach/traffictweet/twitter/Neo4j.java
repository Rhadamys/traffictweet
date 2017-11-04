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
import java.util.Date;
import java.util.logging.Level;
import java.util.logging.Logger;

public class Neo4j {
    private final static Logger LOGGER = Logger.getLogger(Neo4j.class.getName());

    public static void runNeo4j(){
        LOGGER.log(Level.INFO,"Running neo4j module...");
        Driver graphDriver = GraphDatabase.driver(Constant.BOLT_ADDRESS,
                AuthTokens.basic(Constant.BOLT_USERNAME, Constant.BOLT_PASSWORD));
        Session session = graphDriver.session();
        
        session.run("match (a)-[r]->(b) delete r");
        session.run("match (n) delete n");

        Connection conn;
        String commune;
        try {
            conn = DriverManager.getConnection(Constant.MYSQL_ADDRESS, Constant.MYSQL_USERNAME, Constant.MYSQL_PASSWORD);
            Statement st = conn.createStatement();
            ResultSet rs = st.executeQuery("SELECT name FROM communes");

            while (rs.next()) {
                commune = rs.getString("name");
                session.run("CREATE (a:Commune {name:'"+commune+"'})");
            }
            st.close();
        } catch (SQLException ex) {
            LOGGER.log(Level.WARNING,"An error occurred. Maybe user/password is invalid");
            ex.printStackTrace();
        }

        MongoClient mongo = new MongoClient(Constant.MONGO_HOST, Constant.MONGO_PORT);
        MongoDatabase docDatabase = mongo.getDatabase(Constant.PRODUCTION_DB);
        MongoCollection<Document> collection = docDatabase.getCollection(Constant.EVENTS_COLLECTION);

        LOGGER.log(Level.INFO,"Creating nodes...");
        for(Document document : collection.find())
            insertNode(document);

        mongo.close();
        session.close();
        graphDriver.close();

        createRelationships();
        LOGGER.log(Level.INFO,"Neo4j module finished successfully!");
    }
    
    public static void insertNode(Document document) {
        Driver graphDriver = GraphDatabase.driver(Constant.BOLT_ADDRESS,
                AuthTokens.basic(Constant.BOLT_USERNAME, Constant.BOLT_PASSWORD));
        Session session = graphDriver.session();

        LOGGER.log(Level.INFO,"Creating node...");
        String text = (String)document.get(Constant.TEXT_FIELD);
        String commune= (String)document.get(Constant.COMMUNE_FIELD);
        Date date = document.getDate(Constant.DATE_FIELD);
        String dateString = date.toString();
        Long millis = date.getTime();

        session.run("CREATE (a:Occurrence {date:'"+dateString+"', millis:"+millis+", text:'"+text+"', commune: '"+commune+"'})");

        session.close();
        graphDriver.close();
        LOGGER.log(Level.INFO,"Node created successfully!");
    }
    
    public static void createRelationships() {
        Driver graphDriver = GraphDatabase.driver(Constant.BOLT_ADDRESS,
                AuthTokens.basic(Constant.BOLT_USERNAME, Constant.BOLT_PASSWORD));
        Session session = graphDriver.session();

        LOGGER.log(Level.INFO,"Removing relationships...");
        session.run("match (a)-[r]->(b) delete r");

        LOGGER.log(Level.INFO,"Creating relationships");
        session.run("MATCH (a:Occurrence) where true MATCH (b:Commune) where a.commune=b.name" +
                " create (a)-[r:Ubicacion]->(b)");
        session.run("MATCH (a:Occurrence) where true MATCH (b:Occurrence) where a.occurrence_milliseconds - b.occurrence_milliseconds <= 86400000" +
                " AND a.commune = b.commune CREATE (a)-[r:Nearness]->(b)");

        session.run("match (a)-[r]->(a) delete r"); // Se borran las relaciones de los nodos con si mismos

        session.close();
        graphDriver.close();
        LOGGER.log(Level.INFO,"Relationships created successfully!");
    }
}