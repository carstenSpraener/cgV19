package de.spraener.nxtgen.visitor;

/**
 * Traversal phase for a {@link ElementVisitor} method.
 * <ul>
 *   <li>{@code BEFORE} – invoked before the element's children are visited.</li>
 *   <li>{@code AFTER}  – invoked after the element's children are visited.</li>
 *   <li>{@code BOTH}   – invoked in both phases; the method receives this value as a parameter.</li>
 * </ul>
 */
public enum EBeforeOrAfter {
    BEFORE,
    AFTER,
    BOTH
}
