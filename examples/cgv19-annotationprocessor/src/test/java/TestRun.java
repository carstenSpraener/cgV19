import de.spraener.nxtgen.NextGen;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.nio.file.Files;

import static org.junit.jupiter.api.Assertions.assertTrue;

public class TestRun {

    @Test
    public void testWholeGeneration() throws Exception {
        File dir = new File("./build/demo-app");
        deleteRecursively(dir);
        dir.mkdirs();

        NextGen.setWorkingDir("./build/demo-app");
        NextGen.runCartridgeWithName("PoJo-Cartridge");
        NextGen.main(new String[]{"java-ap://src/main/java-model"});

        File genDir = new File(dir, "src/main/java/de/spraener/nxtgen/apdemo/model");
        File genBaseDir = new File(dir, "src/main/java-gen/de/spraener/nxtgen/apdemo/model");

        File person = new File(genDir, "Person.java");
        File address = new File(genDir, "Address.java");
        File personBase = new File(genBaseDir, "PersonBase.java");
        File addressBase = new File(genBaseDir, "AddressBase.java");

        assertTrue(person.exists(), "Person.java must be generated into src/main/java");
        assertTrue(address.exists(), "Address.java must be generated into src/main/java");
        assertTrue(personBase.exists(), "PersonBase.java must be generated into src/main/java-gen");
        assertTrue(addressBase.exists(), "AddressBase.java must be generated into src/main/java-gen");

        String personCode = Files.readString(person.toPath());
        assertTrue(personCode.contains("extends PersonBase"), "Person must extend the generated PersonBase");

        String personBaseCode = Files.readString(personBase.toPath());
        assertTrue(personBaseCode.contains("private java.lang.String name"), "PersonBase must contain the attribute 'name'");
        assertTrue(personBaseCode.contains("public Address getAddress()"), "PersonBase must contain the association 'address'");
    }

    private static void deleteRecursively(File file) throws Exception {
        if (file.isDirectory()) {
            File[] children = file.listFiles();
            if (children != null) {
                for (File child : children) {
                    deleteRecursively(child);
                }
            }
        }
        if (file.exists()) {
            assertTrue(file.delete(), "Could not delete " + file);
        }
    }

}
