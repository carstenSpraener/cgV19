import de.spraener.nxtgen.NextGen;
import de.spraener.nxtgen.mustache.Mustache;
import org.junit.jupiter.api.Test;

import java.io.File;

public class TestRun {
    private Mustache uut = new Mustache();
    @Test
    public void testWholeGeneration() throws Exception {
        File dir = new File("./build/demo-app");
        dir.mkdirs();
        NextGen.setWorkingDir("./build/demo-app");
        NextGen.runCartridgeWithName(uut.getName());
        NextGen.main(new String[]{"/demoapp.oom"});
    }
}

