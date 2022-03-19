package de.spraener.nxtgen.cartridge.rest.cntrl;

import de.spraener.nxtgen.CodeBlock;
import de.spraener.nxtgen.CodeGenerator;
import de.spraener.nxtgen.GroovyCodeBlockImpl;
import de.spraener.nxtgen.cartridge.rest.RESTJavaHelper;
import de.spraener.nxtgen.java.JavaCodeBlock;
import de.spraener.nxtgen.model.ModelElement;
import de.spraener.nxtgen.oom.model.MClass;

public class LogicGenerator implements CodeGenerator {
    @Override
    public CodeBlock resolve(ModelElement element, String templateName) {
        MClass mc = (MClass) element;
        JavaCodeBlock jCB = new JavaCodeBlock("src/main/java-gen", mc.getPackage().getFQName(), mc.getName());
        GroovyCodeBlockImpl gcb = new GroovyCodeBlockImpl("logic", element, "/cntrl/LogicTemplate.groovy");
        jCB.addCodeBlock(gcb);
        return jCB;
    }
}
