package de.spraener.nxtgen.oom.model;

import de.spraener.nxtgen.model.ModelElement;
import de.spraener.nxtgen.model.Stereotype;
import de.spraener.nxtgen.model.TaggedValue;
import de.spraener.nxtgen.model.impl.ModelElementImpl;
import de.spraener.nxtgen.oom.StereotypeHelper;
import org.junit.Test;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.junit.Assert.*;

/**
 * Phase 06 – MActivity Family: fluent Consumer overloads,
 * fluent setters (setControlFlows/setInitNode/…), defensive covariant getParent.
 */
public class TestFluentActivity {

    // =====================================================================
    // Core fluent chain — the main language feature
    // =====================================================================

    @Test
    public void coreFluentChain_returnsClassAndCreatesActivityWithChildren() {
        OOModel model = new OOModel();
        MPackage pkg = model.createPackage("com");
        MClass mc = pkg.createMClass("C");

        mc.createActivity("validate", a -> {
            a.addStereotype("Act", TaggedValue.of("k", "v"));
            a.createAction("doIt", act -> act.addStereotype("A2", TaggedValue.of("x", "y")));
            a.createDecision("chk");
        });

        // createActivity returns the class (assertSame mc)
        assertEquals(1, mc.getActivities().size());
        assertTrue(mc.getChilds().stream().anyMatch(c -> c instanceof MActivity));

        MActivity act = mc.getActivities().get(0);
        assertEquals("validate", act.getName());

        // Activity parent is the class (typed)
        assertSame(mc, act.getParent());

        // Model propagated
        assertSame(model, act.getModel());

        // Stereotype on activity
        assertTrue(StereotypeHelper.hasStereotype(act, "Act"));
        assertEquals("v", act.getTaggedValue("Act", "k"));

        // postDefinition needs an "id" property and non-null metaType on every child
        act.setProperty("id", "validate");
        for (ModelElement c : act.getChilds()) {
            MAbstractModelElement me = (MAbstractModelElement) c;
            if (me.getMetaType() == null) {
                me.setMetaType("child"); // prevent NPE in postDefinition's metaType.equals()
            }
        }
        act.postDefinition();

        // Action present in activity's children with its stereotype
        assertEquals(1, act.getActions().size());
        MActivityAction action = act.getActions().get(0);
        assertEquals("doIt", action.getName());
        assertTrue(StereotypeHelper.hasStereotype(action, "A2"));
        assertEquals("y", action.getTaggedValue("A2", "x"));

        // Decision present in activity's children
        assertEquals(1, act.getDecisions().size());
        MActivityDecision decision = act.getDecisions().get(0);
        assertEquals("chk", decision.getName());
    }

    // =====================================================================
    // No-consumer createAction / createDecision — regression (return child)
    // =====================================================================

    @Test
    public void createActionNoConsumer_regression_returnsAction() {
        OOModel model = new OOModel();
        MPackage pkg = model.createPackage("com");
        MClass mc = pkg.createMClass("C");

        MActivity act = new MActivity();
        act.setName("act1");
        act.setModel(model);
        act.setParent(mc);
        mc.getChilds().add(act);

        MActivityAction a = act.createAction("x");
        assertNotNull(a);
        assertEquals("x", a.getName());
    }

    @Test
    public void createDecisionNoConsumer_regression_returnsDecision() {
        OOModel model = new OOModel();
        MPackage pkg = model.createPackage("com");
        MClass mc = pkg.createMClass("C");

        MActivity act = new MActivity();
        act.setName("act1");
        act.setModel(model);
        act.setParent(mc);
        mc.getChilds().add(act);

        MActivityDecision d = act.createDecision("y");
        assertNotNull(d);
        assertEquals("y", d.getName());
    }

    // =====================================================================
    // Consumer createAction / createDecision — return the activity (chaining)
    // =====================================================================

    @Test
    public void createActionWithConsumer_returnsActivity() {
        OOModel model = new OOModel();
        MPackage pkg = model.createPackage("com");
        MClass mc = pkg.createMClass("C");

        MActivity act = new MActivity();
        act.setName("act1");
        act.setModel(model);
        act.setParent(mc);
        mc.getChilds().add(act);

        Object result = act.createAction("z", a -> a.addStereotype("Z", TaggedValue.of("k","v")));
        assertSame(act, result);
    }

