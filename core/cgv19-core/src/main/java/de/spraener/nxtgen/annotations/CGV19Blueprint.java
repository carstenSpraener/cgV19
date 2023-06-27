package de.spraener.nxtgen.annotations;

import de.spraener.nxtgen.model.ModelElement;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
public @interface CGV19Blueprint {
    String value();
    String requiredStereotype();
    Class<? extends ModelElement> operatesOn() default ModelElement.class;
    String outputDir() default "";
}
