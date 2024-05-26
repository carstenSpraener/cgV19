package de.spraener.nxtgen.laravel;

import de.spraener.nxtgen.cartridge.rest.RESTStereotypes;
import de.spraener.nxtgen.model.Model;
import de.spraener.nxtgen.model.ModelElement;
import de.spraener.nxtgen.oom.StereotypeHelper;
import de.spraener.nxtgen.oom.cartridge.JavaHelper;
import de.spraener.nxtgen.oom.model.*;
import de.spraener.nxtgen.symfony.SymfonyStereotypes;

public class LaravelHelper {

    public static String createPhpPath(MClass entity) {
        return toPhpPath(entity, "/");
    }

    public static String toNameSpace(MClass mc) {
        return toPhpPath(mc, "\\");
    }

    public static String toPhpPath(MClass entity, String delimiter) {
        MPackage pkg = entity.getPackage();
        String path = "";
        while( !StereotypeHelper.hasStereotype(pkg, LaravelStereotypes.LARAVELAPPLICATION.getName()) ) {
            String namespace = readNamespace(pkg);
            if( !"".equals(path)) {
                path = delimiter+path;
            }
            path = namespace+path;
            if( !(pkg.getParent() instanceof MPackage) || pkg.getParent()==null ) {
                break;
            }
            pkg = (MPackage) pkg.getParent();
        }
        if( StereotypeHelper.hasStereotype(pkg, LaravelStereotypes.LARAVELAPPLICATION.getName()) ) {
            path = "App"+delimiter+path;
        }
        return path;
    }

    public static MPackage getApplicationRoot(ModelElement me) {
        if( me == null ||me instanceof Model) {
            return null;
        }

        while( !(me instanceof MPackage) )  {
            me = me.getParent();
            if (me == null) {
                return null;
            }
        }
        MPackage pkg = (MPackage) me;
        while( !StereotypeHelper.hasStereotype(pkg, LaravelStereotypes.LARAVELAPPLICATION.getName()) ) {
            pkg = (MPackage) pkg.getParent();
            if( pkg==null ) {
                return null;
            }
        }
        return pkg;
    }

    private static String readNamespace(MPackage pkg) {
        String namespace = pkg.getTaggedValue(SymfonyStereotypes.PHPPACKAGE.getName(), "namespace");
        if( namespace == null ) {
            namespace = JavaHelper.firstToUpperCase(pkg.getName());
        }
        return namespace;
    }

    public static String toPackageName(ModelElement modelElement) {
        if( modelElement == null ) {
            return "";
        }
        ModelElement parent = modelElement.getParent();
        while( (parent != null ) && !(parent instanceof MPackage) ) {
            parent = parent.getParent();
        }
        if( parent == null ) {
            return "";
        }
        String pkgName = "";
        MPackage pkg = (MPackage) parent;
        while( !StereotypeHelper.hasStereotype(pkg, LaravelStereotypes.LARAVELAPPLICATION.getName()) ) {
            String namespace = readNamespace(pkg);
            if( !"".equals(pkgName) ) {
                pkgName = "\\"+pkgName;
            }
            pkgName = namespace + pkgName;
            if( pkg.getParent()==null || !(pkg.getParent() instanceof MPackage) ) {
                break;
            }
            pkg = (MPackage) pkg.getParent();
        }
        return pkgName;
    }

    public static String toFQPhpName(MClass mc) {
        return toNameSpace(mc)+"\\"+mc.getName();
    }

    public static String toTableName(MClass mClass) {
        String tableName = mClass.getTaggedValue(RESTStereotypes.ENTITY.getName(), "dbTableName");
        if( tableName == null || "".equals(tableName) ) {
            String name = mClass.getName().toLowerCase();
            tableName = toPlural(name);
        }
        return tableName;
    }

    public static String toPlural(String name) {
        if( name.endsWith("y") ) {
            name = name.substring(0, name.length()-1)+"ie";
        }
        return name +"s";
    }

    public static class Attributes {

        public static boolean isUnique(MAttribute mAttribute) {
            return "true".equals(mAttribute.getTaggedValue(RESTStereotypes.PERSISTENTFIELD.getName(), "unique"));
        }

        public static boolean isNullable(MAttribute mAttribute) {
            return !"false".equals(mAttribute.getTaggedValue(RESTStereotypes.PERSISTENTFIELD.getName(), "nullable"));
        }

        public static String getCast(MAttribute a) {
            if( "[]".equals(a.getProperty("typeModifier")) ) {
                return "array";
            }
            return null;
        }

        public static boolean isDetailOnly(MAttribute mAttribute) {
            return "true".equals(mAttribute.getTaggedValue(RESTStereotypes.PERSISTENTFIELD.getName(), "detailOnly"));
        }

        public static String toColumnType(MAttribute mAttribute) {
            switch (mAttribute.getType().toLowerCase() ) {
                case "boolean":
                    return "CheckBoxColumn";
                default:
                    return "TextColumn";
            }
        }

        public static boolean isDateAttribute(MAttribute mAttribute) {
            String lcType = mAttribute.getType().toLowerCase();
            return lcType.equals("date") || lcType.equals("datetime");
        }

        public static String toFormType(MAttribute mAttribute) {
            switch (mAttribute.getType().toLowerCase() ) {
                case "boolean":
                    return "Checkbox";
                case "text":
                    return "MarkdownEditor";
                case "date":
                    return "DatePicker";
                case "datetime":
                    return "DateTimePicker";
                default:
                    return "TextInput";
            }
        }

        public static String getLabel(MAttribute mAttribute) {
            String lable = mAttribute.getTaggedValue(RESTStereotypes.PERSISTENTFIELD.getName(), "label");
            if( lable == null ) {
                lable = JavaHelper.firstToUpperCase(mAttribute.getName());
            }
            return lable;
        }

        public static boolean isNummeric(MAttribute mAttribute) {
            // TODO: This could be done better
            return mAttribute.getType().toLowerCase().contains("int");
        }
    }

    public static class Associations {

        public static String toForeignIdName(MAssociation a) {
            MClass target = ((OOModel)a.getModel()).findClassByName(a.getType());
            return target.getName().toLowerCase()+"_id";
        }

        public static boolean isMultiple(String multiplicity) {
            return multiplicity.endsWith("*") || multiplicity.endsWith("n");
        }

        public static MClass getTarget(MAssociation a) {
            return ((OOModel)a.getModel()).findClassByName(a.getType());
        }
    }

}
