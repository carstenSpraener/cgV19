import de.spraener.nxtgen.NextGen;
import org.junit.jupiter.api.Test;

public class CartridgeTestRun {

    @Test
    public void testCartridge() throws Exception {
        NextGen.setWorkingDir("../cgv19-vpplugin");
        NextGen.main(new String[]{"http://localhost:7001/de.spraener.nextgen.vpplugin.oom"});
    }
}
