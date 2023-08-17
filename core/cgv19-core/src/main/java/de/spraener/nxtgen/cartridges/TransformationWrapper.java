package de.spraener.nxtgen.cartridges;

import de.spraener.nxtgen.Transformation;
import de.spraener.nxtgen.annotations.CGV19Transformation;
import de.spraener.nxtgen.model.ModelElement;
import de.spraener.nxtgen.model.Stereotype;

import java.lang.reflect.Method;

public class TransformationWrapper implements Transformation {
    private CGV19Transformation transformationSpec;
    private Class<?> transformationClass;
    private Method txMethod = null;

    public TransformationWrapper(Class<?> t, Method txMethod) {
        this.transformationSpec = t.getAnnotation(CGV19Transformation.class);
        if( this.transformationSpec == null ) {
            this.transformationSpec = txMethod.getAnnotation(CGV19Transformation.class);
        }
        try {
            t.getConstructor();
        } catch (NoSuchMethodException nsmXC) {
            throw new IllegalArgumentException("Class " + t.getName() + " does not provide a default constructor.");
        }
        this.transformationClass = t;
        this.txMethod = txMethod;
    }

    public TransformationWrapper(Class<?> t) {
            this(t, getTXMethod(t));
    }

    private static Method getTXMethod(Class<?> t) {
        try {
            return t.getMethod("doTransformation", new Class[]{ModelElement.class});
        } catch( ReflectiveOperationException roXC ){
            throw new IllegalArgumentException("Class "+t.getName()+" does not implement Transformation-Interface.");
        }
    }

    @Override
    public void doTransformation(ModelElement element) {
        if( this.transformationSpec == null || this.transformationSpec.operatesOn()==null) {
            return;
        }
        if( this.transformationSpec.operatesOn().isAssignableFrom(element.getClass()) ) {
            if( hasStereotype(element, transformationSpec.requiredStereotype()) ) {
                try {
                    Object txInstance = this.transformationClass.getConstructor().newInstance();
                    this.txMethod.invoke(txInstance,element);
                } catch( ReflectiveOperationException roXC ) {
                    throw new RuntimeException("Exception while calling transformation.", roXC);
                }
            }
        }
    }

    private boolean hasStereotype(ModelElement element, String s) {
        for(Stereotype sType : element.getStereotypes() ) {
            if( sType.getName().equals(s) ) {
                return true;
            }
        }
        return false;
    }
}
