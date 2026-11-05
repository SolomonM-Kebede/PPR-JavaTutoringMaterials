package org.texttechnologylab.ppr.KEBEDE;

import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import com.mongodb.client.MongoDatabase;
import com.mongodb.client.MongoCollection;
import com.mongodb.MongoClientSettings;
import com.mongodb.MongoCredential;
import com.mongodb.ServerAddress;
import org.bson.Document;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * MongoDB Database Copy Utility
 * Copies all collections from one MongoDB database to another with authentication
 * Uses Properties file for configuration
 * @author solo
 */
public class MongoDBCopy {


    private static final Logger log = LoggerFactory.getLogger(MongoDBCopy.class);

    public static void main(String[] args) {
        MongoClient sourceClient = null;
        MongoClient destClient = null;

        try {
            // Load configuration from properties file
            System.out.println("MongoDB Database Copy Tool\n");
            System.out.println("Loading configuration...");

            String configFile = args.length > 0 ? args[0] : "Prop.properties";
            PropConfig config = PropConfig.load(configFile);

            System.out.println("Configuration loaded from: " + configFile);

            // Get configuration values
            int batchSize = config.getBatchSize();
            boolean dropExisting = config.isDropExistingCollections();


            // Connect to source database
            sourceClient = createMongoClient(
                    config.getSourceHost(),
                    config.getSourcePort(),
                    config.getSourceUsername(),
                    config.getSourcePassword(),
                    config.getSourceAuthDatabase()
            );
            MongoDatabase sourceDB = sourceClient.getDatabase(config.getSourceDatabase());
            System.out.println("Connected to source: " + config.getSourceDatabase());

            // Connect to destination database
            System.out.println("\nConnecting to destination database...");
            destClient = createMongoClient(
                    config.getDestHost(),
                    config.getDestPort(),
                    config.getDestUsername(),
                    config.getDestPassword(),
                    config.getDestAuthDatabase()
            );
            MongoDatabase destDB = destClient.getDatabase(config.getDestDatabase());

            System.out.println("Connected to destination: " + config.getDestDatabase());

            // Get all collections from source database
            System.out.println("\nRetrieving collections...");
            List<String> collectionNames = new ArrayList<>();
            for (String name : sourceDB.listCollectionNames()) {
                collectionNames.add(name);
            }
            System.out.println("Found " + collectionNames.size() + " collections to copy");

            // Copy each collection
            int totalDocumentsCopied = 0;
            for (String collectionName : collectionNames) {
                System.out.println("\n Copying collection: " + collectionName);

                MongoCollection<Document> sourceCollection = sourceDB.getCollection(collectionName);
                MongoCollection<Document> destCollection = destDB.getCollection(collectionName);

                // Drop existing collection if configured
                if (dropExisting) {
                    System.out.println("  Dropping existing collection...");
                    destCollection.drop();
                }

                // Count documents in source
                long sourceCount = sourceCollection.countDocuments();
                System.out.println("  Source documents: " + sourceCount);

                if (sourceCount == 0) {
                    System.out.println("  Skipping empty collection");
                    continue;
                }

                // Copy documents in batches
                List<Document> batch = new ArrayList<>();
                long copiedCount = 0;

                for (Document doc : sourceCollection.find()) {
                    batch.add(doc);

                    // Insert batch when it reaches the batch size
                    if (batch.size() >= batchSize) {
                        destCollection.insertMany(batch);
                        copiedCount += batch.size();
                        batch.clear();
                        System.out.print("\r  Progress: " + copiedCount + "/" + sourceCount);
                    }
                }

                // Insert remaining documents
                if (!batch.isEmpty()) {
                    destCollection.insertMany(batch);
                    copiedCount += batch.size();
                }

                System.out.println("\r  Copied " + copiedCount + " documents");
                totalDocumentsCopied += (int) copiedCount;

                // Verify count
                long destCount = destCollection.countDocuments();
                if (destCount != sourceCount) {
                    System.out.println("  Warning: Document count mismatch! Source: " +
                            sourceCount + ", Destination: " + destCount);
                }
            }

            // Summary
            System.out.println("\n Copy Complete");
            System.out.println("Collections copied: " + collectionNames.size());
            System.out.println("Total documents copied: " + totalDocumentsCopied);

        } catch (IOException e) {
            System.err.println("\n Error loading configuration: " + e.getMessage());
            System.err.println("Make sure Prop.properties file exists in the current directory");
            System.exit(1);

        } catch (Exception e) {
            log.error(e.getMessage(), e);
            System.exit(1);

        } finally {
            // Close connections
            if (sourceClient != null) {
                sourceClient.close();
                System.out.println("\nSource connection closed");
            }
            if (destClient != null) {
                destClient.close();
                System.out.println("Destination connection closed");
            }
        }
    }

    /**
     * Create MongoDB client with authentication
     */
    private static MongoClient createMongoClient(String host, int port,
                                                 String username, String password,
                                                 String authDatabase) {
        // Create credential
        MongoCredential credential = MongoCredential.createCredential(
                username,
                authDatabase,
                password.toCharArray()
        );

        // Create server address
        ServerAddress serverAddress = new ServerAddress(host, port);

        // Build client settings
        MongoClientSettings settings = MongoClientSettings.builder()
                .applyToClusterSettings(builder ->
                        builder.hosts(List.of(serverAddress)))
                .credential(credential)
                .build();

        // Create and return client
        return MongoClients.create(settings);
    }
}