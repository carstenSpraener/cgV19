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

    public void setSource(String source) {
        this.source = source;
    }

    public String getSourceID() {
        return sourceID;
    }

    public void setSourceID(String sourceID) {
        this.sourceID = sourceID;
    }

    public String getTarget() {
        return target;
    }

    public void setTarget(String target) {
        this.target = target;
    }

    public String getTargetID() {
        return targetID;
    }

    public void setTargetID(String targetID) {
        this.targetID = targetID;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public MActivityAction getAction() {
        return (MActivityAction) OOModelRepository.getInstance().get(this.sourceID);
    }

    public String getGuard() {
        return guard;
    }
}
