import de.spraener.nxtgen.NextGen;
import de.spraener.nxtgen.codetarget.CodeTarget;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;

import java.io.File;

public class TestRun {
    private CodeTarget uut = new CodeTarget();
    @Test
    public void testWholeGeneration() throws Exception {
        File dir = new File("./build/demo-app");
        dir.mkdirs();
        NextGen.setWorkingDir("./build/demo-app");
        NextGen.runCartridgeWithName(uut.getName());
        NextGen.main(new String[]{"/demoapp.oom"});

        Assertions.assertThat(new File("./build/demo-app/src/main/java-gen/demoapp/DemoApp.java"))
                .content().contains(
                        "public class DemoApp {\n" +
                        "    private static final Logger LOGGER = Logger.getLogger(DemoApp.class.getName());");
    }
}

