package de.spraener.nxtgen.ice;

import de.spraener.nxtgen.NextGen;
import de.spraener.nxtgen.ice2.ICECalledCartridge;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.io.FileWriter;
import java.io.PrintWriter;

public class TestRun {
    @Test
    public void testWholeGeneration() throws Exception {
        File dir = new File("./build/demo-app");
        dir.mkdirs();
        PrintWriter pw = new PrintWriter(new FileWriter("./build/demo-app/ice-demo.oom"));
        pw.println(
"""
import de.spraener.nxtgen.groovy.ModelDSL

ModelDSL.make {
    mPackage {
        name 'ice.demo'
        mClass {
          name 'MyDockerSystem'
          stereotype 'DockerApplication'
        }
        mPackage {
          name 'api'
          stereotype 'DockerService', {
            taggedValue 'cgv19Cartridge', 'ICECalledCartridge'
          }
        }
    }
}
"""
        );
        pw.flush();
        pw.close();
        NextGen.setWorkingDir("./build/demo-app");
        NextGen.addCartridge(new ICECalledCartridge());
        NextGen.runCartridgeWithName(ICECallingCartridge.NAME);
        NextGen.main(new String[]{"./build/demo-app/ice-demo.oom"});
    }
}

