package de.spraener.nxtgen.oom.model;

import de.spraener.nxtgen.model.ModelElement;
import org.junit.Test;

import java.util.function.Function;
import java.util.function.Supplier;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

/**
 * Test correct connection of created child elementes to parent and model. Each child element C of a parent
 * element P should have:
 * C.getModel() == P.getModel()
 * and
 * C.getParent() = P
 */
public class TestChildToParentAndModelConnectionInCreation {

    private <T extends MAbstractModelElement> void testChildCreation(Supplier<T> uutSupplier, Function<T, ModelElement> doChildCreation) throws Exception {
        T uut = uutSupplier.get();
        OOModel model = new OOModel();
        MPackage parent = model.createPackage("test");
        uut.setModel(model);
        uut.setParent(parent);

        ModelElement child = doChildCreation.apply(uut);

        assertNotNull("Child does not have a parent", child.getParent());
        assertEquals("Child has different parent than uut", uut, child.getParent());

        assertNotNull("Child does not have a model set.", child.getModel());
        assertEquals("Child has different model than uut", model, child.getModel());

        ModelElement childFound = uut.getChilds().stream().filter(c->c==child).findFirst().orElse(null);
        assertNotNull("getChilds did not deliver the new created child.", childFound);
    }

    @Test
    public void testMPackage() throws Exception {
        testChildCreation(() -> new MPackage(), p -> p.createMClass("ChildClass"));
    }

    @Test
    public void testMClass() throws Exception {
        testChildCreation(() -> new MClass(), p -> p.createAttribute("childAttr", "String"));
        testChildCreation(() -> new MClass(), p -> p.createReference("childRef"));
        testChildCreation(() -> new MClass(), p -> p.createOperation("childOperation"));
    }

    @Test
    public void testMOperation() throws Exception {
        testChildCreation(() -> new MOperation(), op -> op.createParameter("p1", "String"));
    }

    @Test
    public void testMAbstractModelElement() throws Exception {
        testChildCreation(() -> new MOperation(), op -> op.createDependency("target"));
    }

    @Test
    public void testMActivity() throws Exception {
        testChildCreation(() -> new MActivity(), act -> act.createAction("action"));
        testChildCreation(() -> new MActivity(), act -> act.createDecision("decision"));
    }

    @Test
    public void testMActivityDecision() throws Exception {
        final MActivityAction anAction = new MActivityAction();
        testChildCreation(() -> new MActivityDecision(), act -> act.createIncomingControlFlow(anAction));
        testChildCreation(() -> new MActivityDecision(), act -> act.createOutgoingControlFlow(anAction));
    }
}