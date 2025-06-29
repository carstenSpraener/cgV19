package de.spraener.nxtgen;

import java.io.File;

public class CGV19RuntimeDefaultImpl implements CGV19Runtime {
    private final String workingDir;

    public CGV19RuntimeDefaultImpl() {
        this.workingDir = new File(".").getAbsolutePath();
    }
    
    public CGV19RuntimeDefaultImpl(String workingDir) {
        this.workingDir = workingDir;
    }

    @Override
    public String getWorkingDir() {
        return this.workingDir;
    }

    public void writeCodeBlock(String workingDir, CodeBlock codeBlock) {
        codeBlock.writeOutput(workingDir);
    }
}
