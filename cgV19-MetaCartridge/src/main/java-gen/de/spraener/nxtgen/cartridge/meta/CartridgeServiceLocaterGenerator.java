package de.spraener.nxtgen.cartridge.meta;

import de.spraener.nxtgen.CodeBlock;
import de.spraener.nxtgen.CodeBlockImpl;
import de.spraener.nxtgen.CodeGenerator;
import de.spraener.nxtgen.GroovyCodeBlockImpl;
import de.spraener.nxtgen.model.ModelElement;
import de.spraener.nxtgen.oom.model.MClass;

import java.io.File;

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
