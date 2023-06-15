package de.spraener.nxtgen;

import de.spraener.nxtgen.annotations.CGV19Generator;
import de.spraener.nxtgen.model.ModelElement;
import de.spraener.nxtgen.model.Stereotype;

import java.util.function.Consumer;

public class GeneratorWrapper {
    private CGV19Generator generatorSpec;
    private Class<? extends CodeGenerator> genertorClass;

    public GeneratorWrapper(Class<?> g) {
        generatorSpec = g.getAnnotation(CGV19Generator.class);
        if( !CodeGenerator.class.isAssignableFrom(g) ) {
            throw new IllegalArgumentException("Class "+g.getName()+" does not implemente interface "+CodeGenerator.class.getName());
        }
        genertorClass = (Class<? extends CodeGenerator>)g;
        try {
            g.getConstructor(Consumer[].class);
        } catch( NoSuchMethodException nsmXC ) {
            try {
                g.getConstructor();
            } catch (NoSuchMethodException nsmXC2) {
                throw new IllegalArgumentException("Class " + g.getName() + " does not provide a default constructor.");
            }
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

    public CodeGenerator createCodeGeneratorInstance() {
        String msg = "";
        Throwable error;
        try {
            return this.genertorClass.getConstructor(Consumer[].class).newInstance((Object)new Consumer[] {});
        } catch( ReflectiveOperationException roXC ) {
            msg += roXC.getLocalizedMessage();
        }
        try {
            return this.genertorClass.getConstructor().newInstance();
        } catch( ReflectiveOperationException roXC ) {
            msg += roXC.getLocalizedMessage();
            error = roXC;
        }
        throw new RuntimeException("Unable to create an Instance of "+genertorClass.getName()+": "+msg, error);
    }
}
