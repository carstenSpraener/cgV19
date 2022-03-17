package de.spraener.nxtgen.cartridge.rest;

import de.spraener.nxtgen.CodeBlock;
import de.spraener.nxtgen.CodeGenerator;
import de.spraener.nxtgen.GroovyCodeBlockImpl;
import de.spraener.nxtgen.java.JavaCodeBlock;
import de.spraener.nxtgen.model.ModelElement;
import de.spraener.nxtgen.oom.model.MClass;

public class PoJoGenerator implements CodeGenerator {
    @Override
    public CodeBlock resolve(ModelElement element, String templateName) {
        MClass mc = (MClass) element;
        JavaCodeBlock jCB = new JavaCodeBlock("src/main/java", RESTJavaHelper.toPkgName(mc.getPackage()), mc.getName());
        GroovyCodeBlockImpl gcb = new GroovyCodeBlockImpl("PoJo", element, "/PoJoTemplate.groovy");
        jCB.addCodeBlock(gcb);
        return jCB;
    }
}
