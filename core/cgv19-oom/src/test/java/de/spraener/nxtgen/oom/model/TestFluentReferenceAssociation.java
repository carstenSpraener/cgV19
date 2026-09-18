package de.spraener.nxtgen.oom.model;

import de.spraener.nxtgen.model.ModelElement;
import de.spraener.nxtgen.model.Stereotype;
import de.spraener.nxtgen.model.TaggedValue;
import de.spraener.nxtgen.model.impl.ModelElementImpl;
import de.spraener.nxtgen.oom.StereotypeHelper;
import org.junit.Test;

import static org.junit.Assert.*;

/**
 * Phase 05 – MReference/MAssociation Family: fluent Consumer overloads,
 * fluent setters (setQuantity + 7 MAssociation setters), defensive covariant getParent.
 */
public class TestFluentReferenceAssociation {

    // =====================================================================
    // Core fluent chain — createReference with Consumer
    // =====================================================================

    @Test
    public void createReferenceWithConsumer_returnsClassAndAppliesModifiers() {
        OOModel model = new OOModel();
        MPackage pkg = model.createPackage("com");
        MClass mc = pkg.createMClass("C");

        Object result = mc.createReference("boss", r -> {
            r.setQuantity("1");
            r.addStereotype("Ref", TaggedValue.of("k", "v"));
        });

        assertSame("createReference with consumer should return the class", mc, result);
        assertEquals(1, mc.getReferences().size());
        assertTrue("reference should be in childs", mc.getChilds().contains(mc.getReferences().get(0)));

        MReference ref = mc.getReferences().get(0);
        assertEquals("boss", ref.getName());
        assertSame("ref parent should be the class (typed)", mc, ref.getParent());
        assertEquals("1", ref.getQuantity());
        assertTrue(StereotypeHelper.hasStereotype(ref, "Ref"));
        assertEquals("v", ref.getTaggedValue("Ref", "k"));
    }

    @Test
    public void createReferenceNoConsumer_regression_returnsReference() {
        OOModel model = new OOModel();
        MPackage pkg = model.createPackage("com");
        MClass mc = pkg.createMClass("C");

        MReference ref = mc.createReference("x");
        assertNotNull(ref);
        assertEquals("x", ref.getName());
        assertSame("no-consumer createReference should return the reference itself", ref, mc.getReferences().get(0));
    }

    // =====================================================================
    // Core fluent chain — createAssociation with Consumer
    // =====================================================================

    @Test
    public void createAssociationWithConsumer_returnsClassAndAppliesModifiers() {
        OOModel model = new OOModel();
        MPackage pkg = model.createPackage("com");
        MClass mc = pkg.createMClass("C");

        Object result = mc.createAssociation("orders", "com.example.Order", "0..*", a -> {
            a.setComposite("true");
            a.addStereotype("Assoc", TaggedValue.of("k", "v"));
        });

        assertSame("createAssociation with consumer should return the class", mc, result);
        assertEquals(1, mc.getAssociations().size());
        assertTrue("association should be in childs", mc.getChilds().contains(mc.getAssociations().get(0)));

        MAssociation assoc = mc.getAssociations().get(0);
        assertEquals("orders", assoc.getName());
        assertEquals("com.example.Order", assoc.getType());
        assertEquals("0..*", assoc.getMultiplicity());
        assertSame("assoc parent should be the class (typed)", mc, assoc.getParent());
        assertEquals("true", assoc.getComposite());
        assertTrue(StereotypeHelper.hasStereotype(assoc, "Assoc"));
        assertEquals("v", assoc.getTaggedValue("Assoc", "k"));
    }

    // =====================================================================
    // Fluent setters — return self
    // =====================================================================

    @Test
    public void mReference_setQuantity_fluentReturnsSelf() {
        MReference ref = new MReference();
        assertSame(ref, ref.setQuantity("n"));
        assertEquals("n", ref.getQuantity());
    }

    @Test
    public void mAssociation_setAssocId_fluentReturnsSelf() {
        MAssociation a = new MAssociation();
        assertSame(a, a.setAssocId("id1"));
        assertEquals("id1", a.getAssocId());
    }

    @Test
    public void mAssociation_setOpositeAttribute_fluentReturnsSelf() {
        MAssociation a = new MAssociation();
        assertSame(a, a.setOpositeAttribute("oppAttr"));
        assertEquals("oppAttr", a.getOpositeAttribute());
    }

    @Test
    public void mAssociation_setOpositeMultiplicity_fluentReturnsSelf() {
        MAssociation a = new MAssociation();
        assertSame(a, a.setOpositeMultiplicity("1..*"));
        assertEquals("1..*", a.getOpositeMultiplicity());
    }

    @Test
    public void mAssociation_setAssociationType_fluentReturnsSelf() {
        MAssociation a = new MAssociation();
        assertSame(a, a.setAssociationType("agg"));
        assertEquals("agg", a.getAssociationType());
    }

    @Test
    public void mAssociation_setType_fluentReturnsSelf() {
        MAssociation a = new MAssociation();
        assertSame(a, a.setType("com.example.Target"));
        assertEquals("com.example.Target", a.getType());
    }

    @Test
    public void mAssociation_setMultiplicity_fluentReturnsSelf() {
        MAssociation a = new MAssociation();
        assertSame(a, a.setMultiplicity("*"));
        assertEquals("*", a.getMultiplicity());
    }

    @Test
    public void mAssociation_setComposite_fluentReturnsSelf() {
        MAssociation a = new MAssociation();
        assertSame(a, a.setComposite("false"));
        assertEquals("false", a.getComposite());
    }