    @Test
    public void createDecisionWithConsumer_returnsActivity() {
        OOModel model = new OOModel();
        MPackage pkg = model.createPackage("com");
        MClass mc = pkg.createMClass("C");

        MActivity act = new MActivity();
        act.setName("act1");
        act.setModel(model);
        act.setParent(mc);
        mc.getChilds().add(act);

        Object result = act.createDecision("w", d -> d.addStereotype("W", TaggedValue.of("k","v")));
        assertSame(act, result);
    }

    // =====================================================================
    // Fluent setters — all 5 return this (assertSame)
    // =====================================================================

    @Test
    public void setControlFlows_fluentReturnsSelf() {
        MActivity act = new MActivity();
        List<MActivityControlFlow> flows = new ArrayList<>();
        assertSame(act, act.setControlFlows(flows));
    }

    @Test
    public void setInitNode_fluentReturnsSelf() {
        MActivity act = new MActivity();
        assertSame(act, act.setInitNode(null));
    }

    @Test
    public void setFinalNodes_fluentReturnsSelf() {
        MActivity act = new MActivity();
        List<MActivityNode> nodes = new ArrayList<>();
        assertSame(act, act.setFinalNodes(nodes));
    }

    @Test
    public void setActions_fluentReturnsSelf() {
        MActivity act = new MActivity();
        List<MActivityAction> actions = new ArrayList<>();
        assertSame(act, act.setActions(actions));
    }

    @Test
    public void setDecisions_fluentReturnsSelf() {
        MActivity act = new MActivity();
        List<MActivityDecision> decisions = new ArrayList<>();
        assertSame(act, act.setDecisions(decisions));
    }

    // =====================================================================
    // Defensive getParent — foreign parent returns null, no CCE
    // =====================================================================

    @Test
    public void mActivity_getParent_foreignParentReturnsNull() {
        OOModel model = new OOModel();

        MActivity act = new MActivity(); // no-arg ctor
        act.setName("act1");
        act.setModel(model);
        act.setParent(new ModelElementImpl()); // foreign parent, not an MClass

        // Must NOT throw ClassCastException
        assertNull("covariant getParent with foreign parent should return null",
                act.getParent());
    }

    @Test
    public void mActivity_getParent_returnsMClassWhenParentIsMClass() {
        OOModel model = new OOModel();
        MPackage pkg = model.createPackage("com");
        MClass mc = pkg.createMClass("C");

        MActivity act = new MActivity();
        act.setName("act1");
        act.setModel(model);
        act.setParent(mc);
        mc.getChilds().add(act);

        assertSame(mc, act.getParent());
    }

    // =====================================================================
    // MActivity coverage (>= 80% LINE) — postDefinition, lazy getters
    // =====================================================================

    @Test
    public void mActivity_postDefinition_populatesAllFields() {
        OOModel model = new OOModel();
        MPackage pkg = model.createPackage("com");
        MClass mc = new MClass();
        mc.setName("C");
        mc.setModel(model);
        mc.setParent(pkg);
        pkg.getChilds().add(mc);

        MActivity act = new MActivity(); // no-arg ctor
        act.setName("act1");
        act.setModel(model);
        act.setParent(mc);
        act.setProperty("id", "act1");
        mc.getChilds().add(act);

        // Add an initNode child (MActivityNode with metaType "initNode")
        MActivityNode initN = new MActivityNode();
        initN.setName("init");
        initN.setModel(model);
        initN.setParent(act);
        initN.setMetaType("initNode");
        act.getChilds().add(initN);

        // Add a finalNode child (MActivityNode with metaType "finalNode")
        MActivityNode finalN = new MActivityNode();
        finalN.setName("final");
        finalN.setModel(model);
        finalN.setParent(act);
        finalN.setMetaType("finalNode");
        act.getChilds().add(finalN);

        // Add an action child (no-consumer createAction) — already in childs via createAction
        MActivityAction action = act.createAction("doIt");
        action.setMetaType("action"); // set non-null metaType to avoid NPE in postDefinition

        // Add a decision child (no-consumer createDecision) — already in childs via createDecision
        MActivityDecision decision = act.createDecision("chk");
        decision.setMetaType("decision"); // set non-null metaType to avoid NPE in postDefinition

        // Add a control flow child
        MActivityControlFlow cf = new MActivityControlFlow();
        cf.setName("cf1");
        cf.setModel(model);
        cf.setParent(act);
        cf.setMetaType("controlFlow"); // set non-null metaType to avoid NPE in postDefinition
        act.getChilds().add(cf);

        // Before postDefinition, fields are null
        assertNull(act.getControlFlows());
        assertNull(act.getInitNode());
        assertNull(act.getFinalNodes());
        assertNull(act.getActions());
        assertNull(act.getDecisions());

        act.postDefinition();

        // After postDefinition, all fields should be populated
        assertEquals(1, act.getControlFlows().size());
        assertSame(cf, act.getControlFlows().get(0));

        assertNotNull(act.getInitNode());
        assertSame(initN, act.getInitNode());

        assertEquals(1, act.getFinalNodes().size());
        assertSame(finalN, act.getFinalNodes().get(0));

        assertEquals(1, act.getActions().size());
        assertSame(action, act.getActions().get(0));

        assertEquals(1, act.getDecisions().size());
        assertSame(decision, act.getDecisions().get(0));
    }

