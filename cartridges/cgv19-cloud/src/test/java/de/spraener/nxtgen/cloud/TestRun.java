package de.spraener.nxtgen.cloud;

import de.spraener.nxtgen.NextGen;
import org.junit.jupiter.api.Test;

import java.io.File;

public class TestRun {
    private CloudCartridge uut = new CloudCartridge();
    @Test
    public void testWholeGeneration() throws Exception {
        File dir = new File("./build/source-gen");
        dir.mkdirs();
        NextGen.setWorkingDir("./build/source-gen");
        NextGen.runCartridgeWithName(uut.getName());
        NextGen.main(new String[]{"/de.spraener.tinyapp"});
    }
}
