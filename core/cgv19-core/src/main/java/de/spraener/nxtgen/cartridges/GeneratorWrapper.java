package de.spraener.nxtgen.cartridges;

import de.spraener.nxtgen.CodeBlock;
import de.spraener.nxtgen.CodeGenerator;
import de.spraener.nxtgen.annotations.CGV19Generator;
import de.spraener.nxtgen.annotations.OutputTo;
import de.spraener.nxtgen.annotations.OutputType;
import de.spraener.nxtgen.java.JavaCodeBlock;
import de.spraener.nxtgen.model.ModelElement;
import de.spraener.nxtgen.model.Stereotype;

import java.lang.reflect.Constructor;
import java.lang.reflect.Method;
import java.util.function.Consumer;

public class GeneratorWrapper implements CodeGenerator {
    protected GeneratorSpec generatorSpec;
    protected Class<? extends CodeGenerator> generatorClass;
    protected Method cgMethod = null;

    public GeneratorWrapper(Class<?> g, Method cgMethod) {
        this.generatorSpec = readGeneratorSpec(g, cgMethod);
        try {
            g.getConstructor(Consumer[].class);
        } catch( NoSuchMethodException nsmXC ) {
            try {
                g.getConstructor();
            } catch (NoSuchMethodException nsmXC2) {
                throw new IllegalArgumentException("Class " + g.getName() + " does not provide a default constructor.");
            }
        }
        generatorClass = (Class<? extends CodeGenerator>)g;
        this.cgMethod = cgMethod;
    }

    protected GeneratorSpec readGeneratorSpec(Class<?> g, Method cgMethod) {
        GeneratorSpec  generatorSpec = GeneratorSpec.from(g.getAnnotation(CGV19Generator.class));
        if(generatorSpec == null ) {
            generatorSpec = GeneratorSpec.from(cgMethod.getAnnotation(CGV19Generator.class));
        }
        return generatorSpec;
    }

    public GeneratorWrapper(Class<?> g) {
        this(g, readCgMethod(g));
    }

    protected static Method readCgMethod(Class<?> g) {
        try {
            return g.getMethod("resolve", ModelElement.class, String.class);
        } catch( ReflectiveOperationException roXC ) {
            throw new IllegalArgumentException("Class "+g.getName()+" does not provide an accessible CodeBlock resolve(ModelElement, String) method. ");
        }
    }

    public Class<? extends ModelElement> operatesOn() {
        return this.generatorSpec.getOperatesOn();
    }

    public String requiredStereotype() {
        return this.generatorSpec.getRequiredStereotype();
    }

    public boolean matches(ModelElement e ) {
        if( e.getClass().isAssignableFrom(this.generatorSpec.getOperatesOn()) ) {
            if( this.generatorSpec.getRequiredStereotype()==null || "".equals(this.generatorSpec.getRequiredStereotype())) {
                return true;
            }
            return matchesStereotype( e, this.generatorSpec.getRequiredStereotype());
        }
        return false;
    }

    private boolean matchesStereotype(ModelElement e, String s) {
        for( Stereotype sType : e.getStereotypes() ) {
            if( sType.getName().equals(s) ) {
                return true;
            }
        }
        return false;
    }

    @Override
    public CodeBlock resolve(ModelElement element, String templateName) {
        try {
            Object generatorInstance = null;
            for( Constructor<?> c : this.generatorClass.getConstructors() ) {
                                if( c.getParameterCount()==0) {
                                    generatorInstance = c.newInstance();
                                } else if( c.getParameterCount()==1) {
                                    Consumer<CodeBlock>[] arg = new Consumer[]{};
                                    generatorInstance = c.newInstance(new Object[]{new Consumer[]{}});
                                }
            }
            if( generatorInstance == null ) {
                throw new IllegalStateException("Unable to instantiate generator of class "+this.generatorClass.getName());
            }
            CodeBlock cb = (CodeBlock) this.cgMethod.invoke(generatorInstance, element, templateName);
            return cb;
        } catch( ReflectiveOperationException roXC ) {
            throw new RuntimeException("Error while excuting code generation for "+this.generatorClass.getName()+"."+this.cgMethod.getName(), roXC);
        }
    }

}
