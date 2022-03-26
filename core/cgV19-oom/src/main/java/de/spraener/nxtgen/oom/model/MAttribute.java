package de.spraener.nxtgen.oom.model;

import de.spraener.nxtgen.model.ModelElement;
import de.spraener.nxtgen.model.Stereotype;
import de.spraener.nxtgen.model.TaggedValue;
import de.spraener.nxtgen.model.impl.ModelElementImpl;
import de.spraener.nxtgen.model.impl.StereotypeImpl;
import de.spraener.nxtgen.oom.ModelHelper;
import de.spraener.nxtgen.oom.StereotypeHelper;

public class MAttribute extends MAbstractModelElement {
    private String type;

    public MAttribute(){}

    public MAttribute(String name, String type) {
        this.setName(name);
        this.type = type;
    }

    public String getType() {
        String typeModifier = super.getProperty("typeModifier");
        if( typeModifier!=null ) {
            type += typeModifier;
        }
        if( isToN() ) {
            return "List<"+type+">";
        } else {
            return type;
        }
    }

    public boolean isToN() {
        String multiplicity = getProperty("multiplicity");
        return multiplicity!=null && multiplicity.endsWith("*");
    }

    public MAttribute cloneTo(MClass target) {
        MAttribute attr = target.createAttribute(getName(),getType());
        StereotypeHelper.cloneStereotypes(this, target);
        ModelHelper.cloneProperties(this,target);
        return attr;
    }
}
