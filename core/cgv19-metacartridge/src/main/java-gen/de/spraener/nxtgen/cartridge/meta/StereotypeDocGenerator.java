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
        requiredStereotype = "Stereotype",
        operatesOn = MClass.class,
        outputType = OutputType.MARKDOWN,
        outputTo = OutputTo.OTHER,
        templateName = "/StereotypeDocumentation.groovy",
        implementationKind = ImplementationKind.GROOVY_TEMPLATE
)
*/
public class StereotypeDocGenerator implements CodeGenerator {
    private Consumer<CodeBlock>[] codeBlockModifiers;

    public StereotypeDocGenerator(Consumer<CodeBlock>... codeBlockModifiers) {
        this.codeBlockModifiers = codeBlockModifiers;
    }

    @Override
    public CodeBlock resolve(ModelElement element, String templateName) {
        GroovyCodeBlockImpl gcb = new GroovyCodeBlockImpl("", element, "/StereotypeDocumentation.groovy");
        gcb.setToFileStrategy(new de.spraener.nxtgen.filestrategies.GeneralFileStrategy(".", "", ""));

        if( codeBlockModifiers!=null ) {
            for (Consumer<CodeBlock> codeBlockModifier: this.codeBlockModifiers) {
                codeBlockModifier.accept(gcb);
            }
        }

        return gcb;

    }
}

