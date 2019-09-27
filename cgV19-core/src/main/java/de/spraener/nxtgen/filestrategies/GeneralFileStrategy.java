package de.spraener.nxtgen.filestrategies;

import java.io.File;

public class GeneralFileStrategy implements ToFileStrategy {
    private String outDir;
    private String typeName;
    private String fileEnding;

    public GeneralFileStrategy(String outDir, String typeName, String fileEnding) {
        this.outDir = outDir;
        this.typeName = typeName;
        this.fileEnding = fileEnding;
    }
    @Override
    public File open() {
        return new File(outDir+"/"+typeName+"."+fileEnding);
    }
}
