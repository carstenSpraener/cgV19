import de.spraener.nxtgen.NextGen;
import de.spraener.nxtgen.ProtectionStrategie;
import de.spraener.nxtgen.groovytemplate.GroovyTemplate;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;

import java.io.File;

public class TestRun {
    private GroovyTemplate uut = new GroovyTemplate();

    @Test
    public void testWholeGeneration() throws Exception {
        File dir = new File("./build/demo-app");
        dir.mkdirs();
        NextGen.setWorkingDir("./build/demo-app");
        NextGen.runCartridgeWithName(uut.getName());
        NextGen.main(new String[]{"/demoapp.oom"});

        Assertions.assertThat(new File("./build/demo-app/src/main/java-gen/demoapp/DemoApp.java"))
                .content().contains(
                        "// " + ProtectionStrategie.GENERATED_LINE,
                        "private String name;",
                        "public DemoApp setName( String value )",
                        "public String getName()"
                )
        ;
    }
}

