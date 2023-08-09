package de.spraener.nxtgen.cloud;

import de.spraener.nxtgen.NextGen;
import de.spraener.nxtgen.filestrategies.ToFileStrategy;
import de.spraener.nxtgen.model.ModelElement;

import java.io.File;

public class K8SConfigFileSpec implements ToFileStrategy {
    private String fileName;
    public K8SConfigFileSpec(ModelElement me, String postFix) {
        fileName = me.getName()+postFix+".yaml";
    }

    @Override
    public File open() {
        return new File(NextGen.getWorkingDir()+"/k8s/"+fileName);
    }
}
