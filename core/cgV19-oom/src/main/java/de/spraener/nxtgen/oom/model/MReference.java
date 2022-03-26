package de.spraener.nxtgen.oom.model;

import de.spraener.nxtgen.model.ModelElement;
import de.spraener.nxtgen.model.impl.ModelElementImpl;
import de.spraener.nxtgen.oom.ModelHelper;
import de.spraener.nxtgen.oom.StereotypeHelper;

public class MReference extends MAbstractModelElement {
    private String quantity;

    public MReference() {
    }

    public String getQuantity() {
        return quantity;
    }

    public MReference cloneTo(ModelElement target) {
        MReference tRef = new MReference();
        tRef.setName(getName());
        ModelHelper.cloneProperties(this, tRef);
        StereotypeHelper.cloneStereotypes(this,tRef);
        tRef.quantity = this.getQuantity();
        target.getChilds().add(tRef);

        return tRef;
    }
}
