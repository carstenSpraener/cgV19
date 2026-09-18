package de.spraener.nxtgen.oom.model;

import de.spraener.nxtgen.model.ModelElement;
import de.spraener.nxtgen.model.impl.ModelElementImpl;
import de.spraener.nxtgen.oom.ModelHelper;
import de.spraener.nxtgen.oom.StereotypeHelper;

public class MParameter extends MAbstractModelElement {
    String type;

    public MParameter(String type) {
        this.type = type;
    }

    protected MParameter(MOperation parent, String name, String type) {
       this.setParent(parent);
       this.setName(name);
       this.setType(type);
       this.setModel(parent.getModel());
    }

    protected MParameter() {}

    public String getType() {
        return type;
    }

    public MParameter setType(String type) {
        this.type = type;
        return this;
    }

    @Override
    public MOperation getParent() {
        ModelElement p = super.getParent();
        return (p instanceof MOperation) ? (MOperation) p : null;   // defensive
    }

    public MParameter cloneTo(MOperation op) {
        MParameter clone = new MParameter(op, getName(), getType());
        StereotypeHelper.cloneStereotypes(this, clone);
        ModelHelper.cloneProperties(this, clone);
        clone.type = this.type;
        clone.setName(this.getName());
        op.getParameters().add(clone);
        return clone;
    }
}