    // =====================================================================
    // Defensive getParent — foreign parent returns null, no CCE
    // =====================================================================

    @Test
    public void mReference_getParent_foreignParentReturnsNull() {
        OOModel model = new OOModel();

        MReference ref = new MReference();
        ref.setName("ref1");
        ref.setModel(model);
        ref.setParent(new ModelElementImpl()); // foreign parent, not an MClass

        assertNull("covariant getParent with foreign parent should return null",
                ref.getParent());
    }

    @Test
    public void mAssociation_getParent_foreignParentReturnsNull() {
        OOModel model = new OOModel();

        MAssociation assoc = new MAssociation();
        assoc.setName("assoc1");
        assoc.setModel(model);
        assoc.setParent(new ModelElementImpl()); // foreign parent, not an MClass

        assertNull("covariant getParent with foreign parent should return null",
                assoc.getParent());
    }

    @Test
    public void mReference_getParent_returnsMClassWhenParentIsMClass() {
        OOModel model = new OOModel();
        MPackage pkg = model.createPackage("com");
        MClass mc = pkg.createMClass("C");

        MReference ref = mc.createReference("ref1");
        assertSame(mc, ref.getParent());
    }

    @Test
    public void mAssociation_getParent_returnsMClassWhenParentIsMClass() {
        OOModel model = new OOModel();
        MPackage pkg = model.createPackage("com");
        MClass mc = pkg.createMClass("C");

        MAssociation assoc = new MAssociation();
        assoc.setName("assoc1");
        assoc.setModel(model);
        assoc.setParent(mc);
        mc.getChilds().add(assoc);

        assertSame(mc, assoc.getParent());
    }

    // =====================================================================
    // MReference coverage (>= 80% LINE) — getQuantity/setQuantity, cloneTo
    // =====================================================================

    @Test
    public void mReference_getQuantity_returnsNullWhenNotSet() {
        MReference ref = new MReference();
        assertNull(ref.getQuantity());
    }

    @Test
    public void mReference_cloneTo_clonesNameQuantityStereotypesAndAddsToTarget() {
        OOModel model = new OOModel();

        MReference ref = new MReference();
        ref.setName("ref1");
        ref.setModel(model);
        ref.setQuantity("3");
        ref.addStereotype("Ref", TaggedValue.of("k", "v"));

        ModelElementImpl target = new ModelElementImpl();
        MReference cloned = ref.cloneTo(target);

        assertEquals("ref1", cloned.getName());
        assertEquals("3", cloned.getQuantity());
        assertTrue(StereotypeHelper.hasStereotype(cloned, "Ref"));
        assertEquals("v", cloned.getTaggedValue("Ref", "k"));

        // Cloned reference should be in target's childs
        assertTrue(target.getChilds().contains(cloned));
    }

    // =====================================================================
    // MAssociation coverage (>= 80% LINE) — all getters, postDefinition
    // =====================================================================

    @Test
    public void mAssociation_allGettersReturnValuesSetByFluentSetters() {
        MAssociation a = new MAssociation();
        a.setAssocId("id1")
                .setOpositeAttribute("oppAttr")
                .setOpositeMultiplicity("1..*")
                .setAssociationType("comp")
                .setType("com.example.Target")
                .setMultiplicity("*")
                .setComposite("true");

        assertEquals("id1", a.getAssocId());
        assertEquals("oppAttr", a.getOpositeAttribute());
        assertEquals("1..*", a.getOpositeMultiplicity());
        assertEquals("comp", a.getAssociationType());
        assertEquals("com.example.Target", a.getType());
        assertEquals("*", a.getMultiplicity());
        assertEquals("true", a.getComposite());
    }

    @Test
    public void mAssociation_postDefinition_setsXmiIdFromAssocId() {
        MAssociation a = new MAssociation();
        a.setAssocId("my-unique-id");

        a.postDefinition();

        assertEquals("my-unique-id", a.getXmiID());
    }

    // =====================================================================
    // createReference with null modifiers — no exception
    // =====================================================================

    @Test
    public void createReferenceWithNullModifiers_doesNotThrow() {
        OOModel model = new OOModel();
        MPackage pkg = model.createPackage("com");
        MClass mc = pkg.createMClass("C");

        Object result = mc.createReference("ref1", (java.util.function.Consumer<MReference>[]) null);
        assertSame(mc, result);
        assertEquals(1, mc.getReferences().size());
    }

    // =====================================================================
    // createAssociation with null modifiers — no exception
    // =====================================================================

    @Test
    public void createAssociationWithNullModifiers_doesNotThrow() {
        OOModel model = new OOModel();
        MPackage pkg = model.createPackage("com");
        MClass mc = pkg.createMClass("C");

        Object result = mc.createAssociation("assoc1", "com.example.Target", "*", (java.util.function.Consumer<MAssociation>[]) null);
        assertSame(mc, result);
        assertEquals(1, mc.getAssociations().size());
    }

    // =====================================================================
    // Fluent chaining on the class — sibling chaining
    // =====================================================================

    @Test
    public void createReferenceChaining_createsMultipleReferences() {
        OOModel model = new OOModel();
        MPackage pkg = model.createPackage("com");
        MClass mc = pkg.createMClass("C");

        MClass lastMc = mc.createReference("ref1", r -> r.setQuantity("1"))
                .createAssociation("assoc1", "com.example.Target", "*");

        assertSame(mc, lastMc);
        assertEquals(1, mc.getReferences().size());
        assertEquals(1, mc.getAssociations().size());
    }
}
