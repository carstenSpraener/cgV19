package de.spraener.nxtgen.annotations;


import de.spraener.nxtgen.model.ModelElement;

public @interface CGV19MustacheGenerator {
    String value();
    String requiredStereotype();
    Class<? extends ModelElement> operatesOn() default ModelElement.class;
    String templateResource() default "";
}
