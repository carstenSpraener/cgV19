package de.spraener.nxtgen.cartridge.rest.cntrl;

import de.spraener.nxtgen.NextGen;
import org.junit.jupiter.api.Test;

import java.io.File;

import static org.junit.jupiter.api.Assertions.assertTrue;


public class ApiRessourceTest {

    @Test
    void testApiRessourceGeneration() throws Exception {
        NextGen.main(new String[]{"/apiRessource.oom"});
        File api = new File("build/tmp/src/main/java/pkg/AnAPi.java");
        File apiBase = new File("build/tmp/src/main/java-gen/pkg/AnAPiBase.java");
        assertTrue(api.exists());
        assertTrue(apiBase.exists());
    }
}
