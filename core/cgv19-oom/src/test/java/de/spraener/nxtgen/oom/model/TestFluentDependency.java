package de.spraener.nxtgen.oom.model;

import de.spraener.nxtgen.model.ModelElement;
import de.spraener.nxtgen.model.Stereotype;
import de.spraener.nxtgen.model.TaggedValue;
import de.spraener.nxtgen.oom.StereotypeHelper;
import org.junit.Test;

import java.util.List;

import static org.junit.Assert.*;

/**
 * Phase 08 – Fluent Dependency API:
 *   - createDependency with Consumer overloads (returns parent element)
 *   - removeObject fluent (returns this)
 *   - postDefinition coverage for MAbstractModelElement and MDependency
 */
public class TestFluentDependency {

    // =====================================================================
    // createDependency with Consumer — returns the element (chaining)
    // =====================================================================

    @Test
    public void createDependencyWithConsumer_returnsElement() {
        OOModel model = new OOModel();
        MPackage pkg = model.createPackage("com");
        MClass mc = pkg.createMClass("C");

        Object result = mc.createDependency("com.example.Target", d -> {
            d.addStereotype("Dep", TaggedValue.of("k", "v"));
        });

        assertSame(mc, result);
    }

    @Test
    public void createDependencyWithConsumer_dependencyInChilds() {
        OOModel model = new OOModel();
        MPackage pkg = model.createPackage("com");
        MClass mc = pkg.createMClass("C");

        mc.createDependency("com.example.Target", d -> {
            d.addStereotype("Dep", TaggedValue.of("k", "v"));
        });

        List<ModelElement> childs = mc.getChilds();
        assertEquals(1, childs.size());
        assertTrue(childs.get(0) instanceof MDependency);

        MDependency dep = (MDependency) childs.get(0);
        assertSame(mc, dep.getParent());
        assertSame(model, dep.getModel());
        assertEquals("com.example.Target", dep.getTarget());

        // Stereotype applied via consumer
        assertTrue(StereotypeHelper.hasStereotype(dep, "Dep"));
        assertEquals("v", dep.getTaggedValue("Dep", "k"));
    }

    // =====================================================================
    // No-consumer createDependency — regression (returns the dependency)
    // =====================================================================

    @Test
    public void createDependencyNoConsumer_regression_returnsDependency() {
        OOModel model = new OOModel();
        MPackage pkg = model.createPackage("com");
        MClass mc = pkg.createMClass("C");

        MDependency dep = mc.createDependency("com.example.Target");
        assertNotNull(dep);
        assertEquals("com.example.Target", dep.getTarget());
        assertSame(mc, dep.getParent());
        assertSame(model, dep.getModel());
    }

    // =====================================================================
    // createDependency fluent chaining on the element (sibling chaining)
    // =====================================================================

    @Test
    public void createDependencyFluentChaining_onElement() {
        OOModel model = new OOModel();
        MPackage pkg = model.createPackage("com");
        MClass mc = pkg.createMClass("C");

        // createDependency(Consumer) returns the element, so we can chain
        MDependency secondDep = mc.createDependency("com.example.Target1", d -> {})
                .createDependency("com.example.Target2");

        assertNotNull(secondDep);
        assertEquals("com.example.Target2", secondDep.getTarget());
        assertEquals(2, mc.getChilds().size());
    }

    // =====================================================================
    // createDependency with null modifiers — no exception
    // =====================================================================

    @Test
    public void createDependencyWithNullModifiers_doesNotThrow() {
        OOModel model = new OOModel();
        MPackage pkg = model.createPackage("com");
        MClass mc = pkg.createMClass("C");

        Object result = mc.createDependency("com.example.Target", (java.util.function.Consumer<MDependency>[]) null);
        assertSame(mc, result);
        assertEquals(1, mc.getChilds().size());
    }

    // =====================================================================
    // Fluent object map — putObject / removeObject chain
    // =====================================================================

    @Test
    public void fluentObjectMap_putRemove_returnsElement() {
        OOModel model = new OOModel();
        MPackage pkg = model.createPackage("com");
        MClass mc = pkg.createMClass("C");

        Object putResult = mc.putObject("k", 1);
        assertSame(mc, putResult);

        Object removeResult = mc.removeObject("k");
        assertSame(mc, removeResult);

        // After removal the key is gone
        assertNull(mc.getObject("k"));
    }

    @Test
    public void removeObjectOnFreshElement_noNPE_returnsElement() {
        OOModel model = new OOModel();
        MPackage pkg = model.createPackage("com");
        MClass mc = pkg.createMClass("C");

        // objectMap is null on a fresh element (never initialized)
        Object result = mc.removeObject("nonexistent");
        assertSame(mc, result); // no NPE
    }

    @Test
    public void removeObjectFluentChaining() {
        OOModel model = new OOModel();
        MPackage pkg = model.createPackage("com");
        MClass mc = pkg.createMClass("C");

        // Chain putObject -> removeObject
        mc.putObject("a", 1)
                .putObject("b", 2)
                .removeObject("a")
                .removeObject("b");

        assertNull(mc.getObject("a"));
        assertNull(mc.getObject("b"));
    }

