package de.spraener.nxtgen.oom.model;

import de.spraener.nxtgen.model.ModelElement;
import de.spraener.nxtgen.model.impl.ModelElementImpl;

public class MActivityAction extends ModelElementImpl {
    private String id;

    public MActivityAction( ModelElement me ) {
        setName(me.getName());
        OOModelHelper.mapProperties(this, getClass(), me);
        OOModelRepository.getInstance().put(this.id, this);
        this.setStereotypes(me.getStereotypes());
    }

    public String getId() {
        return id;
    }
}
