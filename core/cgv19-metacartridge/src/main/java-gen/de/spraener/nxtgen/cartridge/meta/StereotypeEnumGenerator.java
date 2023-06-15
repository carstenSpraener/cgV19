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

@CGV19Generator(
        requiredStereotype = "StereotypeEnum",
        operatesOn = MClass.class,
        outputType = OutputType.JAVA,
        outputTo = OutputTo.SRC_GEN,
        tempalteName = "/StereotypeEnumGenerator.groovy",
        implementationKind = ImplementationKind.GROOVY_TEMPLATE
)
public class StereotypeEnumGenerator implements CodeGenerator {
    private Consumer<CodeBlock>[] codeBlockModifiers;

    public StereotypeEnumGenerator(Consumer<CodeBlock>... codeBlockModifiers) {
        this.codeBlockModifiers = codeBlockModifiers;
    }

    @Override
    public CodeBlock resolve(ModelElement element, String templateName) {
        MClass me = (MClass)element;
        JavaCodeBlock jcb = new JavaCodeBlock("src/main/java-gen", me.getPackage().getFQName(), me.getName());
        GroovyCodeBlockImpl gcb = new GroovyCodeBlockImpl("StereotypeEnumGenerator", me, "/StereotypeEnumGenerator.groovy");
        jcb.addCodeBlock(gcb);

        if( codeBlockModifiers!=null ) {
            for (Consumer<CodeBlock> codeBlockModifier: this.codeBlockModifiers) {
                codeBlockModifier.accept(jcb);
            }
        }

        return jcb;
    }
}

