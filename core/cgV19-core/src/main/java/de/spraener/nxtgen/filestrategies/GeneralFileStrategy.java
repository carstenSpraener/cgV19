package de.spraener.nxtgen.filestrategies;

import java.io.File;

public class GeneralFileStrategy implements ToFileStrategy {
    private final String outDir;
    private final String typeName;
    private final String fileEnding;

    public GeneralFileStrategy(String outDir, String typeName, String fileEnding) {
        this.outDir = outDir;
        this.typeName = typeName;
        this.fileEnding = fileEnding;
    }
    @Override
    public File open() {
        return new File(outDir+"/"+typeName+"."+fileEnding);
    }

    @Override
    public File open(String workingDir) {
        return new File(workingDir+"/"+outDir+"/"+typeName+"."+fileEnding);
    }
}
