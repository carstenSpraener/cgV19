package de.spraener.nxtgen.oom.model;

import de.spraener.nxtgen.model.ModelElement;
import de.spraener.nxtgen.model.impl.ModelElementImpl;

public class MActivityAction extends MAbstractModelElement {
    private String id;

    public MActivityAction() {
    }

    public void postDifinition() {
        OOModelHelper.mapProperties(this, getClass(), this);
        OOModelRepository.getInstance().put(this.id, this);
    }

    public String getId() {
        return id;
    }

    public MActivityAction setId(String id) { this.id = id; return this; }

    @Override
    public MActivity getParent() {
        ModelElement p = super.getParent();
        return (p instanceof MActivity) ? (MActivity) p : null;   // defensive, never a hard cast
    }
}
