package de.spraener.nxtgen.cartridge.rest;

import de.spraener.nxtgen.oom.model.MPackage;

public class RESTJavaHelper extends de.spraener.nxtgen.oom.model.JavaHelper {
    public static String toPkgName(MPackage aPackage) {
        if( aPackage==null ) {
            return "";
        }
        return aPackage.getName();
    }
}
