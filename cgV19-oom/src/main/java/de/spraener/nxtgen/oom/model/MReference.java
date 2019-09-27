package de.spraener.nxtgen.oom.model;

import de.spraener.nxtgen.model.ModelElement;
import de.spraener.nxtgen.model.impl.ModelElementImpl;
import de.spraener.nxtgen.oom.ModelHelper;
import de.spraener.nxtgen.oom.StereotypeHelper;

public class MReference extends ModelElementImpl {
    private String quantity;

    public MReference(ModelElement me){
        setName(me.getName());
        this.quantity = super.getProperty("quantity");
    }

    public String getQuantity() {
        return quantity;
    }

    public MReference cloneTo(ModelElement target) {
        MReference tRef = new MReference(target);
        tRef.setName(getName());
        ModelHelper.cloneProperties(this, target);
        StereotypeHelper.cloneStereotypes(this,target);
        tRef.quantity = this.getQuantity();

        return tRef;
    }
}
