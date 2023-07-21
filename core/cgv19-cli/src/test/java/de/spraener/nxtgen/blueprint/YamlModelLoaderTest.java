package de.spraener.nxtgen.blueprint;

import de.spraener.nxtgen.model.Model;
import de.spraener.nxtgen.model.ModelElement;
import org.junit.jupiter.api.Test;

import java.io.ByteArrayInputStream;
import java.io.InputStream;

import static org.junit.jupiter.api.Assertions.*;

class YamlModelLoaderTest {

    @Test
    void loadModel() {
        String yaml = """
                p1:
                    p2:
                        p3: value
                        p4: 2.0
                name: Name
                """;
        InputStream is = new ByteArrayInputStream(yaml.getBytes());
        YamlModelLoader loader = new YamlModelLoader("TEST", null, null);
        Model m = loader.loadModel(is);
        assertNotNull(m);
        ModelElement me = m.getModelElements().get(0);
        assertEquals("TEST", me.getStereotypes().get(0).getName());
        assertEquals("value", me.getProperty("p1.p2.p3"));
        assertEquals("2.0", me.getProperty("p1.p2.p4"));
        assertEquals("Name", me.getProperty("name"));
    }
}
