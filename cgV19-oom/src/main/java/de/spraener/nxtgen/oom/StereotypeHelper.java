package de.spraener.nxtgen.oom;

import de.spraener.nxtgen.model.ModelElement;
import de.spraener.nxtgen.model.Stereotype;

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
}
