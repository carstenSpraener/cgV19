package de.spraener.nxtgen.oom.model;

import de.spraener.nxtgen.model.ModelElement;
import de.spraener.nxtgen.model.impl.ModelElementImpl;
import de.spraener.nxtgen.model.TaggedValue;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

import static org.junit.Assert.*;

/**
 * Phase 02 – Package Level: fluent Consumer overloads and registration fix.
 */
public class TestFluentPackage {

    // =====================================================================
    // OOModel.createPackage (no consumer) — registration fix
    // =====================================================================

    @Test
    public void createPackage_registersInModelChilds() {
        OOModel model = new OOModel();
        MPackage pkg = model.createPackage("com.example");

        assertTrue("package should be in model.getChilds()",
                model.getChilds().contains(pkg));
    }

    @Test
    public void createPackage_registersInModelElements() {
        OOModel model = new OOModel();
        MPackage pkg = model.createPackage("com.example");

        assertTrue("package should be in model.getModelElements()",
                model.getModelElements().contains(pkg));
    }

    @Test
    public void createPackage_setsModel() {
        OOModel model = new OOModel();
        MPackage pkg = model.createPackage("com.example");

        assertSame("package.getModel() should be the model",
                model, pkg.getModel());
    }

    @Test
    public void createPackage_parentIsNullForTopLevel() {
        OOModel model = new OOModel();
        MPackage pkg = model.createPackage("com.example");

        assertNull("top-level package should have null parent",
                pkg.getParent());
    }

    // =====================================================================
    // OOModel.createPackage (Consumer variant) — returns model, chainable
    // =====================================================================

    @Test
    public void createPackageWithConsumer_returnsModel() {
        OOModel model = new OOModel();

        Object result = model.createPackage("com", p -> {
        });

        assertSame("createPackage with consumer should return the model",
                model, result);
    }

    @Test
    public void createPackageWithConsumer_appliesStereotype() {
        OOModel model = new OOModel();

        model.createPackage("com", p -> {
            p.addStereotype("Pkg", TaggedValue.of("k", "v"));
        });

        MPackage pkg = (MPackage) model.getChilds().get(0);
        assertEquals("consumer should have added the stereotype",
                "v", pkg.getTaggedValue("Pkg", "k"));
    }

    @Test
    public void createPackageWithConsumer_chainable() {
        OOModel model = new OOModel();

        model.createPackage("a", p -> {
            p.addStereotype("A");
        }).createPackage("b", p -> {
            p.addStereotype("B");
        });

        assertEquals("two packages should be registered", 2, model.getChilds().size());
        MPackage pkgA = (MPackage) model.getChilds().get(0);
        MPackage pkgB = (MPackage) model.getChilds().get(1);
        assertTrue("package A should have stereotype A",
                pkgA.getStereotypes().stream().anyMatch(s -> s.getName().equals("A")));
        assertTrue("package B should have stereotype B",
                pkgB.getStereotypes().stream().anyMatch(s -> s.getName().equals("B")));
    }

    @Test
    public void createPackageWithConsumer_nullModifiersDoesNotThrow() {
        OOModel model = new OOModel();

        Object result = model.createPackage("com", (Consumer<MPackage>[]) null);

        assertSame(model, result);
        assertEquals(1, model.getChilds().size());
    }

    // =====================================================================
    // MPackage.createMClass (Consumer variant) — returns package
    // =====================================================================

    @Test
    public void createMClassWithConsumer_returnsPackage() {
        OOModel model = new OOModel();
        MPackage pkg = model.createPackage("com");

        Object result = pkg.createMClass("Foo", c -> {
        });

        assertSame("createMClass with consumer should return the package",
                pkg, result);
    }

    @Test
    public void createMClassWithConsumer_classInGetClasses() {
        OOModel model = new OOModel();
        MPackage pkg = model.createPackage("com");

        pkg.createMClass("Bar", c -> {
            c.addStereotype("Entity");
        });

        List<MClass> classes = pkg.getClasses();
        assertEquals(1, classes.size());
        assertEquals("Bar", classes.get(0).getName());
    }

    @Test
    public void createMClassWithConsumer_classInChilds() {
        OOModel model = new OOModel();
        MPackage pkg = model.createPackage("com");

        pkg.createMClass("Baz", c -> {
        });

        assertTrue("class should be in pkg.getChilds()",
                pkg.getChilds().size() == 1);
        assertTrue(pkg.getChilds().get(0) instanceof MClass);
    }

    @Test
    public void createMClassWithConsumer_appliesStereotype() {
        OOModel model = new OOModel();
        MPackage pkg = model.createPackage("com");

        pkg.createMClass("Qux", c -> {
            c.addStereotype("Svc", TaggedValue.of("tier", "backend"));
        });

        MClass cls = pkg.getClasses().get(0);
        assertEquals("backend", cls.getTaggedValue("Svc", "tier"));
    }

