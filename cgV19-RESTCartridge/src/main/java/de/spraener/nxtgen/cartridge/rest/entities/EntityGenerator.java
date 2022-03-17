package de.spraener.nxtgen.cartridge.rest.entities;

import de.spraener.nxtgen.cartridge.rest.RESTJavaHelper;
import de.spraener.nxtgen.oom.model.MClass;
import de.spraener.nxtgen.CodeBlock;
import de.spraener.nxtgen.CodeGenerator;
import de.spraener.nxtgen.GroovyCodeBlockImpl;
import de.spraener.nxtgen.java.JavaCodeBlock;
import de.spraener.nxtgen.model.ModelElement;

public class EntityGenerator implements CodeGenerator {
    @Override
    public CodeBlock resolve(ModelElement element, String templateName) {
        MClass mc = (MClass) element;
        JavaCodeBlock jCB = new JavaCodeBlock("src/main/java-gen", RESTJavaHelper.toPkgName(mc.getPackage()), mc.getName());
        GroovyCodeBlockImpl gcb = new GroovyCodeBlockImpl("entity", element, "/entity/EntityTemplate.groovy");
        jCB.addCodeBlock(gcb);
        return jCB;
    }
}