import de.spraener.nxtgen.NextGen;
import de.spraener.nxtgen.oom.cartridge.OOMMetaCartridge;
import org.junit.jupiter.api.Test;

import java.io.File;

public class TestRun {
    private OOMMetaCartridge uut = new OOMMetaCartridge();
    @Test
    public void testWholeGeneration() throws Exception {
        File dir = new File("./build/demo-app");
        dir.mkdirs();
        NextGen.setWorkingDir("./build/demo-app");
        NextGen.runCartridgeWithName(uut.getName());
        NextGen.main(new String[]{"http://localhost:7001/de.spraener.nxtgen.oom"});
    }
}

