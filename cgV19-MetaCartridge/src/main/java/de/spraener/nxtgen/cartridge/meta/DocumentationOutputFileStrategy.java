package de.spraener.nxtgen.cartridge.meta;

import de.spraener.nxtgen.filestrategies.ToFileStrategy;
import de.spraener.nxtgen.model.ModelElement;

import java.io.File;

public class DocumentationOutputFileStrategy implements ToFileStrategy {
    private ModelElement element;
    private String path;
    private String ending;

    public DocumentationOutputFileStrategy(ModelElement element, String path, String ending) {
        this.element = element;
        this.path = path;
        this.ending = ending;
    }

    @Override
    public File open() {
        return new File( path+"/"+element.getName()+"."+ending);
    }
}
