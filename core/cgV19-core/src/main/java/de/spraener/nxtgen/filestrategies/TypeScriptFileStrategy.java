package de.spraener.nxtgen.filestrategies;

public class TypeScriptFileStrategy extends GeneralFileStrategy {
    private String outDir;
    private String typeName;

    public TypeScriptFileStrategy(String outDir, String typeName) {
        super(outDir, typeName, "ts");
    }
}
