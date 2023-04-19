// THIS FILE IS GENERATED AS LONG AS THIS LINE EXISTS
package de.spraener.nextgen.vpplugin.cartridge;

import de.spraener.nxtgen.CodeBlock;
import de.spraener.nxtgen.CodeGenerator;
import de.spraener.nxtgen.GroovyCodeBlockImpl;
import de.spraener.nxtgen.java.JavaCodeBlock;
import de.spraener.nxtgen.model.ModelElement;
import de.spraener.nxtgen.oom.model.*;

public class ExporterGenerator implements CodeGenerator {
    @Override
    public CodeBlock resolve(ModelElement element, String templateName) {
        MClass me = (MClass)element;
        JavaCodeBlock jcb = new JavaCodeBlock("src/main/java", me.getPackage().getFQName(), me.getName());
        GroovyCodeBlockImpl gcb = new GroovyCodeBlockImpl("ExporterGenerator", me, "/de.spraener.netgen.vpplugin.cartridge/ExporterTemplate.groovy");
        jcb.addCodeBlock(gcb);
        return jcb;
    }
}

