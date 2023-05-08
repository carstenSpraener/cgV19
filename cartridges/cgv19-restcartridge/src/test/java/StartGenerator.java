import de.spraener.nxtgen.NextGen;
import org.junit.Test;

import java.io.File;

public class StartGenerator {

    @Test
    public void testRestModel() {
        System.out.println(new File(".").getAbsolutePath());
        NextGen.main(new String[]{"http://localhost:7001/de.csp.demo.rest"});
    }
}