    @Test
    public void createMClassNoConsumer_regression_returnsClass() {
        OOModel model = new OOModel();
        MPackage pkg = model.createPackage("com");

        MClass cls = pkg.createMClass("Legacy");

        assertNotNull(cls);
        assertEquals("Legacy", cls.getName());
    }

    @Test
    public void createMClassWithConsumer_nullModifiersDoesNotThrow() {
        OOModel model = new OOModel();
        MPackage pkg = model.createPackage("com");

        Object result = pkg.createMClass("NullMod", (Consumer<MClass>[]) null);

        assertSame(pkg, result);
    }

    // =====================================================================
    // MPackage.createPackage (Consumer variant) — nested package
    // =====================================================================

    @Test
    public void createNestedPackage_returnsParent() {
        OOModel model = new OOModel();
        MPackage parent = model.createPackage("com");

        Object result = parent.createPackage("sub", p -> {
        });

        assertSame("createPackage with consumer should return the parent package",
                parent, result);
    }

    @Test
    public void createNestedPackage_parentIsSet() {
        OOModel model = new OOModel();
        MPackage parent = model.createPackage("com");

        parent.createPackage("sub", p -> {
        });

        MPackage sub = (MPackage) parent.getChilds().get(0);
        assertSame("nested package's getParent() should return the parent",
                parent, sub.getParent());
    }

    @Test
    public void createNestedPackage_modelPropagated() {
        OOModel model = new OOModel();
        MPackage parent = model.createPackage("com");

        parent.createPackage("sub", p -> {
        });

        MPackage sub = (MPackage) parent.getChilds().get(0);
        assertSame("nested package should have the same model",
                model, sub.getModel());
    }

    @Test
    public void createNestedPackage_inParentChilds() {
        OOModel model = new OOModel();
        MPackage parent = model.createPackage("com");

        parent.createPackage("sub", p -> {
        });

        assertEquals(1, parent.getChilds().size());
        assertTrue(parent.getChilds().get(0) instanceof MPackage);
    }

    @Test
    public void createNestedPackageWithConsumer_appliesStereotype() {
        OOModel model = new OOModel();
        MPackage parent = model.createPackage("com");

        parent.createPackage("sub", p -> {
            p.addStereotype("Mod", TaggedValue.of("v", "1"));
        });

        MPackage sub = (MPackage) parent.getChilds().get(0);
        assertEquals("1", sub.getTaggedValue("Mod", "v"));
    }

    @Test
    public void createNestedPackageWithConsumer_nullModifiersDoesNotThrow() {
        OOModel model = new OOModel();
        MPackage parent = model.createPackage("com");

        Object result = parent.createPackage("sub", (Consumer<MPackage>[]) null);

        assertSame(parent, result);
    }

    // =====================================================================
    // Covariant getParent — defensive against foreign parents
    // =====================================================================

    @Test
    public void covariantGetParent_nestedPackageReturnsMPackage() {
        OOModel model = new OOModel();
        MPackage parent = model.createPackage("com");

        parent.createPackage("sub", p -> {
        });

        MPackage sub = (MPackage) parent.getChilds().get(0);
        assertSame(parent, sub.getParent());
    }

    @Test
    public void covariantGetParent_foreignParentReturnsNull() {
        OOModel model = new OOModel();
        MPackage pkg = model.createPackage("com");

        // Simulate a .oom-DSL top-level package whose parent is a GroovyElement (ModelElementImpl)
        ModelElementImpl foreignParent = new ModelElementImpl();
        pkg.setParent(foreignParent);

        // Must NOT throw ClassCastException; must return null
        MPackage result = pkg.getParent();
        assertNull("covariant getParent with foreign parent should return null", result);
    }

    @Test
    public void covariantGetParent_nullParentReturnsNull() {
        OOModel model = new OOModel();
        MPackage pkg = model.createPackage("com");

        // createPackage already sets parent to null
        assertNull(pkg.getParent());
    }

    // =====================================================================
    // Coverage of pre-existing methods (≥ 80% LINE)
    // =====================================================================

    @Test
    public void getClasses_returnsOnlyMClass() {
        OOModel model = new OOModel();
        MPackage pkg = model.createPackage("com");

        pkg.createMClass("A");
        pkg.createMClass("B");
        pkg.createPackage("sub", p -> {
            p.createMClass("C");
        });

        assertEquals(2, pkg.getClasses().size());
    }

    @Test
    public void getPackages_returnsOnlyMPackage() {
        OOModel model = new OOModel();
        MPackage pkg = model.createPackage("com");

        pkg.createMClass("A");
        pkg.createPackage("sub1", p -> {
        });
        pkg.createPackage("sub2", p -> {
        });

        assertEquals(2, pkg.getPackages().size());
    }

