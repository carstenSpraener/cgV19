package de.spraener.nxtgen.cloud.model;

import de.spraener.nxtgen.model.impl.ModelElementImpl;
import de.spraener.nxtgen.oom.model.MAbstractModelElement;

public class MPort extends MAbstractModelElement {
    public MPort(String name) {
        this.setName(name);
    }
    public MPort(ModelElementImpl meImpl) {
        this(meImpl.getName());
    }
}
