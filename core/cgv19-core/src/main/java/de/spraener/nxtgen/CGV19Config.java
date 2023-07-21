package de.spraener.nxtgen;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.Reader;
import java.util.Map;
import java.util.Properties;
import java.util.function.Consumer;
import java.util.function.Supplier;

import static de.spraener.nxtgen.NextGen.LOGGER;

/**
 * Responsibility:
 * <p>
 * This class provides values from the environment or from a property file to the generators. The generators
 * can use this values in their logic or templates or whatnot.
 * <p>
 * It reads properties from a file ".cgv19.properties" and also from the environment. The properties form
 * the ".cgv19.properties" file are read in as they are.
 * <p>
 * The environment variables have to have a prefix
 * <b>CGV19_</b> (not case-sensitive) to be accepted as configuration values. The prefix "cgv19_" is removed
 * from the environment variables name. So an environment value of "CGV19_java_persistence=jakarta.persistence"
 * is available under "java_persistence" in CGV19.
 * <p>
 * To access the definition of a specific value the method "definitionOf(key)" can be used.
 */
public class CGV19Config {
    private Properties props = null;
    private static CGV19Config myInstance = null;
    private Supplier<Reader> propertyReaderSupplier = this::getPropertyReader;
    private Supplier<Map<String, String>> envMapSupplier = System::getenv;

    public static CGV19Config getInstance(Consumer<CGV19Config>... modifiers) {
        if (myInstance == null) {
            myInstance = new CGV19Config();
            if (modifiers != null) {
                for (Consumer<CGV19Config> m : modifiers) {
                    m.accept(myInstance);
                }
            }
        } else {
            if (modifiers != null && modifiers.length > 0) {
                LOGGER.warning("Calling CGV19Config.getInstance with modifiers to existent instance. Modifiers were not applied!");
            }
        }
        return myInstance;
    }

    public CGV19Config withPropertyReaderSupplier(Supplier<Reader> propertyReaderSupplier) {
        this.propertyReaderSupplier = propertyReaderSupplier;
        return this;
    }

    public CGV19Config withEnvMapSupplier(Supplier<Map<String, String>> envMapSupplier) {
        this.envMapSupplier = envMapSupplier;
        return this;
    }

    private Reader getPropertyReader() {
        try {
            LOGGER.info("Loading cgv19 properties form .cgv19.properties file into GCV19.definitions.");
            return new FileReader(".cgv19.properties");
        } catch (FileNotFoundException fnfXC) {
            return null;
        }
    }


    private Properties getProperties() {
        if (props != null) {
            return props;
        }
        props = new Properties();
        Reader propReader = this.propertyReaderSupplier.get();
        if (propReader != null) {
            try (propReader) {
                props.load(propReader);
            } catch (IOException xc) {
                LOGGER.warning("PropertyFile .cgv19.properties could not be read. Error: " + xc.getMessage());
            }
        }
        LOGGER.info("Adding environment variable with prefix 'cgv19_' or 'CGV19_' to CGV19Config.definitions removing prefix.");
        Map<String, String> envMap = this.envMapSupplier.get();
        for (Map.Entry e : envMap.entrySet()) {
            String key = e.getKey().toString();
            if (key.toLowerCase().startsWith("cgv19_")) {
                String value = envMap.get(key);
                props.setProperty(key.substring(6), value);
            }
        }
        return props;
    }

    public static String definitionOf(String key) {
        return getInstance().getProperties().getProperty(key, key);
    }
}
