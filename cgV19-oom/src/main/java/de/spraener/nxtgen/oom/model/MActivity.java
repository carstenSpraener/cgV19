package de.spraener.nxtgen.oom.model;

import de.spraener.nxtgen.model.ModelElement;
import de.spraener.nxtgen.model.impl.ModelElementImpl;

import java.util.List;
import java.util.stream.Collectors;

public class MActivity extends ModelElementImpl {
    private List<MActivityControlFlow> controlFlows = null;
    private MActivityNode initNode;
    private List<MActivityNode> finalNodes;
    private List<MActivityAction> actions;
    private List<MActivityDecision> decisions;

    public MActivity(ModelElement meActivity) {
        super();
        setName(meActivity.getName());
        setXmiID(meActivity.getProperty("id"));
        OOModelRepository.getInstance().put(this.getXmiID(),  this);

        this.controlFlows = ((ModelElementImpl)meActivity).filterChilds( child -> {
            return child.getMetaType().equals("mControlFlow");
        }).map(meCF-> {
            MActivityControlFlow cf = new MActivityControlFlow(meCF);
            cf.setParent(this);
            return cf;
        }).collect(Collectors.toList());
        super.getChilds().addAll(this.controlFlows);

        this.initNode =  ((ModelElementImpl)meActivity).filterChilds( child -> {
            return child.getMetaType().equals("initNode");
        }).map(me-> {
            MActivityNode cf = new MActivityNode(me);
            cf.setParent(this);
            return cf;
        }).findFirst().orElse(null);
        super.getChilds().add(initNode);

        this.finalNodes =  ((ModelElementImpl)meActivity).filterChilds( child -> {
            return child.getMetaType().equals("finalNode");
        }).map(me-> {
            MActivityNode cf = new MActivityNode(me);
            cf.setParent(this);
            return cf;
        }).collect(Collectors.toList());
        super.getChilds().addAll(this.finalNodes);

        this.actions =  ((ModelElementImpl)meActivity).filterChilds( child -> {
            return child.getMetaType().equals("mAction");
        }).map(me-> {
            MActivityAction cf = new MActivityAction(me);
            cf.setParent(this);
            return cf;
        }).collect(Collectors.toList());
        super.getChilds().addAll(this.actions);

        this.decisions =  ((ModelElementImpl)meActivity).filterChilds( child -> {
            return child.getMetaType().equals("mDecision");
        }).map(me-> {
            MActivityDecision cf = new MActivityDecision(me);
            cf.setParent(this);
            return cf;
        }).collect(Collectors.toList());
        super.getChilds().addAll(this.decisions);
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
}
