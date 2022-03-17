package de.spraener.nxtgen.cartridge.rest.angular;

import de.spraener.nxtgen.CodeBlock;
import de.spraener.nxtgen.CodeGenerator;
import de.spraener.nxtgen.GroovyCodeBlockImpl;
import de.spraener.nxtgen.filestrategies.TypeScriptFileStrategy;
import de.spraener.nxtgen.model.ModelElement;

public class TSTypeGenerator implements CodeGenerator {
    @Override
    public CodeBlock resolve(ModelElement element, String templateName) {
        GroovyCodeBlockImpl gcb = new GroovyCodeBlockImpl("typscript", element, "/ts/TSType.groovy");
        gcb.setToFileStrategy(new TypeScriptFileStrategy("src/main/frontend/src/app/model", element.getName()));
        return gcb;
    }
}
