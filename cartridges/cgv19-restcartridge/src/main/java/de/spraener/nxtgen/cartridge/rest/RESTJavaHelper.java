package de.spraener.nxtgen.cartridge.rest;

import de.spraener.nxtgen.oom.model.MClass;
import de.spraener.nxtgen.oom.model.MPackage;
import de.spraener.nxtgen.oom.model.OOModel;

public class RESTJavaHelper extends de.spraener.nxtgen.oom.model.JavaHelper {
    public static String toPkgName(MPackage aPackage) {
        if( aPackage==null ) {
            return "";
        }
        return aPackage.getName();
    }

    public static MClass getOriginalClass(MClass mClass ) {
        String originalClassName = mClass.getProperty("originalClass");
        if( originalClassName==null ) {
            return null;
        }
        OOModel ooModel = (OOModel) mClass.getModel();
        return ooModel.findClassByName(originalClassName);
    }

    public static String removePostfix(String postFix, String name) {
        if( name == null || postFix==null ) {
            return name;
        }
        if( !name.endsWith(postFix) ) {
            return name;
        }
        return name.substring(0, name.length()-postFix.length());
    }

    public static String toEntityName(MClass mc) {
        String dataType = mc.getTaggedValue(RESTStereotypes.LOGIC.name(), "dataType");
        if( dataType != null && !"".equals(dataType)) {
            return dataType;
        }
        return mc.getName().replace("Logic", "").replace("Base", "");
    }

    public static String toFQEntityName(MClass mc) {
        String entityClassName = toEntityName(mc);
        String pkgName = mc.getPackage().getFQName()+".model";
        return pkgName+"."+entityClassName;
    }
}
