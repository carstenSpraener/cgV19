package de.spraener.nxtgen.cartridge.meta;

import de.spraener.nxtgen.filestrategies.ToFileStrategy;
import de.spraener.nxtgen.model.ModelElement;

import java.io.File;

public class DocumentationOutputFileStrategy implements ToFileStrategy {
    private final ModelElement element;
    private final String path;
    private final String ending;

    public DocumentationOutputFileStrategy(ModelElement element, String path, String ending) {
        this.element = element;
        this.path = path;
        this.ending = ending;
    }

    @Override
    public File open() {
        return new File( path+"/"+element.getName()+"."+ending);
    }

    @Override
    public File open(String workingDir) {
        return new File( workingDir+"/"+path+"/"+element.getName()+"."+ending);
    }
}
