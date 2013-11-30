package edu.ringtests.view;

import java.io.*;
import java.util.Properties;

/**
 * @author Kamil Sikora
 *         Data: 29.11.13
 */
public class Configuration {
    private final String PROPERTIES_FILE = "properties.xml";
    public static String FORGE_PATH_KEY = "forgePath";
    public static String LAST_PROJECT_KEY = "last_project_path";

    private Properties properties;
    private File propertiesFile;

    public Configuration() {
        properties = new Properties();
        propertiesFile = new File(PROPERTIES_FILE);

        if (propertiesFile.exists()) {
            try {
                FileInputStream is = new FileInputStream(PROPERTIES_FILE);
                properties.loadFromXML(is);
                is.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        } else
            try {
                propertiesFile.createNewFile();
            } catch (IOException e) {
                e.printStackTrace();
            }
    }

    public String getProperty(String key) {
        return (String) properties.get(key);
    }

    /**
     * @return false if there wasn't such property, true if there was.
     */
    public boolean addProperty(String key, Object value) {
        return properties.put(key, value) == null ? false : true;
    }

    public void save() {
        try {
            properties.storeToXML(new FileOutputStream(propertiesFile), "simulationRunner");
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
