package de.spraener.nxtgen.visitor;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;
import java.lang.reflect.Method;

import org.junit.jupiter.api.Test;

import de.spraener.nxtgen.model.ModelElement;

/**
 * Tests that {@link ElementVisitor} can be read via reflection and exposes the documented defaults.
 */
class ElementVisitorTest {

    /** Sample component whose methods carry the annotation under test. */
    static class SampleComponent {

        @ElementVisitor(operatesOn = ModelElement.class)
        public void defaultsOnly(ModelElement el) { }

        @ElementVisitor(operatesOn = ModelElement.class, requiredStereotype = "Getter",
                phase = EBeforeOrAfter.AFTER, order = 42)
        public void allSet(ModelElement el) { }
    }

    @Test
    void readsDefaultValues() throws NoSuchMethodException {
        Method m = SampleComponent.class.getMethod("defaultsOnly", ModelElement.class);
        ElementVisitor ev = m.getAnnotation(ElementVisitor.class);
        assertNotNull(ev, "annotation must be present at runtime");
        assertEquals(ModelElement.class, ev.operatesOn());
        assertEquals("", ev.requiredStereotype(), "default stereotype is empty (match all)");
        assertEquals(EBeforeOrAfter.BEFORE, ev.phase(), "default phase is BEFORE");
        assertEquals(0, ev.order(), "default order is 0");
    }

    @Test
    void readsExplicitValues() throws NoSuchMethodException {
        Method m = SampleComponent.class.getMethod("allSet", ModelElement.class);
        ElementVisitor ev = m.getAnnotation(ElementVisitor.class);
        assertEquals(ModelElement.class, ev.operatesOn());
        assertEquals("Getter", ev.requiredStereotype());
        assertEquals(EBeforeOrAfter.AFTER, ev.phase());
        assertEquals(42, ev.order());
    }

    @Test
    void retentionIsRuntimeAndTargetIsMethod() {
        Retention retention = ElementVisitor.class.getAnnotation(Retention.class);
        assertNotNull(retention);
        assertEquals(java.lang.annotation.RetentionPolicy.RUNTIME, retention.value());

        Target target = ElementVisitor.class.getAnnotation(Target.class);
        assertNotNull(target);
        assertArrayEquals(new ElementType[] {ElementType.METHOD}, target.value());
    }
}
