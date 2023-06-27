package de.spraener.nxtgen.incubator;

import de.spraener.nxtgen.CodeBlock;
import de.spraener.nxtgen.annotations.CGV19Blueprint;
import de.spraener.nxtgen.cartridges.GeneratorSpec;
import de.spraener.nxtgen.cartridges.GeneratorWrapper;
import de.spraener.nxtgen.model.ModelElement;

import java.lang.reflect.Method;
import java.util.Map;

public class BlueprintGeneratorWrapper extends GeneratorWrapper {
    private GeneratorSpec generatorSpec;

    public BlueprintGeneratorWrapper(Class<?> g, Method cgMethod) {
        super(g, cgMethod);
    }

    public BlueprintGeneratorWrapper(Class<?> g) {
        this(g, GeneratorWrapper.readCgMethod(g));
    }

    protected GeneratorSpec readGeneratorSpec(Class<?> g, Method cgMethod) {
        GeneratorSpec generatorSpec = GeneratorSpec.from(g.getAnnotation(CGV19Blueprint.class));
        if (generatorSpec == null) {
            generatorSpec = GeneratorSpec.from(cgMethod.getAnnotation(CGV19Blueprint.class));
        }
        return generatorSpec;
    }

    @Override
    public CodeBlock resolve(ModelElement element, String templateName) {
        BlueprintCompiler bpc = new BlueprintCompiler(this.generatorSpec.getTemplateName());
        Map<String, String> scope = fillMustacheScope(element);
        for( Map.Entry e : scope.entrySet() ) {
            bpc.getScope().put((String)e.getKey(), (String)e.getValue());
        }
        return new BlueprintCodeBlock(bpc, this.generatorSpec.getOutputFile());
    }

    private Map<String, String> fillMustacheScope(ModelElement modelElement) {
        try {
            Object generatorInstance = this.generatorClass.getConstructor().newInstance();
            return (Map<String,String>) this.cgMethod.invoke(generatorInstance, modelElement);
        } catch( ReflectiveOperationException roXC ) {
            throw new RuntimeException("Error while filling scope for "+this.generatorClass.getName()+"."+this.cgMethod.getName());
        }
    }

}
