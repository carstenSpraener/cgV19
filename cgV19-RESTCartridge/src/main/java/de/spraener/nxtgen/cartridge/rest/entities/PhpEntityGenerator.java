package de.spraener.nxtgen.cartridge.rest.entities;

import de.spraener.nxtgen.CodeBlock;
import de.spraener.nxtgen.CodeGenerator;
import de.spraener.nxtgen.GroovyCodeBlockImpl;
import de.spraener.nxtgen.cartridge.rest.RESTJavaHelper;
import de.spraener.nxtgen.cartridge.rest.php.PhpCodeBlock;
import de.spraener.nxtgen.java.JavaCodeBlock;
import de.spraener.nxtgen.model.ModelElement;
import de.spraener.nxtgen.oom.model.MClass;

public class PhpEntityGenerator implements CodeGenerator {
    @Override
    public CodeBlock resolve(ModelElement element, String templateName) {
        MClass mc = (MClass) element;
        PhpCodeBlock phpCB = new PhpCodeBlock("src", "Entity", mc.getName());
        GroovyCodeBlockImpl gcb = new GroovyCodeBlockImpl("entity", element, "/entity/PhpEntityTemplate.groovy");
        phpCB.addCodeBlock(gcb);
        return phpCB;
    }
}
