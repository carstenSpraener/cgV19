package de.spraener.nxtgen.oom.model;

import de.spraener.nxtgen.model.ModelElement;
import de.spraener.nxtgen.model.Relation;
import de.spraener.nxtgen.model.Stereotype;
import de.spraener.nxtgen.model.TaggedValue;
import de.spraener.nxtgen.model.impl.ModelElementImpl;
import de.spraener.nxtgen.model.impl.RelationImpl;
import de.spraener.nxtgen.oom.StereotypeHelper;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

import static org.junit.Assert.*;

/**
 * Phase 03 – MAttribute Family: fluent Consumer overload for createAttribute,
 * setType on MAttribute, defensive covariant getParent.
 */
public class TestFluentAttribute {

    // =====================================================================
    // NEW fluent API: createAttribute with Consumer — returns the class
    // =====================================================================

    @Test
    public void createAttributeWithConsumer_returnsClass() {
        OOModel model = new OOModel();
        MPackage pkg = model.createPackage("com");
        MClass mc = pkg.createMClass("C");

        Object result = mc.createAttribute("name", a -> a.setType("String"));

        assertSame("createAttribute with consumer should return the class",
                mc, result);
    }

    @Test
    public void createAttributeWithConsumer_attributeInGetAttributes() {
        OOModel model = new OOModel();
        MPackage pkg = model.createPackage("com");
        MClass mc = pkg.createMClass("C");

        mc.createAttribute("name", a -> a.setType("String"));

        List<MAttribute> attrs = mc.getAttributes();
        assertEquals(1, attrs.size());
        assertEquals("name", attrs.get(0).getName());
    }

    @Test
    public void createAttributeWithConsumer_attributeInChilds() {
        OOModel model = new OOModel();
        MPackage pkg = model.createPackage("com");
        MClass mc = pkg.createMClass("C");

        mc.createAttribute("name", a -> a.setType("String"));

        assertTrue("attribute should be in mc.getChilds()",
                mc.getChilds().contains(mc.getAttributes().get(0)));
    }

    @Test
    public void createAttributeWithConsumer_parentIsSet() {
        OOModel model = new OOModel();
        MPackage pkg = model.createPackage("com");
        MClass mc = pkg.createMClass("C");

        mc.createAttribute("name", a -> a.setType("String"));

        MAttribute attr = mc.getAttributes().get(0);
        assertSame("attr.getParent() should be the MClass",
                mc, attr.getParent());
    }

    @Test
    public void createAttributeWithConsumer_modelIsSet() {
        OOModel model = new OOModel();
        MPackage pkg = model.createPackage("com");
        MClass mc = pkg.createMClass("C");

        mc.createAttribute("name", a -> a.setType("String"));

        MAttribute attr = mc.getAttributes().get(0);
        assertSame("attr.getModel() should be the model",
                model, attr.getModel());
    }

    @Test
    public void createAttributeWithConsumer_typeIsSet() {
        OOModel model = new OOModel();
        MPackage pkg = model.createPackage("com");
        MClass mc = pkg.createMClass("C");

        mc.createAttribute("name", a -> a.setType("String"));

        assertEquals("String", mc.getAttributes().get(0).getType());
    }

    @Test
    public void createAttributeWithConsumer_varargsTwoConsumers() {
        OOModel model = new OOModel();
        MPackage pkg = model.createPackage("com");
        MClass mc = pkg.createMClass("C");

        mc.createAttribute("name",
                a -> a.setType("String"),
                a -> a.addStereotype("ReadOnly", TaggedValue.of("locked", "true"))
        );

        MAttribute attr = mc.getAttributes().get(0);
        assertEquals("String", attr.getType());
        assertTrue(StereotypeHelper.hasStereotype(attr, "ReadOnly"));
        assertEquals("true", attr.getTaggedValue("ReadOnly", "locked"));
    }

    @Test
    public void createAttributeWithConsumer_nullModifiersDoesNotThrow() {
        OOModel model = new OOModel();
        MPackage pkg = model.createPackage("com");
        MClass mc = pkg.createMClass("C");

        Object result = mc.createAttribute("x", (Consumer<MAttribute>[]) null);

        assertSame(mc, result);
        assertEquals(1, mc.getAttributes().size());
    }

