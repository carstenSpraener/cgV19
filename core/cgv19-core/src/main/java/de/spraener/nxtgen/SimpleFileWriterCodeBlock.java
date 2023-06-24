package de.spraener.nxtgen;

import org.apache.commons.io.IOUtils;

import java.io.FileWriter;
import java.io.IOException;

public class SimpleFileWriterCodeBlock extends SimpleStringCodeBlock {
    private String filePath;

    public SimpleFileWriterCodeBlock(String fileContent, String filePath) {
        super(fileContent);
        this.filePath = filePath;
    }

    @Override
    public void writeOutput(String workingDir) {
        try( FileWriter fw = new FileWriter(workingDir+"/"+this.filePath); ) {
            IOUtils.write(this.toCode(), fw);
        } catch( IOException xc ) {
            throw new RuntimeException(xc);
        }
    }

}
