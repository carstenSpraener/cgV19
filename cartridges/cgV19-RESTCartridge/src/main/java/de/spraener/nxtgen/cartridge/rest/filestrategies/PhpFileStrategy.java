package de.spraener.nxtgen.cartridge.rest.filestrategies;

import de.spraener.nxtgen.CodeBlock;
import de.spraener.nxtgen.cartridge.rest.php.PhpCodeBlock;
import de.spraener.nxtgen.filestrategies.ToFileStrategy;
import de.spraener.nxtgen.model.ModelElement;
import de.spraener.nxtgen.oom.model.MClass;

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
