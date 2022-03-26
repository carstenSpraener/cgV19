package de.spraener.nxtgen.oom.model;

import de.spraener.nxtgen.model.impl.ModelElementImpl;

public class MAbstractModelElement extends ModelElementImpl {

    @Override
    public void postDefinition() {
        OOModelHelper.mapProperties(this, this.getClass(), this);
        OOModelRepository.getInstance().put(this.getXmiID(), this);
    }
}
