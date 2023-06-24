package de.spraener.nxtgen.cartridge.meta;

import de.spraener.nxtgen.CodeBlock;
import de.spraener.nxtgen.CodeBlockImpl;
import de.spraener.nxtgen.CodeGenerator;
import de.spraener.nxtgen.model.ModelElement;
import de.spraener.nxtgen.oom.model.*;
import de.spraener.nxtgen.annotations.*;

import java.io.File;

@CGV19Generator(
        requiredStereotype = "cgV19CartridgeServiceDefinition",
        operatesOn = MClass.class,
        outputType = OutputType.JAVA,
        outputTo = OutputTo.OTHER,
        templateName = "/meta/CartridgeServiceLocaterTemplate.groovy",
        implementationKind = ImplementationKind.GROOVY_TEMPLATE
)
public class CartridgeServiceLocaterGenerator implements CodeGenerator {
    @Override
    public CodeBlock resolve(ModelElement element, String templateName) {
        CodeBlockImpl genericCB = new CodeBlockImpl("ServiceLocator");
        genericCB.setToFileStrategy( () -> {
            return new File("src/main/resources/META-INF/services/de.spraener.nxtgen.Cartridge");
        });
        MClass mClass = ((MClass)element);
        String catridgeClass = mClass.getTaggedValue(MetaCartridge.STYPE_CGV19CARTRIDGE_SERVICE_DEFINITION, MetaCartridge.TV_CARTRIDGE_CLASS);
        genericCB.println(catridgeClass);
        return genericCB;
    }
}
