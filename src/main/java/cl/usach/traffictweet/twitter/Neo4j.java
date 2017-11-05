package cl.usach.traffictweet.twitter;

import cl.usach.traffictweet.models.Commune;
import cl.usach.traffictweet.repositories.CommuneRepository;
import cl.usach.traffictweet.utils.Constant;
import com.mongodb.MongoClient;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import org.bson.Document;
import org.neo4j.driver.v1.Driver;
import org.neo4j.driver.v1.AuthTokens;
import org.neo4j.driver.v1.GraphDatabase;
import org.neo4j.driver.v1.Session;

import java.sql.*;
import java.util.Date;
import java.util.logging.Level;
import java.util.logging.Logger;

public class Neo4j {
    private final static Logger LOGGER = Logger.getLogger(Neo4j.class.getName());
    private static CommuneRepository communeRepository;

    public static void runNeo4j(CommuneRepository communeRepository) {
        Neo4j.communeRepository = communeRepository;
        
        LOGGER.log(Level.INFO,"Running neo4j module...");
        Driver graphDriver = GraphDatabase.driver(Constant.BOLT_ADDRESS,
                AuthTokens.basic(Constant.BOLT_USERNAME, Constant.BOLT_PASSWORD));
        Session session = graphDriver.session();
        
        session.run("match (a)-[r]->(b) delete r");
        session.run("match (n) delete n");

        Connection conn;
        try {
            conn = DriverManager.getConnection(Constant.MYSQL_ADDRESS, Constant.MYSQL_USERNAME, Constant.MYSQL_PASSWORD);
            Statement st = conn.createStatement();
            ResultSet rs = st.executeQuery("SELECT name FROM communes");

            while (rs.next()) {
                Commune commune = Neo4j.communeRepository.findByName(rs.getString("name"));
                if(!commune.getName().equals(Constant.UNKNOWN_COMMUNE)) {
                    String query = "CREATE (a:Commune {name:'"+ commune.getName() + "',adjacent:[";
                    if(!commune.getAdjacentCommunes().isEmpty()) {
                        for(Commune adjacent: commune.getAdjacentCommunes())
                            query = query.concat("'" + adjacent.getName() + "',");
                        query = query.substring(0, query.length() - 1);
                    }
                    session.run(query + "]})");
                }
            }
            st.close();

            session.run("MATCH (a:Commune) " +
                    "MATCH (b:Commune) WHERE a.name IN b.adjacent " +
                    "CREATE UNIQUE (a)-[r:ADJACENT_TO]->(b)");
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
        LOGGER.log(Level.INFO,"Neo4j module finished successfully!");
    }
    
    public static void insertNode(Document document) {
        if(!document.getString(Constant.COMMUNE_FIELD).equals(Constant.UNKNOWN_COMMUNE)) {
            Driver graphDriver = GraphDatabase.driver(Constant.BOLT_ADDRESS,
                    AuthTokens.basic(Constant.BOLT_USERNAME, Constant.BOLT_PASSWORD));
            Session session = graphDriver.session();

            LOGGER.log(Level.INFO,"Creating node...");
            String text = (String)document.get(Constant.TEXT_FIELD);
            String commune= (String)document.get(Constant.COMMUNE_FIELD);
            Date date = document.getDate(Constant.DATE_FIELD);
            String dateString = date.toString();
            Long millis = date.getTime();

            // Create occurrence node
            session.run("CREATE (a:Occurrence {" +
                    "date: '" + dateString + "'," +
                    "millis: " + millis + "," +
                    "text: '"+text+"',"+
                    "commune: '"+commune+"' })");

            // Create relationship user-occurrence
            session.run("MERGE (u:User {" +
                    "username: '" + document.getString(Constant.USER_FIELD) + "'})" +
                    " WITH u MATCH (o:Occurrence) WHERE o.millis = " + millis +
                    " CREATE UNIQUE (u)-[r:REPORTED]->(o)");

            LOGGER.log(Level.INFO,"Creating relationships");
            // Create occurrence-commune relationship
            session.run("MATCH (a:Occurrence) WHERE a.millis = " + millis +
                    " MATCH (b:Commune) WHERE a.commune = b.name" +
                    " CREATE UNIQUE (a)-[r:REPORTED_AT]->(b)");

            // Create occurrence "due to" relationship
            session.run("MATCH (a:Occurrence) WHERE a.millis = " + millis +
                    " MATCH (b:Occurrence) WHERE a <> b AND 0 < a.millis - b.millis <= 10800000 AND " +
                        "(a.commune = b.commune OR b.commune.name IN a.adjacent) " +
                    "CREATE UNIQUE (a)-[r:DUE_TO]->(b)");

            LOGGER.log(Level.INFO,"Relationships created successfully!");

            session.close();
            graphDriver.close();
            LOGGER.log(Level.INFO,"Node created successfully!");
        } else
            LOGGER.log(Level.INFO,"Node has unknown commune. Will be ignored...");
    }
}