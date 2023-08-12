package de.spraener.nxtgen.incubator;

import java.util.Map;

public class Blueprint {

    public static void copyTo(String outDir, String resourcePath, Map<String,String> scope) {
        BlueprintCompiler bpc = new BlueprintCompiler(resourcePath);
        bpc.getScope().putAll(scope);
        bpc.evaluateTo(outDir);
    }
}
