package de.spraener.nxtgen.oom.model;

import de.spraener.nxtgen.model.ModelElement;
import de.spraener.nxtgen.model.impl.ModelElementImpl;

public class MDependency extends ModelElementImpl {
    private String target = null;
    public MDependency(ModelElement me) {
        super();
        this.target = me.getProperty("target");
    }

    public String getTarget() {
        return target;
    }
}
