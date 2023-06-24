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
        requiredStereotype = "Transformation",
        operatesOn = MClass.class,
        outputType = OutputType.JAVA,
        outputTo = OutputTo.SRC_GEN,
        templateName = "/TransformationBase.groovy",
        implementationKind = ImplementationKind.GROOVY_TEMPLATE
)
public class TransformationBaseGenerator implements CodeGenerator {
    private Consumer<CodeBlock>[] codeBlockModifiers;

    public TransformationBaseGenerator(Consumer<CodeBlock>... codeBlockModifiers) {
        this.codeBlockModifiers = codeBlockModifiers;
    }

    @Override
    public CodeBlock resolve(ModelElement element, String templateName) {
        MClass mClass = (MClass)element;
        // TODO: Base postfix should not be set here!
        JavaCodeBlock jcb = new JavaCodeBlock("src/main/java-gen", mClass.getPackage().getFQName(), mClass.getName()+"Base");
        GroovyCodeBlockImpl gcb = new GroovyCodeBlockImpl("TransformationBase", mClass, "/TransformationBase.groovy");
        jcb.addCodeBlock(gcb);

        if( codeBlockModifiers!=null ) {
            for (Consumer<CodeBlock> codeBlockModifier: this.codeBlockModifiers) {
                codeBlockModifier.accept(jcb);
            }
        }

        return jcb;
    }
}