    // =====================================================================
    // Regression: no-consumer createAttribute(String, String) unchanged
    // =====================================================================

    @Test
    public void createAttributeNoConsumer_regression_returnsAttribute() {
        OOModel model = new OOModel();
        MPackage pkg = model.createPackage("com");
        MClass mc = pkg.createMClass("C");

        MAttribute attr = mc.createAttribute("x", "int");

        assertNotNull(attr);
        assertEquals("x", attr.getName());
        assertEquals("int", attr.getType());
    }

    // =====================================================================
    // MAttribute.setType fluent — returns self
    // =====================================================================

    @Test
    public void setType_fluentReturnsSelf() {
        OOModel model = new OOModel();
        MPackage pkg = model.createPackage("com");
        MClass mc = pkg.createMClass("C");

        MAttribute attr = new MAttribute("a", null);
        assertSame(attr, attr.setType("X"));
        assertEquals("X", attr.getType());
    }

    // =====================================================================
    // MAttribute.getType() — all branches (≥ 80% LINE)
    // =====================================================================

    @Test
    public void getType_plainType() {
        MAttribute attr = new MAttribute("a", "String");
        assertEquals("String", attr.getType());
    }

    @Test
    public void getType_withTypeModifier() {
        MAttribute attr = new MAttribute("a", "String");
        attr.setProperty("typeModifier", "!");
        assertEquals("String!", attr.getType());
    }

    @Test
    public void getType_withMultiplicityToN_returnsList() {
        MAttribute attr = new MAttribute("a", "String");
        attr.setProperty("multiplicity", "1..*");
        assertEquals("List<String>", attr.getType());
    }

    @Test
    public void getType_multiplicityWithoutStar_noList() {
        MAttribute attr = new MAttribute("a", "String");
        attr.setProperty("multiplicity", "1..1");
        assertEquals("String", attr.getType());
    }

    @Test
    public void isToN_true() {
        MAttribute attr = new MAttribute("a", "String");
        attr.setProperty("multiplicity", "1..*");
        assertTrue(attr.isToN());
    }

    @Test
    public void isToN_false() {
        MAttribute attr = new MAttribute("a", "String");
        attr.setProperty("multiplicity", "1..1");
        assertFalse(attr.isToN());
    }

    @Test
    public void isToN_noMultiplicity() {
        MAttribute attr = new MAttribute("a", "String");
        assertFalse(attr.isToN());
    }

    // =====================================================================
    // MAttribute(ModelElement) constructor — copies all values
    // =====================================================================

    @Test
    public void mAttributeFromModelElement_copiesAllValues() {
        ModelElementImpl me = new ModelElementImpl();
        me.setName("fromMe");
        me.setMetaType("someMetaType");
        me.setProperty("type", "Integer");
        me.addStereotype("Svc", TaggedValue.of("tier", "backend"));

        MAttribute attr = new MAttribute(me);

        assertEquals("fromMe", attr.getName());
        assertEquals("someMetaType", attr.getMetaType());
        assertEquals("Integer", attr.getType());
        assertEquals(1, attr.getStereotypes().size());
        assertEquals("Svc", attr.getStereotypes().get(0).getName());
    }

    // =====================================================================
    // MAttribute no-arg constructor — line coverage
    // =====================================================================

    @Test
    public void mAttributeNoArgConstructor() {
        MAttribute attr = new MAttribute();
        assertNull(attr.getName());
    }

    // =====================================================================
    // MAttribute.cloneTo — clones name, type, stereotypes
    // =====================================================================

    @Test
    public void cloneTo_clonesNameTypeAndStereotypes() {
        OOModel model = new OOModel();
        MPackage pkg = model.createPackage("com");
        MClass source = pkg.createMClass("Src");

        MAttribute srcAttr = source.createAttribute("id", "Long");
        srcAttr.addStereotype("Id", TaggedValue.of("auto", "true"));

        MClass target = pkg.createMClass("Tgt");
        srcAttr.cloneTo(target);

        MAttribute cloned = target.getAttributes().get(0);
        assertEquals("id", cloned.getName());
        assertEquals("Long", cloned.getType());
        assertTrue(StereotypeHelper.hasStereotype(cloned, "Id"));
    }

