package de.spraener.nxtgen.annotations;

import de.spraener.nxtgen.model.ModelElement;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * This annotation marks the annotated target as a generator for cgV19.
 * It can be applied to a Class which than has to be an implementation of
 * CodeGenerator or a method inside a @CGV19Component. The method has to
 * takes two parameters ModelElement and String.
 *
 * @see de.spraener.nxtgen.CodeGenerator
 * @see CGV19Component
 */
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.TYPE, ElementType.METHOD})
public @interface CGV19Generator {
    String requiredStereotype();
    Class<? extends ModelElement> operatesOn() default ModelElement.class;
    String templateName() default "";
    OutputType outputType() default OutputType.OTHER;
    OutputTo outputTo() default OutputTo.SRC_GEN;
    ImplementationKind implementationKind() default ImplementationKind.GROOVY_TEMPLATE;
}
