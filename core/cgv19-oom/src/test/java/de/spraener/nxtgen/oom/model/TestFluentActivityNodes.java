package de.spraener.nxtgen.oom.model;

import de.spraener.nxtgen.model.ModelElement;
import de.spraener.nxtgen.model.TaggedValue;
import de.spraener.nxtgen.model.impl.ModelElementImpl;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.*;

/**
 * Phase 07 – Fluent Activity Nodes: fluent setters on MActivityAction,
 * MActivityDecision and MActivityControlFlow; consumer overloads for control-flow creation;
 * defensive covariant getParent on action and decision.
 */
public class TestFluentActivityNodes {

    // =====================================================================
    // Core fluent chain — createDecision with nested control flows
    // =====================================================================

    @Test
    public void coreFluentChain_returnsActivityAndWiresDecisionWithControlFlows() {
        OOModel model = new OOModel();
        MPackage pkg = model.createPackage("com");
        MClass mc = pkg.createMClass("C");

        // Create two actions to use as source and target
        MActivity act = new MActivity();
        act.setName("act1");
        act.setModel(model);
        act.setParent(mc);
        mc.getChilds().add(act);

        MActivityAction source = act.createAction("source");
        source.setMetaType("action");
        source.setXmiID("id-source");

        MActivityAction target = act.createAction("target");
        target.setMetaType("action");
        target.setXmiID("id-target");

        // Fluent chain: createDecision with consumer that creates control flows
        Object result = act.createDecision("chk", d -> {
            d.setId("d1");
            d.createOutgoingControlFlow(target, cf -> {
                cf.setGuard("x>0");
                cf.addStereotype("CF", TaggedValue.of("k", "v"));
            });
            d.createIncomingControlFlow(source, cf -> cf.setGuard("y<1"));
        });

        // createDecision(Consumer) returns the activity (assertSame)
        assertSame(act, result);

        // Decision was created and wired (getDecisions() is only populated after postDefinition)
        MActivityDecision decision = act.getChilds().stream()
                .filter(c -> c instanceof MActivityDecision)
                .map(c -> (MActivityDecision) c)
                .findFirst().orElse(null);
        assertNotNull(decision);
        assertEquals("chk", decision.getName());
        assertEquals("d1", decision.getId());

        // Decision parent is the activity (typed)
        assertSame(act, decision.getParent());

        // Both control flows are in the decision's children
        assertEquals(2, decision.getChilds().size());

        // Outgoing control flow (first created)
        MActivityControlFlow outgoing = (MActivityControlFlow) decision.getChilds().get(0);
        assertEquals("x>0", outgoing.getGuard());
        assertEquals("target", outgoing.getTarget());
        assertEquals("id-target", outgoing.getTargetID());
        assertEquals("chk", outgoing.getSource());
        assertNotNull(outgoing.getTaggedValue("CF", "k"));
        assertEquals("v", outgoing.getTaggedValue("CF", "k"));

        // Incoming control flow (second created)
        MActivityControlFlow incoming = (MActivityControlFlow) decision.getChilds().get(1);
        assertEquals("y<1", incoming.getGuard());
        assertEquals("chk", incoming.getTarget());
        assertEquals("source", incoming.getSource());
        assertEquals("id-source", incoming.getSourceID());
    }

    // =====================================================================
    // No-consumer createOutgoingControlFlow / createIncomingControlFlow — regression (return control flow)
    // =====================================================================

    @Test
    public void createOutgoingControlFlowNoConsumer_regression_returnsControlFlow() {
        OOModel model = new OOModel();
        MPackage pkg = model.createPackage("com");
        MClass mc = pkg.createMClass("C");

        MActivity act = new MActivity();
        act.setName("act1");
        act.setModel(model);
        act.setParent(mc);
        mc.getChilds().add(act);

        MActivityDecision decision = act.createDecision("d1");
        decision.setMetaType("decision");

        MActivityAction target = act.createAction("target");
        target.setMetaType("action");
        target.setXmiID("id-target");

        MActivityControlFlow cf = decision.createOutgoingControlFlow(target);
        assertNotNull(cf);
        assertEquals("target", cf.getTarget());
        assertEquals("id-target", cf.getTargetID());
    }

    @Test
    public void createIncomingControlFlowNoConsumer_regression_returnsControlFlow() {
        OOModel model = new OOModel();
        MPackage pkg = model.createPackage("com");
        MClass mc = pkg.createMClass("C");

        MActivity act = new MActivity();
        act.setName("act1");
        act.setModel(model);
        act.setParent(mc);
        mc.getChilds().add(act);

        MActivityDecision decision = act.createDecision("d1");
        decision.setMetaType("decision");

        MActivityAction source = act.createAction("source");
        source.setMetaType("action");
        source.setXmiID("id-source");

        MActivityControlFlow cf = decision.createIncomingControlFlow(source);
        assertNotNull(cf);
        assertEquals("source", cf.getSource());
        assertEquals("id-source", cf.getSourceID());
    }