    // =====================================================================
    // MAttribute.getParent() — defensive covariant
    // =====================================================================

    @Test
    public void getParent_returnsMClassWhenParentIsMClass() {
        OOModel model = new OOModel();
        MPackage pkg = model.createPackage("com");
        MClass mc = pkg.createMClass("C");

        mc.createAttribute("name", a -> a.setType("String"));
        MAttribute attr = mc.getAttributes().get(0);

        assertSame(mc, attr.getParent());
    }

    @Test
    public void getParent_foreignParentReturnsNull() {
        OOModel model = new OOModel();
        MPackage pkg = model.createPackage("com");
        MClass mc = pkg.createMClass("C");

        MAttribute attr = new MAttribute("a", "String");
        attr.setModel(model);
        attr.setParent(new ModelElementImpl()); // foreign parent, not an MClass

        // Must NOT throw ClassCastException
        assertNull("covariant getParent with foreign parent should return null",
                attr.getParent());
    }

    // =====================================================================
    // MClass — all lazy getters called (≥ 80% LINE)
    // =====================================================================

    @Test
    public void mClassAllLazyGetters() {
        OOModel model = new OOModel();
        MPackage pkg = model.createPackage("com");
        MClass mc = new MClass();
        mc.setName("AllGetters");
        mc.setModel(model);
        mc.setParent(pkg);
        pkg.getChilds().add(mc);

        // Each lazy getter should return an empty list (no children of that type)
        assertNotNull(mc.getAttributes());
        assertNotNull(mc.getReferences());
        assertNotNull(mc.getOperations());
        assertNotNull(mc.getActivities());
        assertNotNull(mc.getAssociations());
        assertNotNull(mc.getUsages());
        assertNotNull(mc.getDependencies());

        assertEquals(0, mc.getAttributes().size());
        assertEquals(0, mc.getReferences().size());
        assertEquals(0, mc.getOperations().size());
        assertEquals(0, mc.getActivities().size());
        assertEquals(0, mc.getAssociations().size());
        assertEquals(0, mc.getUsages().size());
        assertEquals(0, mc.getDependencies().size());
    }

    // =====================================================================
    // MClass.hasStereotype — hit and miss
    // =====================================================================

    @Test
    public void hasStereotype_hit() {
        OOModel model = new OOModel();
        MPackage pkg = model.createPackage("com");
        MClass mc = pkg.createMClass("C");

        mc.addStereotype("Entity", TaggedValue.of("k", "v"));
        assertTrue(mc.hasStereotype("Entity"));
    }

    @Test
    public void hasStereotype_miss() {
        OOModel model = new OOModel();
        MPackage pkg = model.createPackage("com");
        MClass mc = pkg.createMClass("C");

        assertFalse(mc.hasStereotype("NonExistent"));
    }

    // =====================================================================
    // MClass.getFQName()
    // =====================================================================

    @Test
    public void getFQName() {
        OOModel model = new OOModel();
        MPackage pkg = model.createPackage("com");
        MClass mc = pkg.createMClass("C");

        assertEquals("com.C", mc.getFQName());
    }

    // =====================================================================
    // MClass.postDefinition() — with extends relation and without
    // =====================================================================

    @Test
    public void postDefinition_withExtendsRelation() {
        OOModel model = new OOModel();
        MPackage pkg = model.createPackage("com");
        MClass mc = new MClass();
        mc.setName("Child");
        mc.setModel(model);
        mc.setParent(pkg);
        pkg.getChilds().add(mc);

        RelationImpl r = new RelationImpl();
        r.setType("extends");
        r.setTargetType("com.example.Base");
        mc.addRelations(r);

        mc.postDefinition();

        assertNotNull(mc.getInheritsFrom());
        assertEquals("com.example.Base", mc.getInheritsFrom().getFullQualifiedClassName());
    }

