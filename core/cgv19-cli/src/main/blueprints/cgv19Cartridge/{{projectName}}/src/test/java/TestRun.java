import de.spraener.nxtgen.NextGen;
import {{rootPackage}}.{{cartridgeName}};
import org.junit.jupiter.api.Test;

import java.io.File;

public class TestRun {
    private {{cartridgeName}} uut = new {{cartridgeName}}();
    @Test
    public void testWholeGeneration() throws Exception {
        File dir = new File("./build/demo-app");
        dir.mkdirs();
        NextGen.setWorkingDir("./build/demo-app");
        NextGen.runCartridgeWithName(uut.getName());
        NextGen.main(new String[]{"/demoapp.oom"});
    }
}
