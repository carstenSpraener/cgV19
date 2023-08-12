import de.spraener.nxtgen.NextGen;
import de.spraener.nxtgen.angular.Cgv19Angular;
import org.junit.jupiter.api.Test;

import java.io.File;

public class Cgv19AngularTestRun {
    private Cgv19Angular uut = new Cgv19Angular();
    @Test
    public void testWholeGeneration() throws Exception {
        File dir = new File("./build/demo-app");
        dir.mkdirs();
        NextGen.setWorkingDir("./build/demo-app");
        NextGen.runCartridgeWithName(uut.getName());
        NextGen.main(new String[]{"/demoapp.oom"});
    }
}

