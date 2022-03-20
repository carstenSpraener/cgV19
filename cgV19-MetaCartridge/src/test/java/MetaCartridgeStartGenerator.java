import de.spraener.nxtgen.NextGen;
import org.junit.Test;

public class MetaCartridgeStartGenerator {

    @Test
    public void testRESTCartridgeDSLModel() {
        NextGen.main(new String[]{"../cgV19-RESTCartridge/DSL.oom"});
    }
}
