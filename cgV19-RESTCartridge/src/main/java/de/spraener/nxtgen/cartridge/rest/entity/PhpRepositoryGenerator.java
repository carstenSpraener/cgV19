package de.spraener.nxtgen.cartridge.rest.entity;

import de.spraener.nxtgen.CodeBlock;
import de.spraener.nxtgen.CodeGenerator;
import de.spraener.nxtgen.GroovyCodeBlockImpl;
import de.spraener.nxtgen.cartridge.rest.php.PhpCodeBlock;
import de.spraener.nxtgen.model.ModelElement;
import de.spraener.nxtgen.oom.model.MClass;

public class PhpRepositoryGenerator implements CodeGenerator {
    @Override
    public CodeBlock resolve(ModelElement element, String templateName) {
        MClass mc = (MClass) element;
        PhpCodeBlock phpCB = new PhpCodeBlock("src", "Repository", mc.getName());
        GroovyCodeBlockImpl gcb = new GroovyCodeBlockImpl("respository", element, "/entity/PhpRepositoryTemplate.groovy");
        phpCB.addCodeBlock(gcb);
        return phpCB;
    }
}
