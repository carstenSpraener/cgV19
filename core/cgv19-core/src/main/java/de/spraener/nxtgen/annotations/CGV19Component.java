package de.spraener.nxtgen.annotations;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * A class annotated with this annotation can hold several methods
 * which are annotated with CGV19Generator or CGV19Transformation,
 * and each such method will be treated as a Generator or Transformation.
 *
 * This makes implementation for small cartridges easier and different
 * aspects can be grouped together in one class.
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
public @interface CGV19Component {
}
