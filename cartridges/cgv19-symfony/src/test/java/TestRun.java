import de.spraener.nxtgen.NextGen;
import de.spraener.nxtgen.symfony.SymfonyCartridge;
import org.junit.jupiter.api.Test;

import java.io.File;

public class TestRun {
    private SymfonyCartridge uut = new SymfonyCartridge();

    @Test
    public void testWholeGeneration() throws Exception {
        File dir = new File("./build/demo-app");
        dir.mkdirs();
        NextGen.setWorkingDir("./build/demo-app");
        NextGen.runCartridgeWithName(uut.getName());
        NextGen.main(new String[]{"/demoapp.oom"});
    }

    @Test
    public void testSymfonyApp() {
        NextGen.setWorkingDir("/Users/casi/PhpstormProjects/symfony");
        NextGen.runCartridgeWithName(uut.getName());
        NextGen.main(new String[]{"http://localhost:7001/de.spraener.tinyapp.symfony"});
    }
}

