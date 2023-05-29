package de.spraener.nxtgen;

import org.junit.Test;

import java.io.ByteArrayInputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.util.HashMap;
import java.util.Map;

import static org.junit.Assert.*;

public class CGV19Test {
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
        CGV19 cgv19 = CGV19.getInstance(
                c -> c.withEnvMapSupplier(()->this.envMap),
                c -> c.withPropertyReaderSupplier(this::getPropertiesReader)
        );
        this.envMap.put("CGV19_TEST", "definitionFromEnv1");
        this.envMap.put("cgv19_TEST2", "definitionFromEnv2");
        this.envMap.put("nix", "notRead");

        assertEquals("nix", CGV19.definitionOf("nix"));
        assertEquals("definitionFromEnv1", CGV19.definitionOf("TEST"));
        assertEquals("definitionFromEnv2", CGV19.definitionOf("TEST2"));
        assertEquals("definitionFromPropertiesA", CGV19.definitionOf("propA"));
        assertEquals("definitionFromPropertiesB", CGV19.definitionOf("propB"));
        assertEquals("empty", CGV19.definitionOf("nothing"));
    }
}
