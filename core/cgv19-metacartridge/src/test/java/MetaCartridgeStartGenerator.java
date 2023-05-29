import de.spraener.nxtgen.NextGen;
import org.junit.Test;

public class MetaCartridgeStartGenerator {

    @Test
    public void testRESTCartridgeDSLModel() {
        NextGen.main(new String[]{"../cgv19-RESTCartridge/DSL.oom"});
    }

    @Test
    public void testMetaCartridgeSelfGenerator() {
        NextGen.main(new String[]{"http://localhost:7001/" +
                "de.spraener.nxtgen.cartridge.meta"});
    }

    @Test
    public void testMetaCartridgeSelfGeneratorOnline() {
        NextGen.main(new String[]{"http://localhost:7000/META-DSL"});
    }
}
