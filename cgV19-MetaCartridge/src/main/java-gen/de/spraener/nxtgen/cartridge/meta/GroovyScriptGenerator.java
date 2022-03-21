package de.spraener.nxtgen.cartridge.meta;

import de.spraener.nxtgen.CodeBlock;
import de.spraener.nxtgen.CodeGenerator;
import de.spraener.nxtgen.GroovyCodeBlockImpl;
import de.spraener.nxtgen.model.ModelElement;
import de.spraener.nxtgen.oom.model.MClass;

import java.io.File;

public class GroovyScriptGenerator implements CodeGenerator {
    @Override
    public CodeBlock resolve(ModelElement element, String templateName) {
        GroovyCodeBlockImpl gcb = new GroovyCodeBlockImpl("GroovyScript", element, "/GroovyScriptTemplate.groovy");
        final MClass mc = (MClass) element;
        gcb.setToFileStrategy( () -> {
            String groovyScriptName = mc.getTaggedValue(MetaCartridge.STYPE_GROOVY_SCRIPT, MetaCartridge.TV_SCRIPT_FILE);
            return new File("src/main/resources/"+groovyScriptName);
        });
        return gcb;
    }
}
