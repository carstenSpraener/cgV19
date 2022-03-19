package de.spraener.nxtgen.cartridge.meta;

import de.spraener.nxtgen.CodeBlock;
import de.spraener.nxtgen.CodeBlockImpl;
import de.spraener.nxtgen.CodeGenerator;
import de.spraener.nxtgen.GroovyCodeBlockImpl;
import de.spraener.nxtgen.model.ModelElement;

public class StereotypeDocGenerator implements CodeGenerator {
    @Override
    public CodeBlock resolve(ModelElement element, String templateName) {
        GroovyCodeBlockImpl gcb = new GroovyCodeBlockImpl("Stereotype Documentation", element, "/StereotypeDocumentation.groovy");
        gcb.setToFileStrategy(new DocumentationOutputFileStrategy(element, "doc/stereotypes", "md"));
        return gcb;
    }
}
