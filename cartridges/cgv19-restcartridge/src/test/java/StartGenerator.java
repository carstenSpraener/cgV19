import de.spraener.nxtgen.NextGen;
import org.junit.jupiter.api.Test;

import java.io.File;

public class StartGenerator {
    private static String WORKING_DIR ="../cgv19-cloud/build/source-gen/api";

    void testRestModel() {
        new File(WORKING_DIR).mkdirs();
        System.out.println(new File(".").getAbsolutePath());
        NextGen.setWorkingDir(WORKING_DIR);
        NextGen.main(new String[]{"http://localhost:7001/de.spraener.tinyapp.api"});
    }
}
