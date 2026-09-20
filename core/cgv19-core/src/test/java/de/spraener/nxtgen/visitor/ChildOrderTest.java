package de.spraener.nxtgen.visitor;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.List;

import org.junit.jupiter.api.Test;

import de.spraener.nxtgen.model.ModelElement;
import de.spraener.nxtgen.model.impl.ModelElementImpl;
import de.spraener.nxtgen.model.impl.ModelElementImplBase;
import de.spraener.nxtgen.oom.model.MAttribute;
import de.spraener.nxtgen.oom.model.MOperation;

/**
 * Tests for the {@link ChildOrder} DSL.
 */
class ChildOrderTest {

    @Test
    void thenBuildsPriorityListInInsertionOrder() {
        ChildOrder co = new ChildOrder()
                .then(MAttribute.class)
                .then(MOperation.class);

        List<Class<? extends ModelElement>> priority = co.getPriority();
        assertEquals(2, priority.size());
        assertEquals(MAttribute.class, priority.get(0));
        assertEquals(MOperation.class, priority.get(1));
    }

    @Test
    void thenIsFluent() {
        ChildOrder co = new ChildOrder();
        assertSame(co, co.then(MAttribute.class));
    }

    @Test
    void defaultFilterAcceptsEverything() {
        ModelElement el = new ModelElementImpl();
        assertTrue(new ChildOrder().getFilter().test(el));
    }

    @Test
    void setPredicateFiltersChildren() {
        ChildOrder co = new ChildOrder().setPredicate(e -> "keep".equals(e.getName()));

        ModelElementImplBase keep = new ModelElementImpl();
        keep.setName("keep");
        ModelElementImplBase drop = new ModelElementImpl();
        drop.setName("drop");

        assertTrue(co.getFilter().test(keep));
        assertFalse(co.getFilter().test(drop));
    }

    @Test
    void setPredicateIsFluent() {
        ChildOrder co = new ChildOrder();
        assertSame(co, co.setPredicate(e -> true));
    }
}
