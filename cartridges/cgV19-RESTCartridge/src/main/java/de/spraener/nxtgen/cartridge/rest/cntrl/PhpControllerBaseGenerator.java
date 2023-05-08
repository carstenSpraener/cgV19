package de.spraener.nxtgen.cartridge.rest.cntrl;

import de.spraener.nxtgen.CodeBlock;
import de.spraener.nxtgen.CodeGenerator;
import de.spraener.nxtgen.GroovyCodeBlockImpl;
import de.spraener.nxtgen.cartridge.rest.php.PhpCodeBlock;
import de.spraener.nxtgen.model.ModelElement;
import de.spraener.nxtgen.oom.model.MClass;

import java.util.function.Consumer;

public class PhpControllerBaseGenerator implements CodeGenerator {
    private Consumer<CodeBlock>[] codeBlockModifiers;

    public PhpControllerBaseGenerator(Consumer<CodeBlock>... codeBlockModifiers) {
        this.codeBlockModifiers = codeBlockModifiers;
    }
    @Override
    public CodeBlock resolve(ModelElement element, String templateName) {
        MClass mc = (MClass) element;
        PhpCodeBlock phpCB = new PhpCodeBlock("src", "Controller/Base", mc.getName());
        GroovyCodeBlockImpl gcb = new GroovyCodeBlockImpl("cntrl", element, "/cntrl/PhpCntrlBaseTemplate.groovy");
        phpCB.addCodeBlock(gcb);

        if( codeBlockModifiers!=null ) {
            for (Consumer<CodeBlock> codeBlockModifier: this.codeBlockModifiers) {
                codeBlockModifier.accept(phpCB);
            }
        }

        return phpCB;
    }
}
