package de.spraener.nxtgen.oom.model;

import de.spraener.nxtgen.model.ModelElement;
import de.spraener.nxtgen.model.ModelHelper;
import de.spraener.nxtgen.model.impl.ModelElementImpl;

public class MDependency extends MAbstractModelElement {
    private String target = null;

    public MDependency() {
    }

    public void postDefinition() {
        super.postDefinition();
        this.target = getProperty("target");
    }

    public String getTarget() {
        return target;
    }

    public ModelElement getTargetElement() {
        return ModelHelper.findByFQName(getModel(), target, ".");
    }
}
