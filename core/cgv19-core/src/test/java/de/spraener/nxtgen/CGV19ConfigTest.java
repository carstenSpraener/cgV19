package de.spraener.nxtgen;

import org.junit.jupiter.api.Test;

import java.io.ByteArrayInputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.util.HashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class CGV19ConfigTest {
    private Map<String,String> envMap = new HashMap<>();
    private String properties = """
            propA=definitionFromPropertiesA
            propB=definitionFromPropertiesB
            nothing=empty
            """;

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
}