    // =====================================================================
    // createActivity with null modifiers — no exception
    // =====================================================================

    @Test
    public void createActivityWithNullModifiers_doesNotThrow() {
        OOModel model = new OOModel();
        MPackage pkg = model.createPackage("com");
        MClass mc = pkg.createMClass("C");

        Object result = mc.createActivity("act1", (java.util.function.Consumer<MActivity>[]) null);
        assertSame(mc, result);
        assertEquals(1, mc.getActivities().size());
    }

    // =====================================================================
    // createAction with null modifiers — no exception
    // =====================================================================

    @Test
    public void createActionWithNullModifiers_doesNotThrow() {
        OOModel model = new OOModel();
        MPackage pkg = model.createPackage("com");
        MClass mc = pkg.createMClass("C");

        MActivity act = new MActivity();
        act.setName("act1");
        act.setModel(model);
        act.setParent(mc);
        mc.getChilds().add(act);

        Object result = act.createAction("x", (java.util.function.Consumer<MActivityAction>[]) null);
        assertSame(act, result);
    }

    // =====================================================================
    // createDecision with null modifiers — no exception
    // =====================================================================

    @Test
    public void createDecisionWithNullModifiers_doesNotThrow() {
        OOModel model = new OOModel();
        MPackage pkg = model.createPackage("com");
        MClass mc = pkg.createMClass("C");

        MActivity act = new MActivity();
        act.setName("act1");
        act.setModel(model);
        act.setParent(mc);
        mc.getChilds().add(act);

        Object result = act.createDecision("y", (java.util.function.Consumer<MActivityDecision>[]) null);
        assertSame(act, result);
    }

    // =====================================================================
    // createActivity fluent chaining on the class (sibling chaining)
    // =====================================================================

    @Test
    public void createActivityFluentChaining_onClass() {
        OOModel model = new OOModel();
        MPackage pkg = model.createPackage("com");
        MClass mc = pkg.createMClass("C");

        // createActivity returns the class, so we can chain
        mc.createActivity("act1")
                .createActivity("act2");

        assertEquals(2, mc.getActivities().size());
        assertEquals("act1", mc.getActivities().get(0).getName());
        assertEquals("act2", mc.getActivities().get(1).getName());
    }

    // =====================================================================
    // createAction fluent chaining on the activity (sibling chaining)
    // =====================================================================

    @Test
    public void createActionFluentChaining_onActivity() {
        OOModel model = new OOModel();
        MPackage pkg = model.createPackage("com");
        MClass mc = pkg.createMClass("C");

        MActivity act = new MActivity();
        act.setName("act1");
        act.setModel(model);
        act.setParent(mc);
        mc.getChilds().add(act);

        MActivityAction secondAction = act.createAction("a1", a -> {})
                .createAction("a2");

        // createAction(Consumer) returns the activity, so we can chain
        assertNotNull(secondAction);
        assertEquals("a2", secondAction.getName());
    }

    // =====================================================================
    // createDecision fluent chaining on the activity (sibling chaining)
    // =====================================================================

    @Test
    public void createDecisionFluentChaining_onActivity() {
        OOModel model = new OOModel();
        MPackage pkg = model.createPackage("com");
        MClass mc = pkg.createMClass("C");

        MActivity act = new MActivity();
        act.setName("act1");
        act.setModel(model);
        act.setParent(mc);
        mc.getChilds().add(act);

        MActivityDecision secondDecision = act.createDecision("d1", d -> {})
                .createDecision("d2");

        // createDecision(Consumer) returns the activity, so we can chain
        assertNotNull(secondDecision);
        assertEquals("d2", secondDecision.getName());
    }
}