    // =====================================================================
    // Consumer variants return the decision (assertSame)
    // =====================================================================

    @Test
    public void createOutgoingControlFlowWithConsumer_returnsDecision() {
        OOModel model = new OOModel();
        MPackage pkg = model.createPackage("com");
        MClass mc = pkg.createMClass("C");

        MActivity act = new MActivity();
        act.setName("act1");
        act.setModel(model);
        act.setParent(mc);
        mc.getChilds().add(act);

        MActivityDecision decision = act.createDecision("d1");
        decision.setMetaType("decision");

        MActivityAction target = act.createAction("target");
        target.setMetaType("action");
        target.setXmiID("id-target");

        Object result = decision.createOutgoingControlFlow(target, cf -> cf.setGuard("g1"));
        assertSame(decision, result);
    }

    @Test
    public void createIncomingControlFlowWithConsumer_returnsDecision() {
        OOModel model = new OOModel();
        MPackage pkg = model.createPackage("com");
        MClass mc = pkg.createMClass("C");

        MActivity act = new MActivity();
        act.setName("act1");
        act.setModel(model);
        act.setParent(mc);
        mc.getChilds().add(act);

        MActivityDecision decision = act.createDecision("d1");
        decision.setMetaType("decision");

        MActivityAction source = act.createAction("source");
        source.setMetaType("action");
        source.setXmiID("id-source");

        Object result = decision.createIncomingControlFlow(source, cf -> cf.setGuard("g2"));
        assertSame(decision, result);
    }

    // =====================================================================
    // Fluent setters — all return this (assertSame)
    // =====================================================================

    @Test
    public void mActivityAction_setId_fluentReturnsSelf() {
        MActivityAction action = new MActivityAction();
        assertSame(action, action.setId("a1"));
    }

    @Test
    public void mActivityDecision_setId_fluentReturnsSelf() {
        MActivityDecision decision = new MActivityDecision();
        assertSame(decision, decision.setId("d1"));
    }

    @Test
    public void mActivityDecision_setIncoming_fluentReturnsSelf() {
        MActivityDecision decision = new MActivityDecision();
        List<MActivityControlFlow> flows = new ArrayList<>();
        assertSame(decision, decision.setIncoming(flows));
    }

    @Test
    public void mActivityDecision_setOutgoing_fluentReturnsSelf() {
        MActivityDecision decision = new MActivityDecision();
        List<MActivityControlFlow> flows = new ArrayList<>();
        assertSame(decision, decision.setOutgoing(flows));
    }

    @Test
    public void mActivityControlFlow_setSource_fluentReturnsSelf() {
        MActivityControlFlow cf = new MActivityControlFlow();
        assertSame(cf, cf.setSource("s"));
    }

    @Test
    public void mActivityControlFlow_setSourceID_fluentReturnsSelf() {
        MActivityControlFlow cf = new MActivityControlFlow();
        assertSame(cf, cf.setSourceID("s1"));
    }

    @Test
    public void mActivityControlFlow_setTarget_fluentReturnsSelf() {
        MActivityControlFlow cf = new MActivityControlFlow();
        assertSame(cf, cf.setTarget("t"));
    }

    @Test
    public void mActivityControlFlow_setTargetID_fluentReturnsSelf() {
        MActivityControlFlow cf = new MActivityControlFlow();
        assertSame(cf, cf.setTargetID("t1"));
    }

    @Test
    public void mActivityControlFlow_setId_fluentReturnsSelf() {
        MActivityControlFlow cf = new MActivityControlFlow();
        assertSame(cf, cf.setId("cf1"));
    }

    @Test
    public void mActivityControlFlow_setGuard_fluentReturnsSelf() {
        MActivityControlFlow cf = new MActivityControlFlow();
        assertSame(cf, cf.setGuard("g1"));
    }

    // =====================================================================
    // Defensive getParent — foreign parent returns null, no CCE
    // =====================================================================

    @Test
    public void mActivityAction_getParent_foreignParentReturnsNull() {
        OOModel model = new OOModel();

        MActivityAction action = new MActivityAction();
        action.setName("a1");
        action.setModel(model);
        action.setParent(new ModelElementImpl()); // foreign parent, not an MActivity

        // Must NOT throw ClassCastException
        assertNull("covariant getParent with foreign parent should return null",
                action.getParent());
    }

