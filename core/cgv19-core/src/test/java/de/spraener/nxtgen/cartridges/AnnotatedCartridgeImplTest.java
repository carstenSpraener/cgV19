package de.spraener.nxtgen.cartridges;

import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.lang.reflect.Method;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import de.spraener.nxtgen.oom.model.MAttribute;
import de.spraener.nxtgen.visitor.EBeforeOrAfter;
import de.spraener.nxtgen.visitor.ElementVisitor;
import de.spraener.nxtgen.visitor.VisitorRegistry;

/**
 * Tests that {@link AnnotatedCartridgeImpl} discovers and registers {@code @ElementVisitor} methods.
 */
class AnnotatedCartridgeImplTest {

    @BeforeEach
    void resetRegistry() {
        VisitorRegistry.clear();
    }

    /** A component with two {@code @ElementVisitor} methods of different order. */
    static class TestComponent {
        public TestComponent() { }

        @ElementVisitor(operatesOn = MAttribute.class, order = 20)
        public void later(MAttribute a) { }

        @ElementVisitor(operatesOn = MAttribute.class, order = 10)
        public void earlier(MAttribute a) { }

        /** A plain method that must be ignored. */
        public void notAVisitor(MAttribute a) { }
    }

    /** A component with no visitor methods. */
    static class PlainComponent {
        public PlainComponent() { }

        public void notAVisitor(MAttribute a) { }
    }

    @Test
    void registerElementVisitorsCollectsAndRegisters() throws Exception {
        AnnotatedCartridgeImpl impl = new AnnotatedCartridgeImpl();

        Method earlier = TestComponent.class.getMethod("earlier", MAttribute.class);
        Method later   = TestComponent.class.getMethod("later", MAttribute.class);

        impl.registerElementVisitors(TestComponent.class);

        // Local copy holds exactly the two visitor methods (not the plain one).
        List<AnnotatedCartridgeImpl.ElementVisitorMethod> visitors = impl.getElementVisitors();
        assertEquals(2, visitors.size(), "only @ElementVisitor methods are collected");

        // Global registry: both registered for MAttribute/BEFORE, sorted by order (10 before 20).
        List<VisitorRegistry.Entry> found = VisitorRegistry.findElementVisitors(new MAttribute(), EBeforeOrAfter.BEFORE);
        assertEquals(2, found.size());
        // Method objects from separate getMethods()/getMethod() calls are distinct instances,
        // so compare by value (declaring class + name + parameter types), not identity.
        assertEquals(earlier, found.get(0).getMethod(), "order 10 comes first");
        assertEquals(later, found.get(1).getMethod(), "order 20 comes second");

        // The owner is a shared TestComponent instance.
        assertInstanceOf(TestComponent.class, found.get(0).getOwner());
        assertSame(found.get(0).getOwner(), found.get(1).getOwner(), "one shared owner instance");
    }

    @Test
    void registerElementVisitorsIgnoresComponentsWithoutVisitors() {
        AnnotatedCartridgeImpl impl = new AnnotatedCartridgeImpl();

        impl.registerElementVisitors(PlainComponent.class);

        assertTrue(impl.getElementVisitors().isEmpty(), "no @ElementVisitor methods → nothing collected");
        assertTrue(VisitorRegistry.findElementVisitors(new MAttribute(), EBeforeOrAfter.BEFORE).isEmpty());
    }

    @Test
    void annotationDataIsExposed() throws Exception {
        AnnotatedCartridgeImpl impl = new AnnotatedCartridgeImpl();
        impl.registerElementVisitors(TestComponent.class);

        for (AnnotatedCartridgeImpl.ElementVisitorMethod evm : impl.getElementVisitors()) {
            ElementVisitor ann = evm.getAnnotation();
            assertEquals(MAttribute.class, ann.operatesOn());
            assertSame(evm.getMethod(), evm.getMethod());
        }
    }
}
