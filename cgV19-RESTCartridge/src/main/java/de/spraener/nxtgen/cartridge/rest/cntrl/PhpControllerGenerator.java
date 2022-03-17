package de.spraener.nxtgen.cartridge.rest.cntrl;

import de.spraener.nxtgen.CodeBlock;
import de.spraener.nxtgen.CodeGenerator;
import de.spraener.nxtgen.GroovyCodeBlockImpl;
import de.spraener.nxtgen.cartridge.rest.php.PhpCodeBlock;
import de.spraener.nxtgen.model.ModelElement;
import de.spraener.nxtgen.oom.model.MClass;

public class PhpControllerGenerator implements CodeGenerator {
    @Override
    public CodeBlock resolve(ModelElement element, String templateName) {
        MClass mc = (MClass) element;
        PhpCodeBlock phpCB = new PhpCodeBlock("src", "Controller", mc.getName().replaceAll("Base", ""));
        GroovyCodeBlockImpl gcb = new GroovyCodeBlockImpl("cntrl", element, "/cntrl/PhpCntrlTemplate.groovy");
        phpCB.addCodeBlock(gcb);
        return phpCB;
    }}