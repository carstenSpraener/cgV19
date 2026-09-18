package de.spraener.nxtgen.oom.model;

import de.spraener.nxtgen.model.ModelElement;
import de.spraener.nxtgen.model.Stereotype;
import de.spraener.nxtgen.model.TaggedValue;
import de.spraener.nxtgen.model.impl.ModelElementImpl;
import de.spraener.nxtgen.oom.StereotypeHelper;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.*;

/**
 * Phase 04 – MOperation/MParameter Family: fluent Consumer overloads,
 * fluent setters (setType/setParameters), defensive covariant getParent.
 */
public class TestFluentOperation {

    // =====================================================================
    // Core fluent chain — the main language feature
    // =====================================================================

    @Test
    public void coreFluentChain_returnsClassAndCreatesBothOperations() {
        OOModel model = new OOModel();
        MPackage pkg = model.createPackage("com");
        MClass mc = pkg.createMClass("C");

        MOperation deleteOp = mc.createOperation("find", op -> {
            op.setType("List");
            op.addStereotype("Op", TaggedValue.of("k", "v"));
            op.createParameter("id", p -> p.setType("Long"));
        }).createOperation("delete");

        // no-consumer createOperation returns the operation (regression)
        assertNotNull(deleteOp);
        assertEquals("delete", deleteOp.getName());
        assertEquals(2, mc.getOperations().size());
        assertEquals("find", mc.getOperations().get(0).getName());
        assertEquals("delete", mc.getOperations().get(1).getName());

        // First operation: type, stereotype, parameter
        MOperation findOp = mc.getOperations().get(0);
        assertEquals("List", findOp.getType());
        assertTrue(StereotypeHelper.hasStereotype(findOp, "Op"));
        assertEquals("v", findOp.getTaggedValue("Op", "k"));

        // Parameter
        assertEquals(1, findOp.getParameters().size());
        MParameter param = findOp.getParameters().get(0);
        assertEquals("id", param.getName());
        assertEquals("Long", param.getType());
        assertSame("param parent should be the operation", findOp, param.getParent());
        assertSame("param model should be propagated", model, param.getModel());
    }

    // =====================================================================
    // createOperation(Consumer) returns the class; no-consumer regression
    // =====================================================================

    @Test
    public void createOperationWithConsumer_returnsClass() {
        OOModel model = new OOModel();
        MPackage pkg = model.createPackage("com");
        MClass mc = pkg.createMClass("C");

        Object result = mc.createOperation("op1", op -> op.setType("String"));
        assertSame(mc, result);
    }

    @Test
    public void createOperationNoConsumer_regression_returnsOperation() {
        OOModel model = new OOModel();
        MPackage pkg = model.createPackage("com");
        MClass mc = pkg.createMClass("C");

        MOperation op = mc.createOperation("op1");
        assertNotNull(op);
        assertEquals("op1", op.getName());
    }

    // =====================================================================
    // createParameter(Consumer) returns the operation; no-consumer regression
    // =====================================================================

    @Test
    public void createParameterWithConsumer_returnsOperation() {
        OOModel model = new OOModel();
        MPackage pkg = model.createPackage("com");
        MClass mc = pkg.createMClass("C");

        MOperation op = mc.createOperation("op1");
        Object result = op.createParameter("p1", p -> p.setType("int"));
        assertSame(op, result);
    }

    @Test
    public void createParameterNoConsumer_regression_returnsParameter() {
        OOModel model = new OOModel();
        MPackage pkg = model.createPackage("com");
        MClass mc = pkg.createMClass("C");

        MOperation op = mc.createOperation("op1");
        MParameter p = op.createParameter("p1", "int");
        assertNotNull(p);
        assertEquals("p1", p.getName());
        assertEquals("int", p.getType());
    }

    // =====================================================================
    // Fluent setters — return self
    // =====================================================================

    @Test
    public void mOperation_setType_fluentReturnsSelf() {
        OOModel model = new OOModel();
        MPackage pkg = model.createPackage("com");
        MClass mc = pkg.createMClass("C");

        MOperation op = mc.createOperation("op1");
        assertSame(op, op.setType("X"));
        assertEquals("X", op.getType());
    }

    @Test
    public void mOperation_setParameters_fluentReturnsSelf() {
        OOModel model = new OOModel();
        MPackage pkg = model.createPackage("com");
        MClass mc = pkg.createMClass("C");

        MOperation op = mc.createOperation("op1");
        List<MParameter> params = new ArrayList<>();
        assertSame(op, op.setParameters(params));
    }

