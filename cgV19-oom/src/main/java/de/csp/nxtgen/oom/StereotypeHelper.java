package de.csp.nxtgen.oom;

import de.csp.nxtgen.model.ModelElement;
import de.csp.nxtgen.model.Stereotype;

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
