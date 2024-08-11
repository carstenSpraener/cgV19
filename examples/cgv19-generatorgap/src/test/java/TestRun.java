import de.spraener.nxtgen.NextGen;
import de.spraener.nxtgen.examples.generatorgap.GeneratorGap;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;

import java.io.File;

import static org.junit.jupiter.api.Assertions.assertTrue;

public class TestRun {
    public static final String WORK_DIR = "./build/demo-app";
    public static final String TEMPLATE_CODE = WORK_DIR + "/src/main/java/demoapp/DemoApp.java";
    public static final String BASE_CODE = WORK_DIR + "/src/main/java-gen/demoapp/DemoAppBase.java";

    private GeneratorGap uut = new GeneratorGap();
    private String[] expectedFiles = new String[]{
            TEMPLATE_CODE,
            BASE_CODE,
    };

    @Test
    public void testWholeGeneration() throws Exception {
        File dir = new File("./build/demo-app");
        dir.mkdirs();
        NextGen.setWorkingDir(WORK_DIR);
        NextGen.runCartridgeWithName(uut.getName());
        NextGen.main(new String[]{"/demoapp.oom"});

        for (String fileName : expectedFiles) {
            File f = new File(fileName);
            assertTrue(f.exists());
        }

        File templateFile = new File(TEMPLATE_CODE);
        Assertions.assertThat(templateFile)
                .content()
                .contains("public class DemoApp extends DemoAppBase")
                .doesNotContain("private String name;")
        ;

        File baseFile = new File(BASE_CODE);
        Assertions.assertThat(baseFile)
                .content().contains(
                        "public class DemoAppBase",
                        "private String name;",
                        "public String getName()",
                        "public void setName( String value)"
                        );
    }
}

