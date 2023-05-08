package de.spraener.nxtgen.filestrategies;

import de.spraener.nxtgen.model.ModelElement;
import de.spraener.nxtgen.model.ModelHelper;

import java.io.File;

public class XmlFileStrategy implements ToFileStrategy{
    ModelElement modelElement;
    private String outputDir;

    public XmlFileStrategy(String outputDir, ModelElement me) {
        this.outputDir = outputDir;
        modelElement = me;
    }
    @Override
    public File open() {
        return new File(outputDir+"/"+ModelHelper.getFQName(this.modelElement, "/")+".xml");
    }
}
