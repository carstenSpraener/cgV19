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
 * TODO: Document this class
 */
public class CGV19 {
    private Properties props = null;
    private static CGV19 myInstance = null;
    private Supplier<Reader> propertyReaderSupplier = this::getPropertyReader;
    private Supplier<Map<String,String>> envMapSupplier = System::getenv;

    public static CGV19 getInstance(Consumer<CGV19>... modifiers) {
        if (myInstance == null) {
            myInstance = new CGV19();
            if (modifiers != null) {
                for (Consumer<CGV19> m : modifiers) {
                    m.accept(myInstance);
                }
            }
        } else {
            if (modifiers != null && modifiers.length > 0) {
                LOGGER.warning("Calling CGV19.getInstance with modifiers to existent instance. Modifiers were not applied!");
            }
        }
        return myInstance;
    }

    public CGV19 withPropertyReaderSupplier(Supplier<Reader> propertyReaderSupplier) {
        this.propertyReaderSupplier = propertyReaderSupplier;
        return this;
    }

    public CGV19 withEnvMapSupplier(Supplier<Map<String, String>> envMapSupplier) {
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
        LOGGER.info("Adding environment variable with prefix 'cgv19_' or 'CGV19_' to CGV19.definitions removing prefix.");
        Map<String,String> envMap = this.envMapSupplier.get();
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
