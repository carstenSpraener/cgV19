package de.spraener.nxtgen;

import de.spraener.nxtgen.annotations.CGV19Transformation;
import de.spraener.nxtgen.model.ModelElement;
import de.spraener.nxtgen.model.Stereotype;

public class TransformationWrapper implements Transformation {
    private CGV19Transformation transformationSpec;
    private Class<? extends Transformation> transformationClass;

    public TransformationWrapper(Class<?> t) {
        this.transformationSpec = t.getAnnotation(CGV19Transformation.class);
        if( ! Transformation.class.isAssignableFrom(t) ) {
            throw new IllegalArgumentException("Class "+t.getName()+" does not implement "+Transformation.class);
        }
        try {
            t.getConstructor();
        } catch( NoSuchMethodException nsmXC ) {
            throw new IllegalArgumentException("Class "+t.getName()+" does not provide a default constructor.");
        }
        this.transformationClass = (Class<? extends Transformation>)t;
    }

    @Override
    public void doTransformation(ModelElement element) {
        if( this.transformationSpec.operatesOn().isAssignableFrom(element.getClass()) ) {
            if( hasStereotype(element, transformationSpec.requiredStereotype()) ) {
                try {
                    this.transformationClass.getConstructor().newInstance().doTransformation(element);
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
