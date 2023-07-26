package de.spraener.nxtgen.blueprint;

import de.spraener.nxtgen.incubator.BlueprintCompiler;
import de.spraener.nxtgen.model.Model;
import de.spraener.nxtgen.model.ModelElement;
import org.junit.jupiter.api.Test;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.util.Collections;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class YamlModelLoaderTest {

    @Test
    void loadModel() {
        BlueprintCompiler bpcMock = mock(BlueprintCompiler.class);
        when(bpcMock.getRequiredValues()).thenReturn(Collections.emptyList());
        String yaml = """
                p1:
                    p2:
                        p3: value
                        p4: 2.0
                name: Name
                """;
        InputStream is = new ByteArrayInputStream(yaml.getBytes());
        YamlModelLoader loader = new YamlModelLoader("TEST", bpcMock, null);
        Model m = loader.loadModel(is);
        assertNotNull(m);
        ModelElement me = m.getModelElements().get(0);
        assertEquals("TEST", me.getStereotypes().get(0).getName());
        assertEquals("value", me.getProperty("p1.p2.p3"));
        assertEquals("2.0", me.getProperty("p1.p2.p4"));
        assertEquals("Name", me.getProperty("name"));
    }
}
