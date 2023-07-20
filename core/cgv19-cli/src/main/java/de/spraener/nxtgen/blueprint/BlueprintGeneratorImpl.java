package de.spraener.nxtgen.blueprint;

import de.spraener.nxtgen.CodeBlock;
import de.spraener.nxtgen.CodeGenerator;
import de.spraener.nxtgen.incubator.BlueprintCodeBlock;
import de.spraener.nxtgen.incubator.BlueprintCompiler;
import de.spraener.nxtgen.model.ModelElement;

import java.util.Map;

public class BlueprintGeneratorImpl implements CodeGenerator {
    private BlueprintCompiler bpc;
    public BlueprintGeneratorImpl(BlueprintCompiler bpc) {
        this.bpc = bpc;
    }

    @Override
    public CodeBlock resolve(ModelElement element, String templateName) {
        fillMustacheScope(element);
        return new BlueprintCodeBlock(bpc, ".");
    }

    private void fillMustacheScope(ModelElement element) {
        for( Map.Entry<String,String> e : element.getProperties().entrySet() ) {
            this.bpc.getScope().put(e.getKey(), e.getValue());
        }
    }
}
