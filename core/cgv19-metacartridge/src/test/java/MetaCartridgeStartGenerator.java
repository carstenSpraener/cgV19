import de.spraener.nxtgen.NextGen;
import org.junit.jupiter.api.Test;

import java.io.File;

public class MetaCartridgeStartGenerator {

    @Test
    public void testRESTCartridgeDSLModel() {
        NextGen.main(new String[]{"../cgv19-RESTCartridge/DSL.oom"});
    }

    @Test
    public void testMetaCartridgeSelfOnline() {
        new File("./build/test-gen").mkdirs();
        NextGen.setWorkingDir("./build/test-gen");
        NextGen.main(new String[]{"http://localhost:7001/META-DSL"});
    }

    @Test
    public void testMetaCartridgeSelfOffline() {
        new File("./build/test-gen").mkdirs();
        NextGen.setWorkingDir("./build/test-gen");
        NextGen.main(new String[]{"META-DSL.oom"});
    }

}
