package org.texttechnology.ppr.tutor.Kebede;

import io.javalin.Javalin;
import io.javalin.http.staticfiles.Location;
import io.javalin.rendering.template.JavalinFreemarker;
import org.neo4j.graphdb.GraphDatabaseService;
import org.texttechnology.ppr.tutor.Kebede.database.Neo4jConnection;
import org.texttechnology.ppr.tutor.Kebede.factory.StaffFactory;
import org.texttechnology.ppr.tutor.Kebede.helper.GraphVisualizer;
import org.texttechnology.ppr.tutor.Kebede.helper.XmlParser;
import org.texttechnology.ppr.tutor.Kebede.rest.RESTHandler;
import org.texttechnology.ppr.tutor.Kebede.rest.TemplateHandler;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;


/**
 * This is the main class which serves for parsing,
 * creating Embedded Neo4Jdb and run the webApp. This material prepared for teaching purpose.
 */
public class Main {

    public static void main(String[] args) {

        Properties props = loadConfiguration();
        if (props == null) return;

        String xmlSourcePath = props.getProperty("SourcePath");
        String neo4jPath = props.getProperty("Neo4jPath");

        if (xmlSourcePath == null || neo4jPath == null) {
            System.out.println("Missing SourcePath or Neo4jPath in config.properties");
            return;
        }

        try {
            // Initialize Neo4j
            System.out.println("Initializing Neo4j...");
            Neo4jConnection connection = new Neo4jConnection(neo4jPath);
            StaffFactory staffFactory = new StaffFactory(connection);
            System.out.println("Neo4j initialized.");

            GraphDatabaseService db = connection.getDatabase();

            // Reset database if needed
            if (staffFactory.count() > 0) {
                System.out.println("Cleaning database...");
                staffFactory.deleteAll();
            }

            // Load XML data
            System.out.println("Parsing XML...");
            XmlParser parser = new XmlParser();
            parser.parseFileWithConnection(xmlSourcePath, connection);

            // Visualize graph
            GraphVisualizer.printStaffSalaryGraph(db);

            // start javalin
            Javalin app = Javalin.create(config -> {
                config.fileRenderer(new JavalinFreemarker());
                config.staticFiles.add("/templates", Location.CLASSPATH);
                config.bundledPlugins.enableDevLogging();
            });

            // Register REST & template routes
            new RESTHandler(connection).registerRoutes(app);
            new TemplateHandler(connection).registerRoutes(app);

            app.start(7777);
            System.out.println("Server running at http://localhost:7777");

            // Shutdown Neo4j when server stops
            app.events(ev -> ev.serverStopped(() -> {
                System.out.println("Shutting down Neo4j...");
                connection.shutdown();
                System.out.println("Neo4j closed.");
            }));

            // Keep JVM alive
            Thread.currentThread().join();

        } catch (Exception e) {
            System.out.println("Application failed: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private static Properties loadConfiguration() {
        Properties props = new Properties();
        try (InputStream inputStream = Main.class.getClassLoader().getResourceAsStream("config.properties")) {
            if (inputStream == null) {
                System.out.println("config.properties not found");
                return null;
            }

            props.load(inputStream);
            System.out.println("Configuration loaded: SourcePath=" + props.getProperty("SourcePath") +
                    ", Neo4jPath=" + props.getProperty("Neo4jPath"));

            return props;

        } catch (IOException e) {
            System.out.println("Error loading config.properties: " + e.getMessage());
            return null;
        }
    }
}