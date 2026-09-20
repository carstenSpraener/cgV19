package de.spraener.nxtgen.visitor;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.lang.reflect.Method;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import de.spraener.nxtgen.NxtGenRuntimeException;
import de.spraener.nxtgen.oom.model.MAttribute;
import de.spraener.nxtgen.oom.model.MClass;
import de.spraener.nxtgen.oom.model.MOperation;

/**
 * Tests for the {@link TreeWalker} engine.
 */
class TreeWalkerTest {

    @BeforeEach
    void resetRegistry() {
        VisitorRegistry.clear();
    }

    /** Records visitation order into a log. */
    static class RecordingVisitor {
        final StringBuilder log = new StringBuilder();

        @ElementVisitor(operatesOn = MClass.class, phase = EBeforeOrAfter.BEFORE)
        public void beforeClass(MClass cls, ElementContext ctx) { log.append("BEFORE_CLASS|"); }

        @ElementVisitor(operatesOn = MAttribute.class)
        public void field(MAttribute attr, ElementContext ctx) { log.append("FIELD|"); }

        @ElementVisitor(operatesOn = MOperation.class)
        public void method(MOperation op, ElementContext ctx) { log.append("METHOD|"); }

        @ElementVisitor(operatesOn = MClass.class, phase = EBeforeOrAfter.AFTER)
        public void afterClass(MClass cls, ElementContext ctx) { log.append("AFTER_CLASS|"); }
    }

    private void registerAll(RecordingVisitor rv) throws Exception {
        VisitorRegistry.register(rv, RecordingVisitor.class.getMethod("beforeClass", MClass.class, ElementContext.class), MClass.class, EBeforeOrAfter.BEFORE, 0);
        VisitorRegistry.register(rv, RecordingVisitor.class.getMethod("field", MAttribute.class, ElementContext.class), MAttribute.class, EBeforeOrAfter.BEFORE, 0);
        VisitorRegistry.register(rv, RecordingVisitor.class.getMethod("method", MOperation.class, ElementContext.class), MOperation.class, EBeforeOrAfter.BEFORE, 0);
        VisitorRegistry.register(rv, RecordingVisitor.class.getMethod("afterClass", MClass.class, ElementContext.class), MClass.class, EBeforeOrAfter.AFTER, 0);
    }

    /** Model: Person with name, age (attrs) then getName (op). */
    private MClass personModel() {
        MClass root = new MClass();
        root.setName("Person");
        root.createAttribute("name", "String");
        root.createAttribute("age", "int");
        root.createOperation("getName");
        return root;
    }

    @Test
    void basicPrePostOrder() throws Exception {
        RecordingVisitor rv = new RecordingVisitor();
        registerAll(rv);

        TreeWalker.on(personModel()).walk();

        assertEquals("BEFORE_CLASS|FIELD|FIELD|METHOD|AFTER_CLASS|", rv.log.toString());
    }

    @Test
    void explicitOrderingReordersChildren() throws Exception {
        RecordingVisitor rv = new RecordingVisitor();
        registerAll(rv);

        // Insertion order: operation FIRST, then attributes.
        MClass root = new MClass();
        root.setName("Person");
        root.createOperation("getName");
        root.createAttribute("name", "String");
        root.createAttribute("age", "int");

        // Priority: attributes before operations → FIELD entries precede METHOD.
        TreeWalker.on(root).order(o -> o.then(MAttribute.class).then(MOperation.class)).walk();

        assertEquals("BEFORE_CLASS|FIELD|FIELD|METHOD|AFTER_CLASS|", rv.log.toString());
    }

    @Test
    void defaultInsertionOrderIsRespected() throws Exception {
        RecordingVisitor rv = new RecordingVisitor();
        registerAll(rv);

        // Insertion order: op, attr, attr → without .order(), the walk follows insertion order.
        MClass root = new MClass();
        root.setName("Person");
        root.createOperation("getName");
        root.createAttribute("name", "String");
        root.createAttribute("age", "int");

        TreeWalker.on(root).walk();   // no .order()

        assertEquals("BEFORE_CLASS|METHOD|FIELD|FIELD|AFTER_CLASS|", rv.log.toString());
    }

    /** Subclass of MAttribute to exercise sub-type matching. */
    static class SubAttribute extends MAttribute {
        public SubAttribute() { }
    }