    @Test
    public void mParameter_setType_fluentReturnsSelf() {
        OOModel model = new OOModel();
        MPackage pkg = model.createPackage("com");
        MClass mc = pkg.createMClass("C");

        MOperation op = mc.createOperation("op1");
        MParameter p = op.createParameter("p1", "int");
        assertSame(p, p.setType("Y"));
        assertEquals("Y", p.getType());
    }

    // =====================================================================
    // Defensive getParent — foreign parent returns null, no CCE
    // =====================================================================

    @Test
    public void mOperation_getParent_foreignParentReturnsNull() {
        OOModel model = new OOModel();

        MOperation op = new MOperation(); // no-arg ctor
        op.setName("op1");
        op.setModel(model);
        op.setParent(new ModelElementImpl()); // foreign parent, not an MClass

        // Must NOT throw ClassCastException
        assertNull("covariant getParent with foreign parent should return null",
                op.getParent());
    }

    @Test
    public void mParameter_getParent_foreignParentReturnsNull() {
        OOModel model = new OOModel();

        MParameter p = new MParameter("String"); // public 1-arg ctor
        p.setName("p1");
        p.setModel(model);
        p.setParent(new ModelElementImpl()); // foreign parent, not an MOperation

        // Must NOT throw ClassCastException
        assertNull("covariant getParent with foreign parent should return null",
                p.getParent());
    }

    @Test
    public void mOperation_getParent_returnsMClassWhenParentIsMClass() {
        OOModel model = new OOModel();
        MPackage pkg = model.createPackage("com");
        MClass mc = pkg.createMClass("C");

        MOperation op = mc.createOperation("op1");
        assertSame(mc, op.getParent());
    }

    @Test
    public void mParameter_getParent_returnsMOperationWhenParentIsMOperation() {
        OOModel model = new OOModel();
        MPackage pkg = model.createPackage("com");
        MClass mc = pkg.createMClass("C");

        MOperation op = mc.createOperation("op1");
        MParameter p = op.createParameter("p1", "int");
        assertSame(op, p.getParent());
    }

    // =====================================================================
    // MOperation coverage (>= 80% LINE) — postDefinition, lazy getParameters,
    // both constructors, cloneTo
    // =====================================================================

    @Test
    public void mOperation_postDefinition_populatesGetParameters() {
        OOModel model = new OOModel();
        MPackage pkg = model.createPackage("com");
        MClass mc = new MClass();
        mc.setName("C");
        mc.setModel(model);
        mc.setParent(pkg);
        pkg.getChilds().add(mc);

        MOperation op = new MOperation(); // no-arg ctor
        op.setName("op1");
        op.setModel(model);
        op.setParent(mc);
        mc.getChilds().add(op);

        // Add a parameter as a child of the operation
        MParameter p = new MParameter("int");
        p.setName("p1");
        p.setModel(model);
        p.setParent(op);
        op.getChilds().add(p);

        // Before postDefinition, parameters field is null
        assertNull(op.parameters);

        op.postDefinition();

        // After postDefinition, getParameters() should be populated
        assertEquals(1, op.getParameters().size());
        assertSame(p, op.getParameters().get(0));
    }

    @Test
    public void mOperation_lazyGetParameters_calledTwice() {
        OOModel model = new OOModel();
        MPackage pkg = model.createPackage("com");
        MClass mc = new MClass();
        mc.setName("C");
        mc.setModel(model);
        mc.setParent(pkg);
        pkg.getChilds().add(mc);

        MOperation op = new MOperation(); // no-arg ctor
        op.setName("op1");
        op.setModel(model);
        op.setParent(mc);
        mc.getChilds().add(op);

        // First call — lazy init (no MParameter children)
        List<MParameter> first = op.getParameters();
        assertNotNull(first);
        assertEquals(0, first.size());

        // Second call — should return the same cached list
        assertSame(first, op.getParameters());
    }

    @Test
    public void mOperation_protectedConstructor_withParentAndName() {
        OOModel model = new OOModel();
        MPackage pkg = model.createPackage("com");
        MClass mc = new MClass();
        mc.setName("C");
        mc.setModel(model);
        mc.setParent(pkg);
        pkg.getChilds().add(mc);

        MOperation op = new MOperation(mc, "op1"); // protected ctor
        assertEquals("op1", op.getName());
        assertSame(mc, op.getParent());
        assertSame(model, op.getModel());
    }

