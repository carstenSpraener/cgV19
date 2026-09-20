package de.spraener.nxtgen.visitor;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Predicate;

import de.spraener.nxtgen.model.ModelElement;

/**
 * DSL used by {@link TreeWalker#order(java.util.function.Consumer)} to control how a
 * parent's children are visited.
 * <p>
 * Two independent knobs:
 * <ul>
 *   <li><b>Priority</b> – an ordered list of element types; a child whose runtime class is
 *       assignable from an earlier entry is visited before one matching a later entry.
 *       Children whose type matches no entry keep their original relative order and are
 *       visited after all explicitly-listed types.</li>
 *   <li><b>Filter</b> – an optional predicate applied to every child before ordering;
 *       children for which it returns {@code false} are skipped entirely. Defaults to
 *       accept-all.</li>
 * </ul>
 */
public class ChildOrder {

    /** Priority list – lower index = earlier visitation. */
    private final List<Class<? extends ModelElement>> priority = new ArrayList<>();

    /** Optional predicate that filters children before ordering. Default: accept all. */
    private Predicate<ModelElement> filter = e -> true;

    /** Add a type to the priority list (fluent). */
    public ChildOrder then(Class<? extends ModelElement> type) {
        priority.add(type);
        return this;
    }

    /** Set a predicate applied to every child before ordering (fluent). */
    public ChildOrder setPredicate(Predicate<ModelElement> predicate) {
        this.filter = predicate;
        return this;
    }

    /** Package-private accessor used by {@link TreeWalker}. */
    List<Class<? extends ModelElement>> getPriority() {
        return priority;
    }

    /** Package-private accessor used by {@link TreeWalker}. */
    Predicate<ModelElement> getFilter() {
        return filter;
    }
}
