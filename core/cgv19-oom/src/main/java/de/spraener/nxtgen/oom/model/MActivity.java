package de.spraener.nxtgen.oom.model;

import de.spraener.nxtgen.model.ModelElement;
import de.spraener.nxtgen.model.impl.ModelElementImpl;

import java.util.List;
import java.util.stream.Collectors;

public class MActivity extends MAbstractModelElement {
    private List<MActivityControlFlow> controlFlows = null;
    private MActivityNode initNode;
    private List<MActivityNode> finalNodes;
    private List<MActivityAction> actions;
    private List<MActivityDecision> decisions;

    public MActivity() {
    }

    public void postDefinition() {
        setXmiID(this.getProperty("id"));
        OOModelRepository.getInstance().put(this.getXmiID(),  this);
        this.controlFlows = filterChilds( child -> child instanceof MActivityControlFlow )
                .map(child-> (MActivityControlFlow)child)
                .collect(Collectors.toList());

        this.initNode = ((ModelElementImpl)this).filterChilds( child -> {
            return child.getMetaType().equals("initNode");
        }).map(me->
                        (MActivityNode) me
                )
        .findFirst().orElse(null);

        this.finalNodes =  filterChilds( child -> child.getMetaType().equals("finalNode"))
                .map(me-> (MActivityNode) me)
                .collect(Collectors.toList());

        this.actions = filterChilds( child -> child instanceof MActivityAction )
                .map(child-> (MActivityAction)child)
                        .collect(Collectors.toList());
         this.decisions = filterChilds( child -> child instanceof MActivityDecision )
                 .map(child-> (MActivityDecision)child)
                 .collect(Collectors.toList());
        OOModelHelper.mapProperties(this, getClass(), this);
    }

    public List<MActivityControlFlow> getControlFlows() {
        return controlFlows;
    }

    public MActivityNode getInitNode() {
        return initNode;
    }

    public List<MActivityNode> getFinalNodes() {
        return finalNodes;
    }

    public List<MActivityAction> getActions() {
        return actions;
    }

    public List<MActivityDecision> getDecisions() {
        return decisions;
    }

    public MActivityAction createAction(String name) {
        MActivityAction action = new MActivityAction();
        action.setName(name);
        action.setModel(getModel());
        action.setParent(this);
        getChilds().add(action);

        return action;
    }

    public MActivityDecision createDecision(String name) {
        MActivityDecision decision = new MActivityDecision();
        decision.setName(name);
        decision.setModel(getModel());
        decision.setParent(this);
        getChilds().add(decision);

        return decision;
    }
}
