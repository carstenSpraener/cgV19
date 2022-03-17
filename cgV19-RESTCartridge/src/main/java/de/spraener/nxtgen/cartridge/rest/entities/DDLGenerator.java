package de.spraener.nxtgen.cartridge.rest.entities;

import de.spraener.nxtgen.CodeBlock;
import de.spraener.nxtgen.CodeGenerator;
import de.spraener.nxtgen.GroovyCodeBlockImpl;
import de.spraener.nxtgen.filestrategies.GeneralFileStrategy;
import de.spraener.nxtgen.model.ModelElement;

public class DDLGenerator implements CodeGenerator {
    @Override
    public CodeBlock resolve(ModelElement element, String templateName) {
        GroovyCodeBlockImpl gcb = new GroovyCodeBlockImpl("ddl", element, "/entity/DDLTemplate.groovy");
        gcb.setToFileStrategy(new GeneralFileStrategy("src/main/sql", element.getName(), "ddl"));
        return gcb;
    }
}
