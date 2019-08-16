package de.spraener.nxtgen.oom.model;

import de.spraener.nxtgen.model.ModelElement;
import de.spraener.nxtgen.model.impl.ModelElementImpl;

public class MReference extends ModelElementImpl {
    private String quantity;

    public MReference(ModelElement me){
        setName(me.getName());
        this.quantity = super.getProperty("quantity");
    }

    public String getQuantity() {
        return quantity;
    }
}
