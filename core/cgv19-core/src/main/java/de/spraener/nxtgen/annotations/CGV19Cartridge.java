package de.spraener.nxtgen.annotations;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * A class annotated with this annotation is the root of a cgv19
 * cartridge. The class needs to be a sublass of AnnotatedCartridgeImpl
 * and must be specified as a service provider in the META-INF
 * directory of the jar file.
 *
 * @see de.spraener.nxtgen.cartridges.AnnotatedCartridgeImpl
 * @see java.util.ServiceLoader
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
public @interface CGV19Cartridge {
    String value() default "";
}