    @Test
    public void mOperation_cloneTo_clonesTypeStereotypesAndParameters() {
        OOModel model = new OOModel();
        MPackage pkg = model.createPackage("com");
        MClass source = new MClass();
        source.setName("Src");
        source.setModel(model);
        source.setParent(pkg);
        pkg.getChilds().add(source);

        // Create an operation with 2 parameters and a stereotype
        MOperation op = new MOperation(source, "op1");
        source.getChilds().add(op);
        op.setType("String");
        op.addStereotype("Svc", TaggedValue.of("tier", "backend"));

        MParameter p1 = new MParameter(op, "p1", "int");
        op.getParameters().add(p1);
        op.getChilds().add(p1);

        MParameter p2 = new MParameter(op, "p2", "String");
        op.getParameters().add(p2);
        op.getChilds().add(p2);

        // Clone to a target class
        MClass target = new MClass();
        target.setName("Tgt");
        target.setModel(model);
        target.setParent(pkg);
        pkg.getChilds().add(target);

        MOperation cloned = op.cloneTo(target);

        assertEquals("op1", cloned.getName());
        assertEquals("String", cloned.getType());
        assertTrue(StereotypeHelper.hasStereotype(cloned, "Svc"));
        assertEquals("backend", cloned.getTaggedValue("Svc", "tier"));

        // Cloned operation should have 2 parameters
        assertEquals(2, cloned.getParameters().size());
        assertEquals("p1", cloned.getParameters().get(0).getName());
        assertEquals("int", cloned.getParameters().get(0).getType());
        assertEquals("p2", cloned.getParameters().get(1).getName());
        assertEquals("String", cloned.getParameters().get(1).getType());

        // Cloned operation should be in target's operations
        assertTrue(target.getOperations().contains(cloned));
    }

    // =====================================================================
    // MParameter coverage (>= 80% LINE) — getType/setType, both ctors, cloneTo
    // =====================================================================

    @Test
    public void mParameter_publicOneArgConstructor() {
        MParameter p = new MParameter("Long");
        assertEquals("Long", p.getType());
    }

    @Test
    public void mParameter_protectedThreeArgConstructor_viaCreateParameter() {
        OOModel model = new OOModel();
        MPackage pkg = model.createPackage("com");
        MClass mc = pkg.createMClass("C");

        MOperation op = mc.createOperation("op1");
        MParameter p = op.createParameter("p1", "Long");

        assertEquals("p1", p.getName());
        assertEquals("Long", p.getType());
        assertSame(op, p.getParent());
        assertSame(model, p.getModel());
    }

    @Test
    public void mParameter_cloneTo_clonesNameTypeAndStereotypes() {
        OOModel model = new OOModel();
        MPackage pkg = model.createPackage("com");
        MClass source = new MClass();
        source.setName("Src");
        source.setModel(model);
        source.setParent(pkg);
        pkg.getChilds().add(source);

        MOperation op = new MOperation(source, "op1");
        source.getChilds().add(op);

        MParameter p = new MParameter(op, "p1", "Long");
        op.getParameters().add(p);
        op.getChilds().add(p);
        p.addStereotype("In", TaggedValue.of("required", "true"));

        // Clone to a target operation
        MClass target = new MClass();
        target.setName("Tgt");
        target.setModel(model);
        target.setParent(pkg);
        pkg.getChilds().add(target);

        MOperation targetOp = new MOperation(target, "op1");
        target.getChilds().add(targetOp);

        MParameter cloned = p.cloneTo(targetOp);

        assertEquals("p1", cloned.getName());
        assertEquals("Long", cloned.getType());
        assertTrue(StereotypeHelper.hasStereotype(cloned, "In"));

        // Cloned parameter should be in target op's parameters
        assertTrue(targetOp.getParameters().contains(cloned));
    }

    @Test
    public void mParameter_noArgConstructor() {
        MParameter p = new MParameter();
        assertNull(p.getName());
    }

    // =====================================================================
    // createOperation with null modifiers — no exception
    // =====================================================================

    @Test
    public void createOperationWithNullModifiers_doesNotThrow() {
        OOModel model = new OOModel();
        MPackage pkg = model.createPackage("com");
        MClass mc = pkg.createMClass("C");

        Object result = mc.createOperation("op1", (java.util.function.Consumer<MOperation>[]) null);
        assertSame(mc, result);
        assertEquals(1, mc.getOperations().size());
    }

    // =====================================================================
    // createParameter with null modifiers — no exception
    // =====================================================================

    @Test
    public void createParameterWithNullModifiers_doesNotThrow() {
        OOModel model = new OOModel();
        MPackage pkg = model.createPackage("com");
        MClass mc = pkg.createMClass("C");

        MOperation op = mc.createOperation("op1");
        Object result = op.createParameter("p1", (java.util.function.Consumer<MParameter>[]) null);
        assertSame(op, result);
        assertEquals(1, op.getParameters().size());
    }
}
