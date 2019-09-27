package de.spraener.nxtgen.oom;

import de.spraener.nxtgen.model.ModelElement;
import de.spraener.nxtgen.model.Stereotype;
import de.spraener.nxtgen.model.TaggedValue;
import de.spraener.nxtgen.model.impl.StereotypeImpl;
import de.spraener.nxtgen.model.impl.TaggedValueImpl;
import de.spraener.nxtgen.oom.model.MAttribute;
import de.spraener.nxtgen.oom.model.MClass;

public class StereotypeHelper {
    public static boolean hasStereotye(ModelElement me, String name) {
        return getStereotype(me, name) != null;
    }

    public static Stereotype getStereotype( ModelElement me, String name ) {
        for( Stereotype sType : me.getStereotypes() ) {
            if( sType.getName().equals(name) ) {
                return sType;
            }
        }
        return null;
    }

    public static void cloneStereotypes(ModelElement from, ModelElement to) {
        for( Stereotype sType : from.getStereotypes() ) {
            StereotypeImpl stFrom = (StereotypeImpl) sType;
            StereotypeImpl stTo = new StereotypeImpl(sType.getName());
            to.getStereotypes().add(stTo);
            for(TaggedValue tv : stFrom.getTaggedValues() ) {
                stTo.setTaggedValue(tv.getName(), tv.getValue());
            }
        }
    }

    public static void renameStereotype(ModelElement me, String oldName, String newName ) {
        StereotypeImpl sType = (StereotypeImpl)StereotypeHelper.getStereotype(me,oldName);
        sType.setName(newName);
    }
}
