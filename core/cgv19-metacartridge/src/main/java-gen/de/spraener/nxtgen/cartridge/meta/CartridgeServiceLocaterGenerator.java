package de.spraener.nxtgen.cartridge.meta;

import java.util.function.Consumer;

import de.spraener.nxtgen.CodeBlock;
import de.spraener.nxtgen.CodeBlockImpl;
import de.spraener.nxtgen.CodeGenerator;
import de.spraener.nxtgen.GroovyCodeBlockImpl;
import de.spraener.nxtgen.java.JavaCodeBlock;
import de.spraener.nxtgen.model.ModelElement;
import de.spraener.nxtgen.oom.model.*;
import de.spraener.nxtgen.annotations.*;

@CGV19Generator(
        requiredStereotype = "cgV19CartridgeServiceDefinition",
        operatesOn = MClass.class,
        outputType = OutputType.JAVA,
        outputTo = OutputTo.OTHER,
        templateName = "/meta/CartridgeServiceLocaterTemplate.groovy",
        implementationKind = ImplementationKind.GROOVY_TEMPLATE
)
public class CartridgeServiceLocaterGenerator implements CodeGenerator {
    private Consumer<CodeBlock>[] codeBlockModifiers;

    public CartridgeServiceLocaterGenerator(Consumer<CodeBlock>... codeBlockModifiers) {
        this.codeBlockModifiers = codeBlockModifiers;
    }

    @Override
    public CodeBlock resolve(ModelElement element, String templateName) {
        CodeBlockImpl genericCB = new CodeBlockImpl("ServiceLocator");
        if( codeBlockModifiers!= null ) {
            for(Consumer<CodeBlock> m : codeBlockModifiers ) {
                m.accept(genericCB);
            }
        }
        MClass mClass = ((MClass)element);
        String catridgeClass = mClass.getTaggedValue(MetaCartridge.STYPE_CGV19CARTRIDGE_SERVICE_DEFINITION, MetaCartridge.TV_CARTRIDGE_CLASS);
        genericCB.println(catridgeClass);
        return genericCB;
        }

}