    @Test
    public void getClassesByStereotype_hit() {
        OOModel model = new OOModel();
        MPackage pkg = model.createPackage("com");

        MClass c1 = pkg.createMClass("A");
        c1.addStereotype("Entity");
        MClass c2 = pkg.createMClass("B");

        List<MClass> result = new ArrayList<>();
        pkg.getClassesByStereotype("Entity", result);

        assertEquals(1, result.size());
        assertEquals("A", result.get(0).getName());
    }

    @Test
    public void getClassesByStereotype_miss() {
        OOModel model = new OOModel();
        MPackage pkg = model.createPackage("com");

        MClass c1 = pkg.createMClass("A");
        c1.addStereotype("Entity");

        List<MClass> result = new ArrayList<>();
        pkg.getClassesByStereotype("Service", result);

        assertEquals(0, result.size());
    }

    @Test
    public void getClassesByStereotype_recursive() {
        OOModel model = new OOModel();
        MPackage pkg = model.createPackage("com");

        pkg.createMClass("Top", c -> {
            c.addStereotype("Entity");
        });

        pkg.createPackage("sub", p -> {
            p.createMClass("Nested", c -> {
                c.addStereotype("Entity");
            });
        });

        List<MClass> result = new ArrayList<>();
        pkg.getClassesByStereotype("Entity", result);

        assertEquals(2, result.size());
    }

    @Test
    public void findSubPackageByName_found() {
        OOModel model = new OOModel();
        MPackage pkg = model.createPackage("com");

        pkg.createPackage("sub", p -> {
        });

        MPackage found = pkg.findSubPackageByName("sub");
        assertNotNull(found);
        assertEquals("sub", found.getName());
    }

    @Test
    public void findSubPackageByName_notFound() {
        OOModel model = new OOModel();
        MPackage pkg = model.createPackage("com");

        pkg.createPackage("sub", p -> {
        });

        assertNull(pkg.findSubPackageByName("missing"));
    }

    @Test
    public void findSubPackageByName_nested() {
        OOModel model = new OOModel();
        MPackage pkg = model.createPackage("com");

        pkg.createPackage("level1", p1 -> {
            p1.createPackage("level2", p2 -> {
            });
        });

        MPackage found = pkg.findSubPackageByName("level2");
        assertNotNull(found);
        assertEquals("level2", found.getName());
    }

    @Test
    public void findOrCreatePackage_existing() {
        OOModel model = new OOModel();
        MPackage pkg = model.createPackage("com");

        pkg.createPackage("sub", p -> {
        });

        MPackage found = pkg.findOrCreatePackage("sub");
        assertNotNull(found);
        assertEquals(1, pkg.getPackages().size()); // only one "sub"
    }

    @Test
    public void findOrCreatePackage_new() {
        OOModel model = new OOModel();
        MPackage pkg = model.createPackage("com");

        MPackage created = pkg.findOrCreatePackage("newSub");
        assertNotNull(created);
        assertEquals(1, pkg.getPackages().size());
    }

    // =====================================================================
    // OOModel pre-existing methods coverage
    // =====================================================================

    @Test
    public void ooModelGetClassesByStereotype() {
        OOModel model = new OOModel();

        MPackage pkg = model.createPackage("com");
        MClass c1 = pkg.createMClass("A");
        c1.addStereotype("Entity");

        MClass c2 = pkg.createMClass("B");
        c2.addStereotype("Service");

        List<MClass> result = model.getClassesByStereotype("Entity");
        assertEquals(1, result.size());
        assertEquals("A", result.get(0).getName());
    }

    @Test
    public void ooModelFindClassByName_fqName() {
        OOModel model = new OOModel();

        MPackage pkg = model.createPackage("com.example");
        pkg.createMClass("Foo", c -> {
        });

        MClass found = model.findClassByName("com.example.Foo");
        assertNotNull(found);
        assertEquals("Foo", found.getName());
    }

    @Test
    public void ooModelFindClassByName_simpleName() {
        OOModel model = new OOModel();

        // createPackage puts the package in getChilds() (thanks to registration fix)
        MPackage pkg = model.createPackage("Orphan");
        // Manually add a class directly to the model's childs (edge case)
        MClass c = new MClass();
        c.setName("Bar");
        c.setModel(model);
        model.getChilds().add(c);

        MClass found = model.findClassByName("Bar");
        assertNotNull(found);
        assertEquals("Bar", found.getName());
    }

    @Test
    public void ooModelFindClassByName_notFound() {
        OOModel model = new OOModel();

        assertNull(model.findClassByName("non.existent.Class"));
    }

    @Test
    public void getFQName_topLevel() {
        OOModel model = new OOModel();
        MPackage pkg = model.createPackage("com");

        assertEquals("com", pkg.getFQName());
    }

    @Test
    public void getFQName_nested() {
        OOModel model = new OOModel();
        MPackage pkg = model.createPackage("com");

        pkg.createPackage("example", p -> {
            assertEquals("com.example", p.getFQName());
        });
    }
}
