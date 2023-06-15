package de.spraener.nxtgen.annotations;

import de.spraener.nxtgen.model.ModelElement;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.TYPE})
public @interface CGV19Generator {
    String requiredStereotype();
    Class<? extends ModelElement> operatesOn() default ModelElement.class;
    String tempalteName() default "";
    OutputType outputType() default OutputType.OTHER;
    OutputTo outputTo() default OutputTo.SRC_GEN;
    ImplementationKind implementationKind() default ImplementationKind.GROOVY_TEMPLATE;
}
