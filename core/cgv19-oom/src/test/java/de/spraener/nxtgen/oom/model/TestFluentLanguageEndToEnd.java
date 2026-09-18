package de.spraener.nxtgen.oom.model;

import de.spraener.nxtgen.model.ModelElement;
import de.spraener.nxtgen.model.TaggedValue;

import org.junit.Test;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

/**
 * Phase 09 — end-to-end proof of the fluent transformation language:
 * one expression tree from package to parameter, exercising every phase (01–08):
 * parent-returning chains, consumer configuration, upserting stereotypes,
 * typed getParent() navigation and sibling chaining at three tree levels.
 */
public class TestFluentLanguageEndToEnd {

    @Test
    public void fluentLanguageEndToEnd() {
        OOModel model = new OOModel();

        model.createPackage("com.example", pkg -> {
            pkg.createMClass("Person", c -> {
                c.addStereotype("Entity", TaggedValue.of("tableName", "person"), TaggedValue.of("isRootEntity", true));
                c.createAttribute("name", a -> a.setType("String"))
                 .createAttribute("orders", a -> { a.setType("Order"); a.setProperty("multiplicity", "0..*"); });
                c.createAssociation("orders", "com.example.Order", "0..*", a -> a.setComposite("true"));
                c.createActivity("validate", act -> {
                    final MActivityAction[] doIt = new MActivityAction[1];   // lambda capture holder
                    act.createAction("doIt", a -> { doIt[0] = a; a.setMetaType("action"); });
                    act.createDecision("chk", d -> {
                        d.setMetaType("decision");   // non-null metaType required by MActivity.postDefinition
                        if (doIt[0] != null) { d.createOutgoingControlFlow(doIt[0], cf -> cf.setGuard("name!=null")); }
                    });
                });
            })
             .createMClass("PersonResource", c -> {
                 MPackage p = c.getParent();                       // typed navigation, no cast
                 assertNotNull(p);
                 c.addStereotype("Rest", TaggedValue.of("apiUrl", "persons"), TaggedValue.of("regime", "reactive"));
                 c.createOperation("create", op -> { op.setType("Person"); op.addStereotype("Op", TaggedValue.of("verb", "POST")); })
                  .createOperation("findAll", op -> { op.setType("List"); op.createParameter("page", pa -> pa.setType("int")); });
             })
             .createMClass("PersonRepository", c -> {              // coupled sibling via the package
                 MPackage p = c.getParent();
                 assertNotNull(p);
                 c.addStereotype("Repository", TaggedValue.of("entityFQName", "com.example.Person"));
             });
        });

        // --- assertions: the model is a complete, correct projection of the expression above
        MClass person = model.findClassByName("com.example.Person");
        assertNotNull(person);
        assertEquals("com.example", person.getPackage().getFQName());
        assertTrue(person.hasStereotype("Entity"));
        assertEquals("person", person.getTaggedValue("Entity", "tableName"));
        assertEquals("true",  person.getTaggedValue("Entity", "isRootEntity"));   // boolean → string-backed
        assertEquals(2, person.getAttributes().size());
        assertEquals("List<Order>", person.getAttributes().get(1).getType());      // multiplicity branch
        assertEquals(1, person.getAssociations().size());

        MClass resource = model.findClassByName("com.example.PersonResource");
        assertNotNull(resource);
        assertEquals(2, resource.getOperations().size());
        MOperation findAll = resource.getOperations().get(1);
        assertEquals("List", findAll.getType());
        assertEquals(1, findAll.getParameters().size());
        assertEquals("int", findAll.getParameters().get(0).getType());

        // whole tree visible through the model (registration):
        List<ModelElement> all = model.getModelElements();
        assertTrue(all.contains(person));
        assertTrue(all.contains(resource));

        // sibling order = creation order (the "language" preserves structure):
        List<MClass> classes = pkgClasses(model);   // helper: top package's getClasses()
        assertEquals(Arrays.asList("Person", "PersonResource", "PersonRepository"), names(classes));

        // activity subtree (typed lists are populated by postDefinition — the loader path):
        assertEquals(1, person.getActivities().size());
        MActivity act = person.getActivities().get(0);
        act.setProperty("id", "fluent-e2e-activity-" + System.nanoTime());   // unique xmiID (global repository)
        act.postDefinition();
        assertEquals(1, act.getActions().size());
        assertEquals(1, act.getDecisions().size());
    }

    @Test
    public void stereotypeUpsertIdempotent() {
        OOModel model = new OOModel();
        model.createPackage("com.example", p -> p.createMClass("Person", c -> { }));
        MClass person = model.findClassByName("com.example.Person");
        assertNotNull(person);

        person.addStereotype("Entity", TaggedValue.of("tableName", "person"));
        person.addStereotype("Entity", TaggedValue.of("tableName", "people"));   // re-apply

        assertEquals(1, person.getStereotypes().size());                          // upsert: still exactly one
        assertEquals("people", person.getTaggedValue("Entity", "tableName"));     // value updated
    }

    private static List<MClass> pkgClasses(OOModel model) {
        return model.getChilds().stream()
                .filter(me -> me instanceof MPackage)
                .map(me -> (MPackage) me)
                .findFirst()
                .orElseThrow(() -> new AssertionError("no top-level package"))
                .getClasses();
    }

    private static List<String> names(List<MClass> classes) {
        return classes.stream().map(MClass::getName).collect(Collectors.toList());
    }
}
