package de.spraener.nxtgen;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.ByteArrayInputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Supplier;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class CGV19ConfigTest {
    private Map<String,String> envMap = new HashMap<>();
    private String properties = """
            propA=definitionFromPropertiesA
            propB=definitionFromPropertiesB
            nothing=empty
            """;

    // Snapshot of the CGV19Config singleton state, taken before this test mutates it
    private Object previousInstance;
    private Object previousProps;
    private Supplier<Reader> previousPropertyReaderSupplier;
    private Supplier<Map<String, String>> previousEnvMapSupplier;

    @BeforeEach
    public void saveConfigState() throws Exception {
        previousInstance = readStaticField("myInstance");
        if (previousInstance instanceof CGV19Config config) {
            previousProps = readField(config, "props");
            previousPropertyReaderSupplier = (Supplier<Reader>) readField(config, "propertyReaderSupplier");
            previousEnvMapSupplier = (Supplier<Map<String, String>>) readField(config, "envMapSupplier");
        } else {
            previousProps = null;
            previousPropertyReaderSupplier = null;
            previousEnvMapSupplier = null;
        }
        // Force a fresh singleton so the modifiers in testPropertiesReading are actually applied,
        // regardless of whether another test already created the singleton.
        writeStaticField("myInstance", null);
    }

    @AfterEach
    public void restoreConfigState() throws Exception {
        if (previousInstance == null) {
            // The singleton did not exist before this test: leave it as if the test never ran.
            writeStaticField("myInstance", null);
        } else {
            CGV19Config config = (CGV19Config) previousInstance;
            // Restore the suppliers via the existing setters ...
            config.withPropertyReaderSupplier(previousPropertyReaderSupplier);
            config.withEnvMapSupplier(previousEnvMapSupplier);
            // ... and the cached properties (no public reset API exists for it).
            writeField(config, "props", previousProps);
            writeStaticField("myInstance", config);
        }
    }

    private Reader getPropertiesReader() {
        InputStreamReader reader = new InputStreamReader(new ByteArrayInputStream(properties.getBytes()));
        return reader;
    }

    @Test
    public void testPropertiesReading() throws Exception {
        CGV19Config cgv19Config = CGV19Config.getInstance(
                c -> c.withEnvMapSupplier(()->this.envMap),
                c -> c.withPropertyReaderSupplier(this::getPropertiesReader)
        );
        this.envMap.put("CGV19_TEST", "definitionFromEnv1");
        this.envMap.put("cgv19_TEST2", "definitionFromEnv2");
        this.envMap.put("nix", "notRead");

        assertEquals("nix", CGV19Config.definitionOf("nix"));
        assertEquals("definitionFromEnv1", CGV19Config.definitionOf("TEST"));
        assertEquals("definitionFromEnv2", CGV19Config.definitionOf("TEST2"));
        assertEquals("definitionFromPropertiesA", CGV19Config.definitionOf("propA"));
        assertEquals("definitionFromPropertiesB", CGV19Config.definitionOf("propB"));
        assertEquals("empty", CGV19Config.definitionOf("nothing"));
    }

    // --- Reflection helpers (test-only; CGV19Config has no public reset API for the singleton/props) ---

    private static Object readStaticField(String name) throws ReflectiveOperationException {
        Field f = CGV19Config.class.getDeclaredField(name);
        f.setAccessible(true);
        return f.get(null);
    }

    private static void writeStaticField(String name, Object value) throws ReflectiveOperationException {
        Field f = CGV19Config.class.getDeclaredField(name);
        f.setAccessible(true);
        f.set(null, value);
    }

    private static Object readField(Object instance, String name) throws ReflectiveOperationException {
        Field f = CGV19Config.class.getDeclaredField(name);
        f.setAccessible(true);
        return f.get(instance);
    }

    private static void writeField(Object instance, String name, Object value) throws ReflectiveOperationException {
        Field f = CGV19Config.class.getDeclaredField(name);
        f.setAccessible(true);
        f.set(instance, value);
    }
}
