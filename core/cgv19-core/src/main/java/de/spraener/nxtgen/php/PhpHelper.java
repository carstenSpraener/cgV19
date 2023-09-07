package de.spraener.nxtgen.php;

import de.spraener.nxtgen.model.ModelElement;
import de.spraener.nxtgen.model.Stereotype;
import de.spraener.nxtgen.model.impl.StereotypeImpl;

import java.util.function.Function;

public class PhpHelper {
    private static Function<ModelElement, String> packageNameProvider = null;

    public static String toPhpORMType(String javaType) {
        if( javaType.startsWith("VARCHAR") ) {
            return "type: \"string\", length: 255";
        }
        if( javaType.startsWith("DECIMAL") ) {
            return "type: \"decimal\"";
        }
        if( javaType.startsWith("DATE") ) {
            return "type: \"date\"";
        }
        if( javaType.startsWith("INTEGER") ) {
            return "type: \"integer\"";
        }
        if( javaType.startsWith("CHAR(1)") ) {
            return "type: \"boolean\"";
        }
        return "type: \"string\", length: 255";
    }

    public static int toPhpORMLength(String javaType) {
        return 255;
    }

    public static String toClassName(String fqName) {
        return fqName.substring(fqName.lastIndexOf('.')+1);
    }

    public static void setPackageNameProvider(Function<ModelElement, String> packageNameProvider) {
        PhpHelper.packageNameProvider = packageNameProvider;
    }

    public static String toPhpPackageName(ModelElement me) {
        if( packageNameProvider != null ) {
            return packageNameProvider.apply(me);
        } else {
            String parentName = me.getParent().getName();
            return parentName.substring(0, 1).toUpperCase() + parentName.substring(1);
        }
    }

    public static boolean isBaseType(String a) {
        if( a.startsWith("java") ) {
            return true;
        }
        return a.indexOf('.')==-1;
    }

    public static String readOutDirFromModelElement(ModelElement element, String defaultDir) {
        for(Stereotype sType : element.getStereotypes() ) {
            String dir = ((StereotypeImpl)sType).getTaggedValue("phpOutputTo");
            if( dir != null ) {
                return dir;
            }
        }
        return defaultDir;
    }

    public static <T extends ModelElement> T setOutputDirForModelElement(T me, String sTypeName, String dir) {
        me.getStereotypes().stream().filter(s -> s.getName().equals(sTypeName))
                .forEach( s -> s.setTaggedValue("phpOutputTo", dir));
        return me;
    }
}