    @Test
    public void mActivityAction_getParent_returnsMActivityWhenParentIsMActivity() {
        OOModel model = new OOModel();
        MPackage pkg = model.createPackage("com");
        MClass mc = pkg.createMClass("C");

        MActivity act = new MActivity();
        act.setName("act1");
        act.setModel(model);
        act.setParent(mc);
        mc.getChilds().add(act);

        MActivityAction action = new MActivityAction();
        action.setName("a1");
        action.setModel(model);
        action.setParent(act);
        act.getChilds().add(action);

        assertSame(act, action.getParent());
    }

    @Test
    public void mActivityDecision_getParent_foreignParentReturnsNull() {
        OOModel model = new OOModel();

        MActivityDecision decision = new MActivityDecision();
        decision.setName("d1");
        decision.setModel(model);
        decision.setParent(new ModelElementImpl()); // foreign parent, not an MActivity

        // Must NOT throw ClassCastException
        assertNull("covariant getParent with foreign parent should return null",
                decision.getParent());
    }

    @Test
    public void mActivityDecision_getParent_returnsMActivityWhenParentIsMActivity() {
        OOModel model = new OOModel();
        MPackage pkg = model.createPackage("com");
        MClass mc = pkg.createMClass("C");

        MActivity act = new MActivity();
        act.setName("act1");
        act.setModel(model);
        act.setParent(mc);
        mc.getChilds().add(act);

        MActivityDecision decision = new MActivityDecision();
        decision.setName("d1");
        decision.setModel(model);
        decision.setParent(act);
        act.getChilds().add(decision);

        assertSame(act, decision.getParent());
    }

    // =====================================================================
    // MActivityAction coverage (>= 80% LINE) — getId, setId, postDifinition
    // =====================================================================

    @Test
    public void mActivityAction_getId_returnsSetId() {
        MActivityAction action = new MActivityAction();
        assertNull(action.getId());

        action.setId("a1");
        assertEquals("a1", action.getId());
    }

    @Test
    public void mActivityAction_postDifinition_registersInRepository() {
        // Use a unique id to avoid collision with other tests (OOModelRepository is global)
        String uniqueId = "action-postdef-" + System.nanoTime();

        MActivityAction action = new MActivityAction();
        action.setName("a1");
        action.setId(uniqueId);

        // postDifinition (note: pre-existing typo) registers the action in OOModelRepository
        action.postDifinition();

        // Verify it was registered (using unique id)
        assertNotNull(OOModelRepository.getInstance().get(uniqueId));
        assertSame(action, OOModelRepository.getInstance().get(uniqueId));

        // Clean up to not pollute the global singleton for other tests
        OOModelRepository.getInstance().reset();
    }

    // =====================================================================
    // MActivityDecision coverage (>= 80% LINE) — postDefinition with incoming/outgoing children
    // =====================================================================

    @Test
    public void mActivityDecision_postDefinition_populatesIncomingAndOutgoing() {
        OOModel model = new OOModel();

        MActivityDecision decision = new MActivityDecision();
        decision.setName("d1");
        decision.setModel(model);

        // Create an "incoming" child element containing one control flow
        ModelElementImpl incomingContainer = new ModelElementImpl();
        incomingContainer.setMetaType("incoming");
        incomingContainer.setName("incomingContainer");

        MActivityControlFlow cf1 = new MActivityControlFlow();
        cf1.setName("cf1");
        cf1.setModel(model);
        cf1.setParent(decision);
        cf1.setMetaType("controlFlow");
        incomingContainer.getChilds().add(cf1);

        decision.getChilds().add(incomingContainer);

        // Create an "outgoing" child element containing one control flow
        ModelElementImpl outgoingContainer = new ModelElementImpl();
        outgoingContainer.setMetaType("outgoing");
        outgoingContainer.setName("outgoingContainer");

        MActivityControlFlow cf2 = new MActivityControlFlow();
        cf2.setName("cf2");
        cf2.setModel(model);
        cf2.setParent(decision);
        cf2.setMetaType("controlFlow");
        outgoingContainer.getChilds().add(cf2);

        decision.getChilds().add(outgoingContainer);

        // Before postDefinition, lists are null
        assertNull(decision.getIncoming());
        assertNull(decision.getOutgoing());

        decision.postDefinition();

        // After postDefinition, both lists should be non-null and populated
        assertNotNull(decision.getIncoming());
        assertEquals(1, decision.getIncoming().size());

        assertNotNull(decision.getOutgoing());
        assertEquals(1, decision.getOutgoing().size());

        // Do NOT assert which element the values came from — the quirk is pre-existing, out of scope
    }

    // =====================================================================
    // MActivityControlFlow coverage (>= 80% LINE) — all getters/setters, postDefinition, getAction
    // =====================================================================

