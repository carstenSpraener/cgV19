package de.spraener.nxtgen.oom.model;

import de.spraener.nxtgen.model.ModelElement;
import de.spraener.nxtgen.model.impl.ModelElementImpl;

public class MActivityNode extends ModelElementImpl {
    private String id;
    private String value;

    public MActivityNode(ModelElement me) {
        setName(me.getName());
        OOModelHelper.mapProperties(this, getClass(), me);
        OOModelRepository.getInstance().put(id ,this);
    }

    public String getId() {
        return id;
    }

    public String getValue() {
        return value;
    }
}
