package org.texttechnologylab.ppr.KEBEDE;


import java.io.IOException;
import java.util.Properties;

public class PropConfig {

    private final Properties properties;

    private PropConfig(Properties properties) {
        this.properties = properties;
    }

    public static PropConfig load(String filename) throws IOException {
        Properties props = new Properties();

        try (var is = PropConfig.class
                .getClassLoader()
                .getResourceAsStream(filename)) {

            if (is == null) {
                throw new IOException("Properties file not found on classpath: " + filename);
            }

            props.load(is);
        }

        return new PropConfig(props);
    }


   //source
    public String getSourceHost() {
        return properties.getProperty("source.host");
    }

    public int getSourcePort() {
        return Integer.parseInt(properties.getProperty("source.port"));
    }

    public String getSourceDatabase() {
        return properties.getProperty("source.database");
    }

    public String getSourceUsername() {
        return properties.getProperty("source.username");
    }

    public String getSourcePassword() {
        return properties.getProperty("source.password");
    }

    public String getSourceAuthDatabase() {
        return properties.getProperty(
                "source.auth.database",
                getSourceDatabase()
        );
    }


    //destination
    public String getDestHost() {
        return properties.getProperty("dest.host");
    }

    public int getDestPort() {
        return Integer.parseInt(properties.getProperty("dest.port"));
    }

    public String getDestDatabase() {
        return properties.getProperty("dest.database");
    }

    public String getDestUsername() {
        return properties.getProperty("dest.username");
    }

    public String getDestPassword() {
        return properties.getProperty("dest.password");
    }

    public String getDestAuthDatabase() {
        return properties.getProperty(
                "dest.auth.database",
                getDestDatabase()
        );
    }

    //copy settings
    public int getBatchSize() {
        return Integer.parseInt(properties.getProperty("batch.size", "1000"));
    }

    public boolean isDropExistingCollections() {
        return Boolean.parseBoolean(
                properties.getProperty("drop.existing.collections", "false")
        );
    }
}

