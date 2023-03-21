package de.spraener.nxtgen.filestrategies;

import de.spraener.nxtgen.model.ModelElement;
import de.spraener.nxtgen.model.ModelHelper;

import java.io.File;

public class XmlFileStrategy implements ToFileStrategy{
    ModelElement modelElement;
    private final String outputDir;

    public XmlFileStrategy(String outputDir, ModelElement me) {
        this.outputDir = outputDir;
        modelElement = me;
    }
    @Override
    public File open() {
        return new File(outputDir+"/"+ModelHelper.getFQName(this.modelElement, "/")+".xml");
    }

    @Override
    public File open(String workingDir) {
        return new File(workingDir+"/"+outputDir+"/"+ModelHelper.getFQName(this.modelElement, "/")+".xml");
    }
}