    @Test
    public void postDefinition_noExtendsRelation() {
        OOModel model = new OOModel();
        MPackage pkg = model.createPackage("com");
        MClass mc = new MClass();
        mc.setName("Standalone");
        mc.setModel(model);
        mc.setParent(pkg);
        pkg.getChilds().add(mc);

        // Add a non-extends relation
        RelationImpl r = new RelationImpl();
        r.setType("depends");
        mc.addRelations(r);

        mc.postDefinition();

        assertNull(mc.getInheritsFrom());
    }

    // =====================================================================
    // MClass.cloneTo — with attributes, references, inheritsFrom
    // =====================================================================

    @Test
    public void cloneTo_clonesAttributesReferencesAndInheritsFrom() {
        OOModel model = new OOModel();
        MPackage pkg = model.createPackage("com");
        MClass source = new MClass();
        source.setName("Src");
        source.setModel(model);
        source.setParent(pkg);
        pkg.getChilds().add(source);

        // Add 2 attributes
        source.createAttribute("a1", "String");
        source.createAttribute("a2", "int");

        // Add 1 reference
        source.createReference("ref1");

        // Set inheritsFrom
        source.setInheritsFrom(new MClassRef("com.example.Base"));

        // Add a stereotype
        source.addStereotype("Entity");

        MPackage targetPkg = model.createPackage("target");
        MClass cloned = source.cloneTo(targetPkg, "Copy");

        assertEquals(2, cloned.getAttributes().size());
        assertEquals("a1", cloned.getAttributes().get(0).getName());
        assertEquals("String", cloned.getAttributes().get(0).getType());

        assertEquals(1, cloned.getReferences().size());
        assertEquals("ref1", cloned.getReferences().get(0).getName());

        assertNotNull(cloned.getInheritsFrom());
        assertEquals("com.example.Base", cloned.getInheritsFrom().getFullQualifiedClassName());

        assertTrue(StereotypeHelper.hasStereotype(cloned, "Entity"));
    }

    // =====================================================================
    // MClass.setOperations / getInheritsFrom / setInheritsFrom
    // =====================================================================

    @Test
    public void setOperations_andGetInheritsFrom() {
        OOModel model = new OOModel();
        MPackage pkg = model.createPackage("com");
        MClass mc = new MClass();
        mc.setName("C");
        mc.setModel(model);
        mc.setParent(pkg);
        pkg.getChilds().add(mc);

        List<MOperation> ops = new ArrayList<>();
        MOperation op = new MOperation();
        op.setName("doSomething");
        ops.add(op);
        mc.setOperations(ops);

        assertEquals(1, mc.getOperations().size());
        assertEquals("doSomething", mc.getOperations().get(0).getName());

        MClassRef ref = new MClassRef("com.example.Base");
        mc.setInheritsFrom(ref);
        assertSame(ref, mc.getInheritsFrom());
    }

    // =====================================================================
    // Regression: createOperation and createReference called once each
    // =====================================================================

    @Test
    public void regression_createOperation() {
        OOModel model = new OOModel();
        MPackage pkg = model.createPackage("com");
        MClass mc = pkg.createMClass("C");

        MOperation op = mc.createOperation("doIt");
        assertNotNull(op);
        assertEquals("doIt", op.getName());
    }

    @Test
    public void regression_createReference() {
        OOModel model = new OOModel();
        MPackage pkg = model.createPackage("com");
        MClass mc = pkg.createMClass("C");

        MReference ref = mc.createReference("myRef");
        assertNotNull(ref);
        assertEquals("myRef", ref.getName());
    }

    // =====================================================================
    // MClass no-arg constructor — line coverage
    // =====================================================================

    @Test
    public void mClassNoArgConstructor() {
        MClass mc = new MClass();
        assertNull(mc.getName());
    }

    // =====================================================================
    // MClass.getPackage() — line coverage
    // =====================================================================

    @Test
    public void getPackage_returnsParent() {
        OOModel model = new OOModel();
        MPackage pkg = model.createPackage("com");
        MClass mc = pkg.createMClass("C");

        assertSame(pkg, mc.getPackage());
    }
}
