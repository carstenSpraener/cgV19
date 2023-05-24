package de.spraener.nxtgen;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.Map;
import java.util.Properties;

import static de.spraener.nxtgen.NextGen.LOGGER;

public class CGV19 {
    private static Properties props = null;

    private static Properties getProperties() {
        if( props != null ) {
            return props;
        }
        File propFile = new File(".cgv19.properties");
        props = new Properties();
        if( propFile.exists() ) {
            LOGGER.info("Loading cgv19 properties form .cgv19.properties file into GCV19.definitions.");
            try (FileReader propReader = new FileReader(propFile)) {
                props.load(new FileReader(propFile));
            } catch( IOException xc ) {
                LOGGER.warning("PropertyFile .cgv19.properties could not be read. Error: "+xc.getMessage());
            }
        }
        LOGGER.info("Adding environment variable with prefix 'cgv19_' or 'CGV19_' to CGV19.definitions.");
        for(Map.Entry e : System.getenv().entrySet() ) {
            String key = e.getKey().toString().toLowerCase();
            if( key.startsWith("cgv19_")) {
                String value = System.getenv().get(key);
                props.setProperty(key, value);
            }
        }
        return props;
    }

    public static String definitionOf(String key) {
        return getProperties().getProperty(key, key);
    }
}
