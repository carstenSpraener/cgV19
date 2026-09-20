package de.spraener.nxtgen.visitor;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.lang.reflect.Method;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import de.spraener.nxtgen.oom.model.MAttribute;
import de.spraener.nxtgen.oom.model.MClass;

/**
 * Tests for {@link VisitorRegistry}.
 */
class VisitorRegistryTest {

    @BeforeEach
    void resetRegistry() {
        VisitorRegistry.clear();
    }

    /** Dummy component whose methods carry the annotation under test. */
    static class DummyComponent {

        @ElementVisitor(operatesOn = MAttribute.class, order = 20)
        public void v2(MAttribute a) { }

        @ElementVisitor(operatesOn = MAttribute.class, order = 10)
        public void v1(MAttribute a) { }

        @ElementVisitor(operatesOn = MClass.class)
        public void v3(MClass c) { }

        @ElementVisitor(operatesOn = MAttribute.class, requiredStereotype = "Getter")
        public void v4(MAttribute a) { }
    }

    /** Subclass of MAttribute to exercise sub-type matching. */
    static class SubAttribute extends MAttribute {
        public SubAttribute() { }
    }

    @Test
    void returnsEmptyListWhenNothingRegistered() {
        MClass c = new MClass();
        assertTrue(VisitorRegistry.findElementVisitors(c, EBeforeOrAfter.BEFORE).isEmpty());
    }

    @Test
    void findsRegisteredVisitorForExactType() throws Exception {
        DummyComponent owner = new DummyComponent();
        Method v3 = DummyComponent.class.getMethod("v3", MClass.class);
        VisitorRegistry.register(owner, v3, MClass.class, EBeforeOrAfter.BEFORE, 0);

        List<VisitorRegistry.Entry> result = VisitorRegistry.findElementVisitors(new MClass(), EBeforeOrAfter.BEFORE);
        assertEquals(1, result.size());
        assertSame(v3, result.get(0).getMethod());
        assertSame(owner, result.get(0).getOwner());
    }

    @Test
    void respectsSubclassMatching() throws Exception {
        DummyComponent owner = new DummyComponent();
        Method v1 = DummyComponent.class.getMethod("v1", MAttribute.class);
        VisitorRegistry.register(owner, v1, MAttribute.class, EBeforeOrAfter.BEFORE, 0);

        List<VisitorRegistry.Entry> result = VisitorRegistry.findElementVisitors(new SubAttribute(), EBeforeOrAfter.BEFORE);
        assertEquals(1, result.size());
        assertSame(v1, result.get(0).getMethod());
    }

    @Test
    void sortsByOrderNotInsertionOrder() throws Exception {
        DummyComponent owner = new DummyComponent();
        Method v1 = DummyComponent.class.getMethod("v1", MAttribute.class);   // order 10
        Method v2 = DummyComponent.class.getMethod("v2", MAttribute.class);   // order 20
        // Register in reverse of the desired order to prove sorting (not insertion) decides sequence.
        VisitorRegistry.register(owner, v2, MAttribute.class, EBeforeOrAfter.BEFORE, 20);
        VisitorRegistry.register(owner, v1, MAttribute.class, EBeforeOrAfter.BEFORE, 10);

        List<VisitorRegistry.Entry> result = VisitorRegistry.findElementVisitors(new MAttribute(), EBeforeOrAfter.BEFORE);
        assertEquals(2, result.size());
        assertSame(v1, result.get(0).getMethod(), "lower order value comes first");
        assertSame(v2, result.get(1).getMethod());
    }

    @Test
    void phaseIsRespected() throws Exception {
        DummyComponent owner = new DummyComponent();
        Method v3 = DummyComponent.class.getMethod("v3", MClass.class);
        VisitorRegistry.register(owner, v3, MClass.class, EBeforeOrAfter.AFTER, 0);

        MClass c = new MClass();
        assertTrue(VisitorRegistry.findElementVisitors(c, EBeforeOrAfter.BEFORE).isEmpty(), "registered for AFTER only");
        assertEquals(1, VisitorRegistry.findElementVisitors(c, EBeforeOrAfter.AFTER).size());
    }

    @Test
    void requiredStereotypeFiltersElements() throws Exception {
        DummyComponent owner = new DummyComponent();
        Method v4 = DummyComponent.class.getMethod("v4", MAttribute.class);
        VisitorRegistry.register(owner, v4, MAttribute.class, EBeforeOrAfter.BEFORE, 0);

        MAttribute withStereotype = new MAttribute("a", "String");
        withStereotype.addStereotype("Getter");

        MAttribute plain = new MAttribute("b", "int");

        assertEquals(1, VisitorRegistry.findElementVisitors(withStereotype, EBeforeOrAfter.BEFORE).size(),
                "element with the required stereotype matches");
        assertTrue(VisitorRegistry.findElementVisitors(plain, EBeforeOrAfter.BEFORE).isEmpty(),
                "element without the stereotype is filtered out");
    }

    @Test
    void clearRemovesAllRegistrations() throws Exception {
        DummyComponent owner = new DummyComponent();
        Method v3 = DummyComponent.class.getMethod("v3", MClass.class);
        VisitorRegistry.register(owner, v3, MClass.class, EBeforeOrAfter.BEFORE, 0);
        assertEquals(1, VisitorRegistry.findElementVisitors(new MClass(), EBeforeOrAfter.BEFORE).size());

        VisitorRegistry.clear();
        assertTrue(VisitorRegistry.findElementVisitors(new MClass(), EBeforeOrAfter.BEFORE).isEmpty());
    }

    @Test
    void reRegistrationOfSameVisitorIsIdempotent() throws Exception {
        DummyComponent first  = new DummyComponent();   // e.g. from the first cartridge instance
        DummyComponent second = new DummyComponent();   // e.g. from a re-instantiated cartridge
        Method v3 = DummyComponent.class.getMethod("v3", MClass.class);

        VisitorRegistry.register(first, v3, MClass.class, EBeforeOrAfter.BEFORE, 0);
        VisitorRegistry.register(second, v3, MClass.class, EBeforeOrAfter.BEFORE, 0);

        List<VisitorRegistry.Entry> result = VisitorRegistry.findElementVisitors(new MClass(), EBeforeOrAfter.BEFORE);
        assertEquals(1, result.size(), "same visitor class+method must not be registered twice");
        assertSame(first, result.get(0).getOwner(), "first registration wins");
    }

    @Test
    void differentVisitorClassesAreBothKept() throws Exception {
        DummyComponent owner = new DummyComponent();
        Method v1 = DummyComponent.class.getMethod("v1", MAttribute.class);
        Method v2 = DummyComponent.class.getMethod("v2", MAttribute.class);

        VisitorRegistry.register(owner, v1, MAttribute.class, EBeforeOrAfter.BEFORE, 0);
        VisitorRegistry.register(owner, v2, MAttribute.class, EBeforeOrAfter.BEFORE, 0);
        VisitorRegistry.register(owner, v1, MAttribute.class, EBeforeOrAfter.BEFORE, 0);   // duplicate of v1

        List<VisitorRegistry.Entry> result = VisitorRegistry.findElementVisitors(new MAttribute(), EBeforeOrAfter.BEFORE);
        assertEquals(2, result.size(), "distinct methods are kept, duplicates dropped");
    }
}
