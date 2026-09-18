package de.spraener.nxtgen.oom.model;

import de.spraener.nxtgen.model.ModelElement;
import de.spraener.nxtgen.model.impl.ModelElementImpl;

public class MActivityControlFlow extends MAbstractModelElement {
    private String source;
    private String sourceID;
    private String target;
    private String targetID;
    private String id;
    private String guard;

    public MActivityControlFlow() {
    }

    public void postDefinition() {
        this.guard = getProperty("transitOn");
        super.postDefinition();
    }

    public String getSource() {
        return source;
    }

    public MActivityControlFlow setSource(String source) { this.source = source; return this; }

    public String getSourceID() {
        return sourceID;
    }

    public MActivityControlFlow setSourceID(String sourceID) { this.sourceID = sourceID; return this; }

    public String getTarget() {
        return target;
    }

    public MActivityControlFlow setTarget(String target) { this.target = target; return this; }

    public String getTargetID() {
        return targetID;
    }

    public MActivityControlFlow setTargetID(String targetID) { this.targetID = targetID; return this; }

    public String getId() {
        return id;
    }

    public MActivityControlFlow setId(String id) { this.id = id; return this; }

    public MActivityControlFlow setGuard(String guard) { this.guard = guard; return this; }

    public MActivityAction getAction() {
        return (MActivityAction) OOModelRepository.getInstance().get(this.sourceID);
    }

    public String getGuard() {
        return guard;
    }
}
