import de.spraener.nxtgen.NextGen;
import org.junit.jupiter.api.Test;

import java.io.File;

public class StartGenerator {

    @Test
    void testRestModel() {
        System.out.println(new File(".").getAbsolutePath());
        NextGen.setWorkingDir("../../restdemoDocker");
        NextGen.main(new String[]{"http://localhost:7001/de.csp.demo.rest"});
    }
}
