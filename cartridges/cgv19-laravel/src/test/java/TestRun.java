import de.spraener.nxtgen.NextGen;
import de.spraener.nxtgen.laravel.Laravel;
import org.junit.jupiter.api.Test;

import java.io.File;

public class TestRun {

    public static void main(String[] args) throws Exception {
        Laravel uut = new Laravel();
        File dir = new File("./build/demo-app");
        dir.mkdirs();
        NextGen.setWorkingDir("./build/demo-app");
        NextGen.runCartridgeWithName(uut.getName());
        NextGen.main(new String[]{"http://localhost:7001/de.spraener.tinyapp.laravel"});
    }
}

