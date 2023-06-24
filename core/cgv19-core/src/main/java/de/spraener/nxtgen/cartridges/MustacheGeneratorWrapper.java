package de.spraener.nxtgen.cartridges;

import de.spraener.nxtgen.CodeBlock;
import de.spraener.nxtgen.MustacheGenerator;
import de.spraener.nxtgen.annotations.CGV19MustacheGenerator;
import de.spraener.nxtgen.model.ModelElement;

import java.lang.reflect.Method;
import java.util.Map;

public class MustacheGeneratorWrapper extends GeneratorWrapper {

    public MustacheGeneratorWrapper(Class<?> g, Method cgMethod) {
        super(g, cgMethod);
    }

    public MustacheGeneratorWrapper(Class<?> g) {
        this(g, GeneratorWrapper.readCgMethod(g));
    }

    protected GeneratorSpec readGeneratorSpec(Class<?> g, Method cgMethod) {
        GeneratorSpec generatorSpec = GeneratorSpec.from(g.getAnnotation(CGV19MustacheGenerator.class));
        if (generatorSpec == null) {
            generatorSpec = GeneratorSpec.from(cgMethod.getAnnotation(CGV19MustacheGenerator.class));
        }
        return generatorSpec;
    }

    @Override
    public CodeBlock resolve(ModelElement element, String templateName) {
        MustacheGenerator mustacheGenerator = new MustacheGenerator(
                generatorSpec.getTemplateName(),
                generatorSpec.getOutputFile(),
                this::fillMustacheScope);
        return mustacheGenerator.resolve(element, templateName);
    }

    private void fillMustacheScope(ModelElement modelElement, Map<String, Object> stringObjectMap) {
        try {
            Object generatorInstance = this.generatorClass.getConstructor().newInstance();
            this.cgMethod.invoke(generatorInstance, modelElement, stringObjectMap);
        } catch( ReflectiveOperationException roXC ) {
            throw new RuntimeException("Error while filling scope for "+this.generatorClass.getName()+"."+this.cgMethod.getName());
        }
    }
}
