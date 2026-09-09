import de.spraener.nxtgen.NextGen;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;

import java.io.File;

public class TestRun {

    @Test
    public void testAiCodeBlockGeneration() throws Exception {
        File dir = new File("./build/demo-app");
        dir.mkdirs();
        NextGen.setWorkingDir("./build/demo-app");
        NextGen.runCartridgeWithName("AiCodeBlock");
        NextGen.main(new String[]{"de.spraener.nxtgen.aicb.oom"});

        Assertions.assertThat(new File("./build/demo-app/src/main/java-gen/demoapp/GreetingService.java"))
                .exists()
                .content().contains(
                        "class GreetingService",
                        "private String name;",
                        "private int age;"
                );
    }
}
