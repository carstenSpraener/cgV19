package de.spraener.nxtgen.symfony.php;

import de.spraener.nxtgen.cartridge.rest.RESTStereotypes;
import de.spraener.nxtgen.model.ModelElement;
import de.spraener.nxtgen.oom.StereotypeHelper;
import de.spraener.nxtgen.oom.model.MAssociation;
import de.spraener.nxtgen.oom.model.MAttribute;
import de.spraener.nxtgen.oom.model.MClass;
import de.spraener.nxtgen.oom.model.MPackage;
import de.spraener.nxtgen.symfony.SymfonyStereotypes;

import static de.spraener.nxtgen.oom.cartridge.JavaHelper.firstToUpperCase;

public class PhpCodeHelper {
    private static MPackage prjRootPackage = null;

    public static MPackage getProjectRootPackage(ModelElement e) {
        if( prjRootPackage==null ) {
            while( e != null && !isRootPackage(e) ) {
                e = e.getParent();
            }
            if( e == null ) {
                return null;
            } else {
                prjRootPackage = (MPackage) e;
            }
        }
        return prjRootPackage;
    }

    private static boolean isRootPackage(ModelElement e) {
        return e instanceof MPackage pkg && StereotypeHelper.hasStereotype(e, SymfonyStereotypes.SYMFONYAPP.getName());
    }

    public static String toNameSpace(MClass mc) {
        MPackage root = getProjectRootPackage(mc);
        MPackage cPkg = mc.getPackage();
        String path = cPkg.getFQName().substring(root.getFQName().length()+1);
        StringBuilder sb = new StringBuilder();
        sb.append("App");
        for( String pkgName : path.split("\\.")) {
            sb.append("\\");
            sb.append(firstToUpperCase(pkgName));
        }
        return sb.toString();
    }

    public static String toImportStatement(MClass superClass) {
        return "use " + toNameSpace(superClass) + "\\" + superClass.getName() + ";";
    }

    public static boolean isDifferentNamespace(MClass mc, MClass assocTarget) {
        return !toNameSpace(mc).equals(toNameSpace(assocTarget));
    }

    public static boolean isToN(MAssociation assoc) {
        return assoc.getMultiplicity().endsWith("*") || assoc.getMultiplicity().endsWith("n");
    }

    public static boolean isOneToOne(MAssociation assoc) {
        String multyEnd = assoc.getMultiplicity();
        String multyOp = assoc.getOpositeMultiplicity();
        if( multyOp==null || "".equals(multyOp)) {
            return !isToN(assoc);
        }
        return multyEnd.endsWith("1") && multyOp.endsWith("1");
    }

    public static boolean isOneToMany(MAssociation assoc) {
        return "OneToMany".equals(assoc.getAssociationType());
    }

    public static boolean isManyToOne(MAssociation assoc) {
        return "ManyToOne".equals(assoc.getAssociationType());
    }

    public static boolean isManyToMany(MAssociation assoc) {
        return "ManyToMany".equals(assoc.getAssociationType());
    }

    public static String toPhpType(MAttribute a) {
        String type = a.getType();
        if( type.contains("java.") ) {
            type = type.substring(type.lastIndexOf('.')+1);
        }
        if(
           "integer".equals(type.toLowerCase()) ||
           "long".equals(type.toLowerCase())
        ) {
            type = "int";
        }
        if( "date".equals(type.toLowerCase()) ) {
            return "DateTime";
        }
        return type;
    }

    public static String readLabel(MAttribute mAttribute) {
        String label = de.spraener.nxtgen.oom.cartridge.JavaHelper.firstToUpperCase(mAttribute.getName());
        if (StereotypeHelper.hasStereotype(mAttribute, RESTStereotypes.PERSISTENTFIELD.getName())) {
            String modelLabel = mAttribute.getTaggedValue(RESTStereotypes.PERSISTENTFIELD.getName(), "label");
            if (modelLabel != null && !"".equals(modelLabel)) {
                label = modelLabel;
            }
        }
        return label;
    }

    public static String toFormTypa(MAttribute mAttribute) {
        String type = toPhpType(mAttribute);
        switch (type) {
            case "int":
                return "IntegerType::class";
            case "DateTime":
                return "DateType::class";
            default:
                return "TextType::class";
        }
    }

    public static String toPhpOutputDir(MClass mc) {
        MPackage pkg = mc.getPackage();
        String dirName = "";
        while( !StereotypeHelper.hasStereotype(pkg, SymfonyStereotypes.SYMFONYAPP.getName())) {
            dirName = firstToUpperCase(pkg.getName())+"/"+dirName;
            pkg = (MPackage) pkg.getParent();
        }
        if(dirName.endsWith("/")) {
            dirName = dirName.substring(0, dirName.length()-1);
        }
        return dirName;
    }
}
