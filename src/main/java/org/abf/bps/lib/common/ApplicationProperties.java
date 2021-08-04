package org.abf.bps.lib.common;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

/**
 * Application properties that deals with things like location of the blast database, index paths etc.
 * This is intended to be loaded on application startup
 *
 * @author Hector Plahar
 */
public class ApplicationProperties {

    private final Properties properties = new Properties();
    private static ApplicationProperties INSTANCE;

    public static ApplicationProperties getInstance() {
        if (INSTANCE == null)
            INSTANCE = new ApplicationProperties();
        return INSTANCE;
    }

    public void initialize(final String file) {
        InputStream in;
        try {
            in = new FileInputStream(file);
            properties.load(in);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public String get(ConfigProperty property) {
        return properties.getProperty(property.name());
    }
}
