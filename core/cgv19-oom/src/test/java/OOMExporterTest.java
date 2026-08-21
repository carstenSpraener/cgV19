import de.spraener.nxtgen.oom.OOModelBuilder;
import de.spraener.nxtgen.oom.model.MClass;
import de.spraener.nxtgen.oom.model.MPackage;
import de.spraener.nxtgen.oom.model.OOModel;
import de.spraener.nxtgen.oom.model.OOMExporter;
import org.junit.Test;

import java.nio.file.Files;
import java.nio.file.Path;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class OOMExporterTest {

    @Test
    public void shouldExportEmptyModel() {
        OOModel model = new OOModel();
        String result = new OOMExporter().export(model);
        assertEquals("import de.spraener.nxtgen.groovy.ModelDSL\n\nModelDSL.make {\n}", result);
    }

    @Test
    public void shouldExportPackage() {
        OOModel model = OOModelBuilder.createModel(m -> {
            OOModelBuilder.createPackage(m, "de.test");
        });
        String result = new OOMExporter().export(model);
        assertTrue(result.contains("mPackage {"));
        assertTrue(result.contains("name '''de.test'''"));
    }

    @Test
    public void shouldExportClassWithStereotype() {
        OOModel model = OOModelBuilder.createModel(m -> {
            MPackage pkg = OOModelBuilder.createPackage(m, "de.test");
            MClass clz = OOModelBuilder.createMClass(pkg, "Person");
            OOModelBuilder.addStereotype(clz, "Ressource");
        });
        String result = new OOMExporter().export(model);
        assertTrue(result.contains("stereotype '''Ressource'''"));
    }

    @Test
    public void shouldExportClassWithAttributes() {
        OOModel model = OOModelBuilder.createModel(m -> {
            MPackage pkg = OOModelBuilder.createPackage(m, "de.test");
            MClass clz = OOModelBuilder.createMClass(pkg, "Person");
            clz.createAttribute("name", "String");
        });
        String result = new OOMExporter().export(model);
        assertTrue(result.contains("mAttribute {"));
        assertTrue(result.contains("name '''name'''"));
        assertTrue(result.contains("type '''String'''"));
    }

    @Test
    public void shouldExportClassWithOperations() {
        OOModel model = OOModelBuilder.createModel(m -> {
            MPackage pkg = OOModelBuilder.createPackage(m, "de.test");
            MClass clz = OOModelBuilder.createMClass(pkg, "Person");
            OOModelBuilder.createOperation(clz, "doSomething", "void");
        });
        String result = new OOMExporter().export(model);
        assertTrue(result.contains("mOperation {"));
        assertTrue(result.contains("name '''doSomething'''"));
    }

    @Test
    public void shouldExportTaggedValues() {
        OOModel model = OOModelBuilder.createModel(m -> {
            MPackage pkg = OOModelBuilder.createPackage(m, "de.test");
            MClass clz = OOModelBuilder.createMClass(pkg, "Person");
            OOModelBuilder.addStereotype(clz, "Ressource", "key=value");
        });
        String result = new OOMExporter().export(model);
        assertTrue(result.contains("taggedValue '''key''', '''value'''"));
    }

    @Test
    public void shouldExportNestedPackages() {
        OOModel model = OOModelBuilder.createModel(m -> {
            MPackage outer = OOModelBuilder.createPackage(m, "de");
            OOModelBuilder.createPackage(outer, "test");
        });
        String result = new OOMExporter().export(model);
        String expected = "import de.spraener.nxtgen.groovy.ModelDSL\n"
                + "\n"
                + "ModelDSL.make {\n"
                + "  mPackage {\n"
                + "    name '''de'''\n"
                + "    mPackage {\n"
                + "      name '''test'''\n"
                + "    }\n"
                + "  }\n"
                + "}";
        assertEquals(expected, result);
    }

    @Test
    public void shouldExportToPath() throws Exception {
        OOModel model = OOModelBuilder.createModel(m -> {
            OOModelBuilder.createPackage(m, "de.test");
        });
        Path target = Files.createTempFile("oom-exporter-test", ".oom");
        try {
            new OOMExporter().export(model, target);
            String result = new String(Files.readAllBytes(target));
            assertTrue(result.contains("mPackage {"));
            assertTrue(result.contains("name '''de.test'''"));
        } finally {
            Files.deleteIfExists(target);
        }
    }
}
