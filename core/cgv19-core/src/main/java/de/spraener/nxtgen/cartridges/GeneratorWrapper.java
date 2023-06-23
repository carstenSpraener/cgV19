package de.spraener.nxtgen.cartridges;

import de.spraener.nxtgen.CodeBlock;
import de.spraener.nxtgen.CodeGenerator;
import de.spraener.nxtgen.annotations.CGV19Generator;
import de.spraener.nxtgen.annotations.OutputTo;
import de.spraener.nxtgen.annotations.OutputType;
import de.spraener.nxtgen.java.JavaCodeBlock;
import de.spraener.nxtgen.model.ModelElement;
import de.spraener.nxtgen.model.Stereotype;

import java.lang.reflect.Method;
import java.util.function.Consumer;

public class GeneratorWrapper implements CodeGenerator {
    private CGV19Generator generatorSpec;
    private Class<? extends CodeGenerator> generatorClass;
    private Method cgMethod = null;

    public GeneratorWrapper(Class<?> g, Method cgMethod) {
        generatorSpec = g.getAnnotation(CGV19Generator.class);
        if(generatorSpec == null ) {
            generatorSpec = cgMethod.getAnnotation(CGV19Generator.class);
        }
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

    public GeneratorWrapper(Class<?> g) {
        this(g, readCgMethod(g));
    }

    private static Method readCgMethod(Class<?> g) {
        try {
            return g.getMethod("resolve", ModelElement.class, String.class);
        } catch( ReflectiveOperationException roXC ) {
            throw new IllegalArgumentException("Class "+g.getName()+" does not provide an accessible CodeBlock resolve(ModelElement, String) method. ");
        }
    }

    public Class<? extends ModelElement> operatesOn() {
        return this.generatorSpec.operatesOn();
    }

    public String requiredStereotype() {
        return this.generatorSpec.requiredStereotype();
    }

    public boolean matches(ModelElement e ) {
        if( e.getClass().isAssignableFrom(this.generatorSpec.operatesOn()) ) {
            if( this.generatorSpec.requiredStereotype()==null || "".equals(this.generatorSpec.requiredStereotype())) {
                return true;
            }
            return matchesStereotype( e, this.generatorSpec.requiredStereotype());
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
            Object generatorInstance = this.generatorClass.getConstructor().newInstance();
            CodeBlock cb = (CodeBlock) this.cgMethod.invoke(generatorInstance, element, templateName);
            return cb;
        } catch( ReflectiveOperationException roXC ) {
            throw new RuntimeException("Error while excuting code generation for "+this.generatorClass.getName()+"."+this.cgMethod.getName(), roXC);
        }
    }

    public CodeGenerator createCodeGeneratorInstance() {
        String msg = "";
        Throwable error;
        try {
            return this.generatorClass.getConstructor(Consumer[].class).newInstance((Object)new Consumer[] {});
        } catch( ReflectiveOperationException roXC ) {
            msg += roXC.getLocalizedMessage();
        }
        try {
            return this.generatorClass.getConstructor().newInstance();
        } catch( ReflectiveOperationException roXC ) {
            msg += roXC.getLocalizedMessage();
            error = roXC;
        }
        throw new RuntimeException("Unable to create an Instance of "+ generatorClass.getName()+": "+msg, error);
    }
}