    // =====================================================================
    // MAbstractModelElement coverage (>= 80% LINE) — postDefinition
    // =====================================================================

    @Test
    public void mAbstractModelElement_postDefinition_registersInRepository() {
        String uniqueId = "fluent-test-" + System.nanoTime();

        OOModel model = new OOModel();
        MPackage pkg = model.createPackage("com");
        MClass mc = new MClass();
        mc.setName("C");
        mc.setModel(model);
        mc.setParent(pkg);
        pkg.getChilds().add(mc);

        MAttribute attr = new MAttribute();
        attr.setName("attr1");
        attr.setModel(model);
        attr.setParent(mc);
        attr.setXmiID(uniqueId);
        attr.setProperty("type", "String");
        mc.getChilds().add(attr);

        // postDefinition registers the element in OOModelRepository
        attr.postDefinition();

        assertSame(attr, OOModelRepository.getInstance().get(uniqueId));
    }

    // =====================================================================
    // MDependency coverage (>= 80% LINE) — setTarget / getTarget
    // =====================================================================

    @Test
    public void mDependency_setTarget_fluentReturnsSelf() {
        MDependency dep = new MDependency();
        assertSame(dep, dep.setTarget("com.example.Target"));
    }

    @Test
    public void mDependency_getTarget_returnsSetTarget() {
        MDependency dep = new MDependency();
        dep.setTarget("com.example.Target");
        assertEquals("com.example.Target", dep.getTarget());
    }

    // =====================================================================
    // MDependency coverage (>= 80% LINE) — postDefinition with property "target"
    // =====================================================================

    @Test
    public void mDependency_postDefinition_populatesTargetFromProperty() {
        String uniqueId = "dep-test-" + System.nanoTime();

        OOModel model = new OOModel();
        MPackage pkg = model.createPackage("com");
        MClass mc = new MClass();
        mc.setName("C");
        mc.setModel(model);
        mc.setParent(pkg);
        pkg.getChilds().add(mc);

        MDependency dep = new MDependency();
        dep.setModel(model);
        dep.setParent(mc);
        dep.setXmiID(uniqueId);
        dep.setProperty("target", "com.example.Target");
        mc.getChilds().add(dep);

        // Before postDefinition, target field is null (set via setTarget would work, but we test property path)
        // Actually: the field is null because we didn't call setTarget, only setProperty
        dep.postDefinition();

        assertEquals("com.example.Target", dep.getTarget());
    }

    // =====================================================================
    // MDependency coverage (>= 80% LINE) — getTargetElement()
    // =====================================================================

    @Test
    public void mDependency_getTargetElement_returnsTargetedClass() {
        OOModel model = new OOModel();
        MPackage pkg = model.createPackage("com");
        MClass targetClass = pkg.createMClass("Target");

        // Register the target class in the repository so findByFQName can find it
        String uniqueId = "target-test-" + System.nanoTime();
        targetClass.setXmiID(uniqueId);
        targetClass.postDefinition();

        // Now create a dependency targeting the class's FQ name
        MClass source = pkg.createMClass("Source");
        String depId = "dep-target-test-" + System.nanoTime();

        MDependency dep = new MDependency();
        dep.setModel(model);
        dep.setParent(source);
        dep.setXmiID(depId);
        source.getChilds().add(dep);

        // FQ name of targetClass: package + "." + className
        String fqName = pkg.getName() + "." + targetClass.getName();
        dep.setTarget(fqName);

        ModelElement found = dep.getTargetElement();
        assertNotNull(found);
        assertSame(targetClass, found);
    }

    // =====================================================================
    // MAttribute (subclass of MAbstractModelElement) — fluent removeObject
    // =====================================================================

    @Test
    public void mAttribute_fluentRemoveObject() {
        OOModel model = new OOModel();
        MPackage pkg = model.createPackage("com");
        MClass mc = pkg.createMClass("C");

        MAttribute attr = new MAttribute();
        attr.setName("attr1");
        attr.setModel(model);
        attr.setParent(mc);
        mc.getChilds().add(attr);

        attr.putObject("key1", "value1");
        assertSame(attr, attr.removeObject("key1"));
        assertNull(attr.getObject("key1"));
    }

    // =====================================================================
    // createDependency with multiple consumers
    // =====================================================================

    @Test
    public void createDependencyWithMultipleConsumers_allApplied() {
        OOModel model = new OOModel();
        MPackage pkg = model.createPackage("com");
        MClass mc = pkg.createMClass("C");

        mc.createDependency("com.example.Target",
                d -> d.addStereotype("Dep1", TaggedValue.of("k1", "v1")),
                d -> d.addStereotype("Dep2", TaggedValue.of("k2", "v2"))
        );

        MDependency dep = (MDependency) mc.getChilds().get(0);
        assertTrue(StereotypeHelper.hasStereotype(dep, "Dep1"));
        assertEquals("v1", dep.getTaggedValue("Dep1", "k1"));
        assertTrue(StereotypeHelper.hasStereotype(dep, "Dep2"));
        assertEquals("v2", dep.getTaggedValue("Dep2", "k2"));
    }
}
