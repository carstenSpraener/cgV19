import de.spraener.nxtgen.NextGen;
import de.spraener.nxtgen.cartridge.rest.RESTCartridge;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;
import org.assertj.core.api.Assertions;
import org.assertj.core.api.Condition;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;

import static org.junit.jupiter.api.Assertions.assertTrue;

public class StartGenerator {
    private static String WORKING_DIR ="../cgv19-cloud/build/source-gen/api";
    private RESTCartridge uut = new RESTCartridge();

    void testRestModel() {
        new File(WORKING_DIR).mkdirs();
        System.out.println(new File(".").getAbsolutePath());
        NextGen.setWorkingDir(WORKING_DIR);
        NextGen.main(new String[]{"http://localhost:7001/de.spraener.tinyapp.api"});
    }

    @Test
    public void testRestDemoGeneration() throws Exception {
        String modelDir = "../../demoProjects/restdemo";
        String modelPath = modelDir+"/restDemo.oom";
        String testDir = "./build/source-gen/restDemo";
        try {
            File dir = new File(testDir);
            dir.mkdirs();
            IOUtils.copy(new FileInputStream(modelPath), new FileOutputStream(testDir + "/restDemo.oom"));

            NextGen.setWorkingDir(testDir);
            NextGen.runCartridgeWithName(uut.getName());
            NextGen.main(new String[]{new File(testDir + "/restDemo.oom").getAbsolutePath()});

            File buildGradle = new File(testDir + "/build.gradle");
            assertTrue(buildGradle.exists());
            File[] expectedFiles = new File[]{
                    new File(testDir + "/build.gradle"),
                    new File(testDir + "/Dockerfile"),
                    new File(testDir + "/src/main/java/de/csp/demo/rest/SpringBootApp.java"),
                    new File(testDir + "/src/main/java/de/csp/demo/rest/Student.java"),
                    new File(testDir + "/src/main/java/de/csp/demo/rest/logic/StudentLogic.java"),

                    new File(testDir + "/src/main/java-gen/de/csp/demo/rest/StudentBase.java"),
                    new File(testDir + "/src/main/java-gen/de/csp/demo/rest/logic/StudentLogicBase.java"),
                    new File(testDir + "/src/main/java-gen/de/csp/demo/rest/model/Adresse.java"),
                    new File(testDir + "/src/main/java-gen/de/csp/demo/rest/model/BankAccount.java"),
                    new File(testDir + "/src/main/java-gen/de/csp/demo/rest/model/Customer.java"),
                    new File(testDir + "/src/main/java-gen/de/csp/demo/rest/model/Group.java"),
                    new File(testDir + "/src/main/java-gen/de/csp/demo/rest/model/User.java"),
                    new File(testDir + "/src/main/java-gen/de/csp/demo/rest/model/UserRepository.java"),

                    new File(testDir + "/src/main/java-gen/de/csp/demo/rest/model/Student.java"),
                    new File(testDir + "/src/main/java-gen/de/csp/demo/rest/model/StudentRepository.java"),
            };
            Assertions.assertThat(expectedFiles)
                    .are(new Condition<>(f -> f.exists(), "File not found"))
            ;
        } finally {
            FileUtils.deleteDirectory(new File(testDir));
        }
    }
}
