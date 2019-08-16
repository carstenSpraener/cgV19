package de.spraener.nxtgen.oom.model;

import de.spraener.nxtgen.model.ModelElement;
import de.spraener.nxtgen.model.impl.ModelElementImpl;

public class MAttribute extends ModelElementImpl {
    private String type;

    public MAttribute(ModelElement me) {
        setName(me.getName());
        setMetaType(me.getMetaType());
        this.type = me.getProperty("type");
        this.setStereotypes(me.getStereotypes());
        this.setRelations( me.getRelations() );
        this.setProperties( me.getProperties() );
    }

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

}
