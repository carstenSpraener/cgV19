package de.spraener.nxtgen;

import org.apache.commons.io.IOUtils;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

public class SimpleFileWriterCodeBlock extends SimpleStringCodeBlock {
    private String filePath;

    public SimpleFileWriterCodeBlock(String fileContent, String filePath) {
        super(fileContent);
        this.filePath = filePath;
    }

    protected boolean checkProtection(File file) {
        return NextGen.getProtectionStrategie().isProtected(file);
    }

    @Override
    public void writeOutput(String workingDir) {
        File f = new File(filePath);
        if( checkProtection(f) ) {
            return;
        }
        try( FileWriter fw = new FileWriter(f); ) {
            IOUtils.write(this.toCode(), fw);
        } catch( IOException xc ) {
            throw new RuntimeException(xc);
        }
    }

}
