package de.spraener.nxtgen.oom.cartridge;

import de.spraener.nxtgen.model.ModelElement;

public class OOMHelper {
    public static String findPackage(ModelElement element) {
        String pkgName = ".de.spraener.nxtgen.model.oom";
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
