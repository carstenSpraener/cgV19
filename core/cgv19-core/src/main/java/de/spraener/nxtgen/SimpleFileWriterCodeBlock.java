package de.spraener.nxtgen;

import org.apache.commons.io.IOUtils;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.charset.StandardCharsets;

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
        File f = new File(workingDir+"/"+filePath);
        if( checkProtection(f) ) {
            return;
        }
        try( FileWriter fw = new FileWriter(workingDir+"/"+this.filePath, StandardCharsets.UTF_8); ) {
            IOUtils.write(this.toCode().getBytes(StandardCharsets.UTF_8), fw, StandardCharsets.UTF_8);
        } catch( IOException xc ) {
            throw new RuntimeException(xc);
        }
    }

}
