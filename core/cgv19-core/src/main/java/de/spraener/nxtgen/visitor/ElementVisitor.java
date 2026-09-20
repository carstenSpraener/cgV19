package de.spraener.nxtgen.visitor;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import static java.lang.annotation.RetentionPolicy.RUNTIME;

import de.spraener.nxtgen.model.ModelElement;

/**
 * Marks a method as an element-visitor for the {@link TreeWalker} engine.
 * <p>
 * Annotated methods are discovered (typically on {@code @CGV19Component} classes) and
 * invoked by the walker as it traverses a {@link ModelElement} tree. A method is
 * invoked for an element when:
 * <ul>
 *   <li>{@link #operatesOn()} is assignable from the element's runtime class (sub-type matching),</li>
 *   <li>{@link #requiredStereotype()} is empty or matches a stereotype present on the element,</li>
 *   <li>{@link #phase()} matches the current traversal phase.</li>
 * </ul>
 */
@Target(ElementType.METHOD)
@Retention(RUNTIME)
public @interface ElementVisitor {

    /** The {@link ModelElement} subclass this visitor handles. */
    Class<? extends ModelElement> operatesOn();

    /** Optional stereotype filter – an empty string matches all elements. */
    String requiredStereotype() default "";

    /** Phase in which the method is invoked (default {@link EBeforeOrAfter#BEFORE}). */
    EBeforeOrAfter phase() default EBeforeOrAfter.BEFORE;

    /** Optional order value for deterministic sequencing among visitors of the same type (default 0). */
    int order() default 0;
}
