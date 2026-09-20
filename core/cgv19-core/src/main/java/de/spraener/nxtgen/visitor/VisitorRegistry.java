package de.spraener.nxtgen.visitor;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import de.spraener.nxtgen.model.ModelElement;
import de.spraener.nxtgen.model.Stereotype;

/**
 * Holds the cached mapping from (element-type, phase) → visitor methods.
 * <p>
 * The registry is populated by {@code AnnotatedCartridgeImpl} (and directly by tests) and
 * queried by {@link TreeWalker}. Lookup is O(1) per class-hierarchy level: a static cache
 * keyed by {@code <element-class-name>#<phase>} holds the registered visitors, and a walk
 * up the element's class hierarchy finds the most specific match.
 */
public final class VisitorRegistry {

    private VisitorRegistry() { }   // static utility – no instances

    /**
     * A registered visitor: the declaring instance (invocation target) plus its method and order.
     */
    public static final class Entry {
        private final Object owner;
        private final Method method;
        private final int order;

        Entry(Object owner, Method method, int order) {
            this.owner = owner;
            this.method = method;
            this.order = order;
        }

        /** The instance the visitor method is invoked on. */
        public Object getOwner() { return owner; }

        /** The visitor method to invoke. */
        public Method getMethod() { return method; }

        /** Ordering value (from {@link ElementVisitor#order()}). */
        int getOrder() { return order; }
    }

    /** Key: element-class + phase. Value: registered visitors for that key. */
    private static final Map<String, List<Entry>> CACHE = new HashMap<>();

    /**
     * Register a visitor method. Called during cartridge registration (and by tests).
     *
     * @param owner      the instance the method will be invoked on (must not be null for instance methods)
     * @param method     the {@code @ElementVisitor}-annotated method
     * @param operatesOn the element type the visitor handles (used as the cache key)
     * @param phase      the traversal phase (used as the cache key)
     * @param order      ordering value among visitors of the same type/phase
     */
    public static synchronized void register(Object owner, Method method,
                                             Class<? extends ModelElement> operatesOn,
                                             EBeforeOrAfter phase, int order) {
        if (phase == EBeforeOrAfter.BOTH) {
            // A BOTH visitor must be found in both phases; register under each.
            registerOnce(owner, method, operatesOn, EBeforeOrAfter.BEFORE, order);
            registerOnce(owner, method, operatesOn, EBeforeOrAfter.AFTER, order);
        } else {
            registerOnce(owner, method, operatesOn, phase, order);
        }
    }

    /**
     * Adds the entry unless an equivalent one (same declaring class, same method) is already
     * registered for that key. This makes re-instantiated cartridges idempotent: a cartridge
     * created per test run or per NextGen invocation must not register the same visitor twice,
     * which would otherwise make it fire once per instantiation. The first registration wins.
     */
    private static void registerOnce(Object owner, Method method,
                                     Class<? extends ModelElement> operatesOn,
                                     EBeforeOrAfter phase, int order) {
        List<Entry> list = CACHE.computeIfAbsent(makeKey(operatesOn, phase), k -> new ArrayList<>());
        for (Entry e : list) {
            if (e.getMethod().equals(method) && owner != null && e.getOwner() != null
                    && e.getOwner().getClass() == owner.getClass()) {
                return;   // already registered — skip duplicate
            }
        }
        list.add(new Entry(owner, method, order));
    }

    /**
     * Find the visitor methods that apply to {@code el} in the given {@code phase}.
     * <p>
     * Walks up the element's class hierarchy and returns the most specific non-empty match,
     * filtered by {@link ElementVisitor#requiredStereotype()} (empty = all) and sorted
     * stably by {@link ElementVisitor#order()}. Returns an empty list if nothing matches.
     */
    public static List<Entry> findElementVisitors(ModelElement el, EBeforeOrAfter phase) {
        Class<?> cls = el.getClass();
        while (cls != null) {
            List<Entry> list = CACHE.get(makeKey(cls, phase));
            if (list != null) {
                List<Entry> matched = new ArrayList<>();
                for (Entry e : list) {
                    if (matchesStereotype(e, el)) {
                        matched.add(e);
                    }
                }
                if (!matched.isEmpty()) {
                    matched.sort(Comparator.comparingInt(Entry::getOrder));   // stable sort
                    return matched;
                }
            }
            cls = cls.getSuperclass();
        }
        return new ArrayList<>();
    }

    /** Test hook: remove all registrations. */
    public static synchronized void clear() {
        CACHE.clear();
    }

    private static boolean matchesStereotype(Entry e, ModelElement el) {
        ElementVisitor ev = e.getMethod().getAnnotation(ElementVisitor.class);
        if (ev == null) {
            return true;
        }
        String required = ev.requiredStereotype();
        if (required == null || required.isEmpty()) {
            return true;   // empty = match all
        }
        for (Stereotype st : el.getStereotypes()) {
            if (required.equals(st.getName())) {
                return true;
            }
        }
        return false;
    }

    private static String makeKey(Class<?> cls, EBeforeOrAfter phase) {
        return cls.getName() + "#" + phase.name();
    }
}