    @Test
    void subTypeMatchingInvokesSuperTypeVisitor() throws Exception {
        RecordingVisitor rv = new RecordingVisitor();
        // Register ONLY a visitor for MAttribute (not the subclass).
        VisitorRegistry.register(rv, RecordingVisitor.class.getMethod("field", MAttribute.class, ElementContext.class),
                MAttribute.class, EBeforeOrAfter.BEFORE, 0);

        MClass root = new MClass();
        root.setName("Person");
        SubAttribute sub = new SubAttribute();
        sub.setParent(root);
        root.getChilds().add(sub);

        TreeWalker.on(root).walk();

        assertEquals("FIELD|", rv.log.toString(), "visitor for MAttribute must fire on its subclass");
    }

    @Test
    void exceptionInVisitorAbortsWalk() throws Exception {
        ThrowingVisitor tv = new ThrowingVisitor();
        VisitorRegistry.register(tv, ThrowingVisitor.class.getMethod("boom", MAttribute.class, ElementContext.class),
                MAttribute.class, EBeforeOrAfter.BEFORE, 10);
        VisitorRegistry.register(tv, ThrowingVisitor.class.getMethod("afterBoom", MAttribute.class, ElementContext.class),
                MAttribute.class, EBeforeOrAfter.BEFORE, 20);

        MClass root = new MClass();
        root.setName("Person");
        root.createAttribute("name", "String");

        assertThrows(NxtGenRuntimeException.class, () -> TreeWalker.on(root).walk());
        assertEquals("BOOM|", tv.log.toString(), "no visitor after the throwing one may run");
    }

    static class ThrowingVisitor {
        final StringBuilder log = new StringBuilder();

        @ElementVisitor(operatesOn = MAttribute.class, order = 10)
        public void boom(MAttribute a, ElementContext ctx) { log.append("BOOM|"); throw new RuntimeException("kaboom"); }

        @ElementVisitor(operatesOn = MAttribute.class, order = 20)
        public void afterBoom(MAttribute a, ElementContext ctx) { log.append("AFTER_BOOM|"); }
    }

    @Test
    void bothPhaseVisitorRunsInBothPhases() throws Exception {
        BothVisitor bv = new BothVisitor();
        VisitorRegistry.register(bv, BothVisitor.class.getMethod("visit", MClass.class, ElementContext.class, EBeforeOrAfter.class),
                MClass.class, EBeforeOrAfter.BOTH, 0);

        MClass root = new MClass();
        root.setName("Person");

        TreeWalker.on(root).walk();

        assertEquals("OPEN|CLOSE|", bv.log.toString(), "BOTH visitor runs in BEFORE then AFTER");
    }

    static class BothVisitor {
        final StringBuilder log = new StringBuilder();

        @ElementVisitor(operatesOn = MClass.class, phase = EBeforeOrAfter.BOTH)
        public void visit(MClass cls, ElementContext ctx, EBeforeOrAfter phase) {
            log.append(phase == EBeforeOrAfter.BEFORE ? "OPEN|" : "CLOSE|");
        }
    }

    @Test
    void unlistedChildTypeSortsAfterListed() throws Exception {
        RecordingVisitor rv = new RecordingVisitor();
        registerAll(rv);

        // Insertion order: operation first, then attribute. Only MAttribute is listed in the
        // priority; the unlisted MOperation must sort AFTER it (covering indexInList's not-found path).
        MClass root = new MClass();
        root.setName("Person");
        root.createOperation("getName");   // inserted first, but unlisted → sorts last
        root.createAttribute("name", "String");

        TreeWalker.on(root).order(o -> o.then(MAttribute.class)).walk();

        assertEquals("BEFORE_CLASS|FIELD|METHOD|AFTER_CLASS|", rv.log.toString(),
                "listed MAttribute must be visited before the unlisted MOperation");
    }

    @Test
    void filterPredicateExcludesChildren() throws Exception {
        RecordingVisitor rv = new RecordingVisitor();
        registerAll(rv);

        MClass root = new MClass();
        root.setName("Person");
        root.createAttribute("keep", "String");
        root.createAttribute("drop", "int");

        // Only the attribute named "keep" passes the predicate; "drop" is skipped entirely.
        TreeWalker.on(root).order(o -> o.setPredicate(a -> "keep".equals(a.getName()))).walk();

        assertEquals("BEFORE_CLASS|FIELD|AFTER_CLASS|", rv.log.toString(),
                "filtered-out child must not be visited");
    }
}
