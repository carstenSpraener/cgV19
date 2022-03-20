package de.spraener.nxtgen.cartridge.meta;

import de.spraener.nxtgen.CodeBlock;
import de.spraener.nxtgen.CodeGenerator;
import de.spraener.nxtgen.GroovyCodeBlockImpl;
import de.spraener.nxtgen.java.JavaCodeBlock;
import de.spraener.nxtgen.model.ModelElement;
import de.spraener.nxtgen.oom.model.MClass;

public class CodeGeneratorGenerator implements CodeGenerator {
    @Override
    public CodeBlock resolve(ModelElement element, String templateName) {
        MClass mClass = (MClass)element;
        JavaCodeBlock jcb = new JavaCodeBlock("src/main/java-gen", mClass.getPackage().getFQName(), mClass.getName());
        GroovyCodeBlockImpl gcb = new GroovyCodeBlockImpl("CodeGenerator", mClass, "/CodeGenerator.groovy");
        jcb.addCodeBlock(gcb);
        return jcb;
    }
}
