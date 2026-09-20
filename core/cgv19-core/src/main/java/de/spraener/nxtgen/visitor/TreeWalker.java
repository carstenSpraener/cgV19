package de.spraener.nxtgen.visitor;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.List;
import java.util.function.Predicate;

import de.spraener.nxtgen.NxtGenRuntimeException;
import de.spraener.nxtgen.model.ModelElement;
import de.spraener.nxtgen.target.CodeTarget;

/**
 * Engine that walks a {@link ModelElement} tree in pre-/post-order and invokes
 * {@code @ElementVisitor} methods (resolved via {@link VisitorRegistry}).
 * <p>
 * For each element it runs the matching BEFORE visitors, then recurses into its (optionally
 * ordered/filtered) children, then runs the matching AFTER visitors in reverse order. All
 * visitors share one fresh {@link CodeTarget}, which is returned by {@link #walk()}.
 * <p>
 * Usage:
 * <pre>{@code
 * CodeTarget ct = TreeWalker.on(mClass)
 *         .order(o -> o.then(MAttribute.class).then(MOperation.class))
 *         .walk();
 * }</pre>
 */
public class TreeWalker {

    private final ModelElement root;
    private ChildOrder childOrder = null;

    /** Create a walker rooted at {@code root}. */
    public static TreeWalker on(ModelElement root) {
        return new TreeWalker(root);
    }

    private TreeWalker(ModelElement root) {
        this.root = root;
    }

    /** Optional ordering/filtering configuration for children (fluent). */
    public TreeWalker order(java.util.function.Consumer<ChildOrder> cfg) {
        ChildOrder co = new ChildOrder();
        cfg.accept(co);
        this.childOrder = co;
        return this;
    }

    /** Walk the tree and return a populated, fresh {@link CodeTarget}. */
    public CodeTarget walk() {
        CodeTarget ct = new CodeTarget();
        return walk(ct);
    }

    public CodeTarget walk(CodeTarget sharedTarget) {
        walkElement(root, sharedTarget);
        return sharedTarget;
    }

    /* ------------------------------------------------------------------ */

    private void walkElement(ModelElement el, CodeTarget sharedTarget) {
        ElementContext ctx = new ElementContext(sharedTarget, el);

        // 1. BEFORE visitors
        for (VisitorRegistry.Entry e : VisitorRegistry.findElementVisitors(el, EBeforeOrAfter.BEFORE)) {
            invokeVisitor(e, el, ctx, EBeforeOrAfter.BEFORE);
        }

        // 2. Recurse into children (ordered/filtered per childOrder)
        for (ModelElement child : sortChildren(el)) {
            walkElement(child, sharedTarget);
        }

        // 3. AFTER visitors – reverse order (stack semantics)
        List<VisitorRegistry.Entry> afters = VisitorRegistry.findElementVisitors(el, EBeforeOrAfter.AFTER);
        for (int i = afters.size() - 1; i >= 0; --i) {
            invokeVisitor(afters.get(i), el, ctx, EBeforeOrAfter.AFTER);
        }
    }

    /** Apply the {@link ChildOrder} (if any): filter, then stable-sort by priority. */
    private List<ModelElement> sortChildren(ModelElement el) {
        if (childOrder == null) {
            return el.getChilds();   // model insertion order
        }
        List<Class<? extends ModelElement>> priority = childOrder.getPriority();
        Predicate<ModelElement> filter = childOrder.getFilter();
        return el.getChilds().stream()
                .filter(filter)
                .sorted((a, b) -> Integer.compare(indexInList(a.getClass(), priority),
                                                  indexInList(b.getClass(), priority)))
                .toList();
    }

    private int indexInList(Class<?> cls, List<Class<? extends ModelElement>> priority) {
        for (int i = 0; i < priority.size(); i++) {
            if (priority.get(i).isAssignableFrom(cls)) {
                return i;
            }
        }
        return priority.size();   // unlisted types sort after all explicit entries (stable)
    }

    /** Reflectively invoke a visitor on its owner (2-arg or 3-arg form). */
    private void invokeVisitor(VisitorRegistry.Entry e, ModelElement el, ElementContext ctx, EBeforeOrAfter phase) {
        Method m = e.getMethod();
        Object owner = e.getOwner();
        try {
            if (m.getParameterCount() == 2) {                 // (Element, Context)
                m.invoke(owner, el, ctx);
            } else if (m.getParameterCount() == 3) {          // (Element, Context, Phase)
                m.invoke(owner, el, ctx, phase);
            } else {
                throw new IllegalStateException("Unsupported @ElementVisitor signature: " + m);
            }
        } catch (ReflectiveOperationException ex) {
            Throwable cause = (ex instanceof InvocationTargetException && ex.getCause() != null)
                    ? ex.getCause() : ex;
            throw new NxtGenRuntimeException(
                    "Error invoking visitor '" + m.getName() + "' on element '" + el.getName() + "'", cause);
        }
    }
}
