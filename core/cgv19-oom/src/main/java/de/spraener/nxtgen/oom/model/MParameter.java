package de.spraener.nxtgen.oom.model;

import de.spraener.nxtgen.model.ModelElement;
import de.spraener.nxtgen.model.impl.ModelElementImpl;
import de.spraener.nxtgen.oom.ModelHelper;
import de.spraener.nxtgen.oom.StereotypeHelper;

import java.util.function.Consumer;

public class MParameter extends MAbstractModelElement {
    String type;

    public MParameter(String type) {
        this.type = type;
    }

    protected MParameter(MOperation parent, String name, String type) {
       this.setParent(parent);
       this.setName(name);
       this.setType(type);
       this.setModel(parent.getModel());
    }

    protected MParameter() {}

    public static MParameter createInstance(MAbstractModelElement parent, String name, String type, Consumer<MParameter>[] modifiers) {
        return MAbstractModelElement.createMElement(parent, MParameter::new,
                p->p.setName(name),
                p->p.setType(type)
        );
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public MParameter cloneTo(MOperation op) {
        MParameter clone = new MParameter(op, getName(), getType());
        StereotypeHelper.cloneStereotypes(this, clone);
        ModelHelper.cloneProperties(this, clone);
        clone.type = this.type;
        clone.setName(this.getName());
        op.getParameters().add(clone);
        return clone;
    }
}
