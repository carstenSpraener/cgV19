package de.spraener.nxtgen.ap.metameta;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Marker for model annotations. An annotation meta-annotated with this is
 * mapped to a Stereotype on the model element (its attributes become TaggedValues).
 * Retention must be CLASS or RUNTIME: with SOURCE the marker would not be
 * present in class files and could not be detected when the annotation is
 * resolved from a JAR.
 */
@Retention(RetentionPolicy.CLASS)
@Target(ElementType.ANNOTATION_TYPE)
public @interface Stereotype {
}
