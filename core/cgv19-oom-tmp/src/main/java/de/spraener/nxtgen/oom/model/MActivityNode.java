package de.spraener.nxtgen.oom.model;

import de.spraener.nxtgen.model.ModelElement;
import de.spraener.nxtgen.model.impl.ModelElementImpl;

public class MActivityNode extends MAbstractModelElement {
    private String id;
    private String value;

    public MActivityNode() {
    }

    public String getId() {
        return id;
    }

    public String getValue() {
        return value;
    }
}
