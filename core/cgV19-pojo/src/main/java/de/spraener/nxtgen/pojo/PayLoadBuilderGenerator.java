package de.spraener.nxtgen.pojo;

import de.spraener.nxtgen.CodeBlock;
import de.spraener.nxtgen.CodeGenerator;
import de.spraener.nxtgen.GroovyCodeBlockImpl;
import de.spraener.nxtgen.java.JavaCodeBlock;
import de.spraener.nxtgen.model.ModelElement;

public class PayLoadBuilderGenerator implements CodeGenerator {
    @Override
    public CodeBlock resolve(ModelElement me, String templateName) {
        ModelElement pkg = me.getParent();
        String pkgName = pkg.getName();
        JavaCodeBlock jcb = new JavaCodeBlock("src/main/java-gen", pkgName, me.getName()+"Builder");
        GroovyCodeBlockImpl gcb = new GroovyCodeBlockImpl("PayLoadBuilder", me, "/PayLoadBuilderTemplate.groovy");
        jcb.addCodeBlock(gcb);
        return jcb;
    }
}
