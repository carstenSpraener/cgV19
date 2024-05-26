// THIS FILE IS GENERATED AS LONG AS THIS LINE EXISTS
package de.spraener.nxtgen.cartridge.meta;

import java.util.function.Consumer;

import de.spraener.nxtgen.CodeBlock;
import de.spraener.nxtgen.CodeGenerator;
import de.spraener.nxtgen.GroovyCodeBlockImpl;
import de.spraener.nxtgen.java.JavaCodeBlock;
import de.spraener.nxtgen.model.ModelElement;
import de.spraener.nxtgen.oom.model.*;
import de.spraener.nxtgen.annotations.*;

/*
@CGV19Generator(
        requiredStereotype = "GroovyScript",
        operatesOn = MClass.class,
        outputType = OutputType.OTHER,
        outputTo = OutputTo.OTHER,
        templateName = "/GroovyScriptTemplate.groovy",
        implementationKind = ImplementationKind.GROOVY_TEMPLATE
)
*/
public class GroovyScriptGenerator implements CodeGenerator {
    private Consumer<CodeBlock>[] codeBlockModifiers;

    public GroovyScriptGenerator(Consumer<CodeBlock>... codeBlockModifiers) {
        this.codeBlockModifiers = codeBlockModifiers;
    }

    @Override
    public CodeBlock resolve(ModelElement element, String templateName) {
        GroovyCodeBlockImpl gcb = new GroovyCodeBlockImpl("", element, "/GroovyScriptTemplate.groovy");
        gcb.setToFileStrategy(new de.spraener.nxtgen.filestrategies.GeneralFileStrategy(".", "", ""));

        if( codeBlockModifiers!=null ) {
            for (Consumer<CodeBlock> codeBlockModifier: this.codeBlockModifiers) {
                codeBlockModifier.accept(gcb);
            }
        }

        return gcb;

    }
}

