package de.spraener.nxtgen.annotations;

import de.spraener.nxtgen.model.ModelElement;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
/**
 * This annotation marks the annotated target as a transformation for cgV19.
 * It can be applied to a Class which than has to be an implementation of
 * Transformation or a method inside a @CGV19Component. The method has to
 * take a ModelElement parameter.
 *
 * @see de.spraener.nxtgen.Transformation
 * @see CGV19Component
 */
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.TYPE, ElementType.METHOD})
public @interface CGV19Transformation {
    String requiredStereotype() default "";
    Class< ? extends ModelElement> operatesOn() default ModelElement.class;
}
