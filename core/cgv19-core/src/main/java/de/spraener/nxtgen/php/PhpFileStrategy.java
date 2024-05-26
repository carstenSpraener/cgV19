package de.spraener.nxtgen.php;

import de.spraener.nxtgen.filestrategies.ToFileStrategy;

import java.io.File;

public class PhpFileStrategy implements ToFileStrategy {
    private String phpDir = null;
    private String fileName;

    public PhpFileStrategy(String phpDir, String fileName) {
        this.fileName = fileName;
        this.phpDir = phpDir;
    }

    @Override
    public File open() {
        return new File(getPhpOutputDir() + "/" + fileName);
    }


    private String getPhpOutputDir() {
        return PhpCodeBlock.getOutputPath() + "/src/"+phpDir;
    }

}
