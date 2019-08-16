package de.csp.nxtgen.oom.cartridge;

import de.csp.nxtgen.model.ModelElement;

public class OOMHelper {
    public static String findPackage(ModelElement element) {
        String pkgName = ".de.csp.nxtgen.model.oom";
        /*
        while( element != null ) {
            if( "mPackage".equals(((ModelElementImpl)element).getMetaType()) ) {
                pkgName = pkgName+"."+element.getName();
                element = element.getParent();
            }
        }
         */
        return pkgName.substring(1);

    }
}
