package de.spraener.nxtgen.incubator;

import de.spraener.nxtgen.CodeBlockImpl;

public class BlueprintCodeBlock extends CodeBlockImpl {
    private BlueprintCompiler blueprintCompiler;
    private String outputDir;

    /**
     * Create a new CodeBlock with the given name.
     *
     * @param blueprintCompiler a BlueprintCompiler to run on evaluation.
     */
    public BlueprintCodeBlock(BlueprintCompiler blueprintCompiler, String outputDir) {
        super(blueprintCompiler.getName());
        this.blueprintCompiler = blueprintCompiler;
        this.outputDir = outputDir;
    }

    @Override
    public void writeOutput(String workingDir) {
        String finalDir = workingDir;
        if( this.outputDir.startsWith("/") ) {
            finalDir = outputDir;
        } else {
            finalDir = outputDir + "/" + outputDir;
        }
        this.blueprintCompiler.evaluateTo(finalDir);
    }
}
