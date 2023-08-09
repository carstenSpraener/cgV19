package de.spraener.nxtgen.cloud.model;

import de.spraener.nxtgen.model.impl.ModelElementImpl;
import de.spraener.nxtgen.oom.model.MAbstractModelElement;

public class MPort extends MAbstractModelElement {
    public MPort(ModelElementImpl meImpl) {
        this.setName(meImpl.getName());
    }
}
