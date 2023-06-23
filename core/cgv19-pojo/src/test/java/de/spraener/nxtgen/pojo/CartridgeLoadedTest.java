package de.spraener.nxtgen.pojo;

import de.spraener.nxtgen.NextGen;
import org.junit.jupiter.api.Test;

import java.io.File;

import static org.junit.jupiter.api.Assertions.assertTrue;

public class CartridgeLoadedTest {

    @Test
    public void testCartidgeLoaded() throws Exception {
        NextGen.setWorkingDir("./build/tmp");
        NextGen.main(new String[]{"/pojotest.oom"});

        File fPojo = new File("./build/tmp/src/main/java/a/PoJoA.java");
        File fPojoBase = new File("./build/tmp/src/main/java-gen/a/PoJoABase.java");
        assertTrue(fPojo.exists());
        assertTrue(fPojoBase.exists());
    }
}