    @Test
    public void mActivityControlFlow_allGettersAndSetters() {
        MActivityControlFlow cf = new MActivityControlFlow();

        // All getters return null initially
        assertNull(cf.getSource());
        assertNull(cf.getSourceID());
        assertNull(cf.getTarget());
        assertNull(cf.getTargetID());
        assertNull(cf.getId());
        assertNull(cf.getGuard());

        // Set all values (fluent)
        cf.setSource("src")
                .setSourceID("id-src")
                .setTarget("tgt")
                .setTargetID("id-tgt")
                .setId("cf1")
                .setGuard("x>0");

        // Verify all getters return the set values
        assertEquals("src", cf.getSource());
        assertEquals("id-src", cf.getSourceID());
        assertEquals("tgt", cf.getTarget());
        assertEquals("id-tgt", cf.getTargetID());
        assertEquals("cf1", cf.getId());
        assertEquals("x>0", cf.getGuard());
    }

    @Test
    public void mActivityControlFlow_postDefinition_setsGuardFromTransitOn() {
        MActivityControlFlow cf = new MActivityControlFlow();
        cf.setName("cf1");
        cf.setMetaType("controlFlow"); // prevent NPE in super.postDefinition

        cf.setProperty("transitOn", "x>10");
        assertNull(cf.getGuard()); // guard is not set yet

        cf.postDefinition();

        assertEquals("x>10", cf.getGuard());
    }

    @Test
    public void mActivityControlFlow_getAction_returnsRegisteredAction() {
        // Use a unique id to avoid collision with other tests (OOModelRepository is global)
        String uniqueId = "action-get-" + System.nanoTime();

        // Register an MActivityAction in the repository first
        MActivityAction action = new MActivityAction();
        action.setName("a1");
        action.setId(uniqueId);
        OOModelRepository.getInstance().put(uniqueId, action);

        // Create a control flow with the same sourceID
        MActivityControlFlow cf = new MActivityControlFlow();
        cf.setSourceID(uniqueId);

        // getAction should return the registered action
        assertSame(action, cf.getAction());

        // Clean up to not pollute the global singleton for other tests
        OOModelRepository.getInstance().reset();
    }

    // =====================================================================
    // Consumer overloads with null modifiers — no exception
    // =====================================================================

    @Test
    public void createOutgoingControlFlowWithNullModifiers_doesNotThrow() {
        OOModel model = new OOModel();
        MPackage pkg = model.createPackage("com");
        MClass mc = pkg.createMClass("C");

        MActivity act = new MActivity();
        act.setName("act1");
        act.setModel(model);
        act.setParent(mc);
        mc.getChilds().add(act);

        MActivityDecision decision = act.createDecision("d1");
        decision.setMetaType("decision");

        MActivityAction target = act.createAction("target");
        target.setMetaType("action");
        target.setXmiID("id-target");

        Object result = decision.createOutgoingControlFlow(target, (java.util.function.Consumer<MActivityControlFlow>[]) null);
        assertSame(decision, result);
    }

    @Test
    public void createIncomingControlFlowWithNullModifiers_doesNotThrow() {
        OOModel model = new OOModel();
        MPackage pkg = model.createPackage("com");
        MClass mc = pkg.createMClass("C");

        MActivity act = new MActivity();
        act.setName("act1");
        act.setModel(model);
        act.setParent(mc);
        mc.getChilds().add(act);

        MActivityDecision decision = act.createDecision("d1");
        decision.setMetaType("decision");

        MActivityAction source = act.createAction("source");
        source.setMetaType("action");
        source.setXmiID("id-source");

        Object result = decision.createIncomingControlFlow(source, (java.util.function.Consumer<MActivityControlFlow>[]) null);
        assertSame(decision, result);
    }

    // =====================================================================
    // Fluent chaining on the decision (sibling chaining)
    // =====================================================================

    @Test
    public void createOutgoingControlFlowFluentChaining_onDecision() {
        OOModel model = new OOModel();
        MPackage pkg = model.createPackage("com");
        MClass mc = pkg.createMClass("C");

        MActivity act = new MActivity();
        act.setName("act1");
        act.setModel(model);
        act.setParent(mc);
        mc.getChilds().add(act);

        MActivityDecision decision = act.createDecision("d1");
        decision.setMetaType("decision");

        MActivityAction target1 = act.createAction("t1");
        target1.setMetaType("action");
        target1.setXmiID("id-t1");

        MActivityAction target2 = act.createAction("t2");
        target2.setMetaType("action");
        target2.setXmiID("id-t2");

        // createOutgoingControlFlow(Consumer) returns the decision, so we can chain
        decision.createOutgoingControlFlow(target1, cf -> {})
                .createOutgoingControlFlow(target2);

        assertEquals(2, decision.getOutgoing().size());
    }
}
