package de.spraener.nxtgen.cartridge.meta;

import de.spraener.nxtgen.CodeBlock;
import de.spraener.nxtgen.CodeGenerator;
import de.spraener.nxtgen.GroovyCodeBlockImpl;
import de.spraener.nxtgen.java.JavaCodeBlock;
import de.spraener.nxtgen.model.ModelElement;
import de.spraener.nxtgen.oom.model.MClass;

public class StereotypeEnumGenerator implements CodeGenerator {
    @Override
    public CodeBlock resolve(ModelElement element, String templateName) {
        GroovyCodeBlockImpl gcb =  new GroovyCodeBlockImpl("StereotypeEnum", element, "/StereotypeEnumGenerator.groovy");
        JavaCodeBlock jcb = new JavaCodeBlock("src/main/java-gen", ((MClass)element).getPackage().getFQName(), element.getName());
        jcb.addCodeBlock(gcb);
        return jcb;
    }
}
