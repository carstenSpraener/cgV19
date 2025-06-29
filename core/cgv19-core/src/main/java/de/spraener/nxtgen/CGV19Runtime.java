package de.spraener.nxtgen;

import java.io.File;

public interface CGV19Runtime {
    void writeCodeBlock(String workingDir, CodeBlock codeBlock);
    String getWorkingDir();
}
