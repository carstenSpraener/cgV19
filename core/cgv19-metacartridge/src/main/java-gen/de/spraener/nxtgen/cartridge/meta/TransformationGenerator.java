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
        requiredStereotype = "Transformation",
        operatesOn = MClass.class,
        outputType = OutputType.JAVA,
        outputTo = OutputTo.SRC,
        templateName = "/Transformation.groovy",
        implementationKind = ImplementationKind.GROOVY_TEMPLATE
)
*/
public class TransformationGenerator implements CodeGenerator {
    private Consumer<CodeBlock>[] codeBlockModifiers;

    public TransformationGenerator(Consumer<CodeBlock>... codeBlockModifiers) {
        this.codeBlockModifiers = codeBlockModifiers;
    }

    @Override
    public CodeBlock resolve(ModelElement element, String templateName) {
        MClass me = (MClass)element;
        JavaCodeBlock jcb = new JavaCodeBlock("src/main/java", me.getPackage().getFQName(), me.getName());
        GroovyCodeBlockImpl gcb = new GroovyCodeBlockImpl("TransformationGenerator", me, "/Transformation.groovy");
        jcb.addCodeBlock(gcb);

        if( codeBlockModifiers!=null ) {
            for (Consumer<CodeBlock> codeBlockModifier: this.codeBlockModifiers) {
                codeBlockModifier.accept(jcb);
            }
        }

        return jcb;
    }
}

