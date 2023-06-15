package de.spraener.nxtgen.cartridge.meta;

import java.io.File;
import java.util.function.Consumer;

import de.spraener.nxtgen.CodeBlock;
import de.spraener.nxtgen.CodeGenerator;
import de.spraener.nxtgen.GroovyCodeBlockImpl;
import de.spraener.nxtgen.java.JavaCodeBlock;
import de.spraener.nxtgen.model.ModelElement;
import de.spraener.nxtgen.oom.model.*;
import de.spraener.nxtgen.annotations.*;

@CGV19Generator(
        requiredStereotype = "GroovyScript",
        operatesOn = MClass.class,
        outputType = OutputType.JAVA,
        outputTo = OutputTo.OTHER,
        tempalteName = "/GroovyScriptTemplate.groovy",
        implementationKind = ImplementationKind.GROOVY_TEMPLATE
)
public class GroovyScriptGenerator implements CodeGenerator {

    @Override
    public CodeBlock resolve(ModelElement element, String templateName) {
        final MClass mc = (MClass) element;
        GroovyCodeBlockImpl gcb = new GroovyCodeBlockImpl("GroovyScript", element, "/GroovyScriptTemplate.groovy");
        gcb.setToFileStrategy( () -> {
            String groovyScriptName = mc.getTaggedValue(MetaCartridge.STYPE_GROOVY_SCRIPT, MetaCartridge.TV_SCRIPT_FILE);
            return new File("src/main/resources/"+groovyScriptName);
        });
        return gcb;
    }

}

