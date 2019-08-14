package de.csp.nxtgen.filestrategies;

import java.io.File;

public class TypeScriptFileStrategy implements ToFileStrategy {
    private String outDir;
    private String typeName;

    public TypeScriptFileStrategy(String outDir, String typeName) {
        this.outDir = outDir;
        this.typeName = typeName;
    }
    @Override
    public File open() {
        return new File(outDir+"/"+typeName+".ts");
    }
}
