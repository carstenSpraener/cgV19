import de.spraener.nxtgen.NextGen;
import de.spraener.nxtgen.examples.generatorgap.GeneratorGap;
import org.junit.jupiter.api.Test;

import java.io.File;

public class TestRun {
    private GeneratorGap uut = new GeneratorGap();
    @Test
    public void testWholeGeneration() throws Exception {
        File dir = new File("./build/demo-app");
        dir.mkdirs();
        NextGen.setWorkingDir("./build/demo-app");
        NextGen.runCartridgeWithName(uut.getName());
        NextGen.main(new String[]{"/demoapp.oom"});
    }
}

