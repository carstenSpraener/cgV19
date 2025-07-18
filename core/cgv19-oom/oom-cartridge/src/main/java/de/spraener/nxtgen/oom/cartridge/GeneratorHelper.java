package de.spraener.nxtgen.oom.cartridge;

import de.spraener.nxtgen.oom.model.*;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Predicate;

public class GeneratorHelper {

    public static String extendsStr(MClass mc) {
        return extendsStr(mc, "");
    }

    public static String extendsStr(MClass mc, String defExtends) {
        MClassRef ref = mc.getInheritsFrom();
        if (ref == null) {
            return defExtends;
        }
        return " extends " + ref.getFullQualifiedClassName();
    }

    public static List<MAssociation> listAssociationsWith(MClass mc, Predicate<MAssociation> p) {
        List<MAssociation> assocs = new ArrayList<>();
        for (MAssociation ref : mc.getAssociations()) {
            if (p.test(ref)) {
                assocs.add(ref);
            }
        }
        return assocs;
    }

    public static boolean namedToNMElementAssociationPredicate(MAssociation assoc) {
        if( assoc.getName().equals("null") ) {
            return false;
        }
        if( assoc.getMultiplicity().endsWith("*") || assoc.getMultiplicity().endsWith("n") ) {
            return toMElemntAssociationPredicate(assoc);
        } else {
            return false;
        }
    }

    public static boolean namedToOneMElementAssociationPredicate(MAssociation assoc) {
        if( assoc.getName().equals("null") ) {
            return false;
        }
        if( assoc.getMultiplicity().endsWith("1") ) {
            return toMElemntAssociationPredicate(assoc);
        } else {
            return false;
        }
    }

    private static boolean toMElemntAssociationPredicate(MAssociation assoc) {
        MClass targetType = ((OOModel) assoc.getModel()).findClassByName(assoc.getType());
        return targetType.hasStereotype(OomStereoTypes.MELEMENT.getName());
    }

    public static List<MAssociation> listToNMElementAssociations(MClass mc) {
        return listAssociationsWith(mc,GeneratorHelper::namedToNMElementAssociationPredicate);
    }

    public static List<MAssociation> listToOneMElementAssociations(MClass mc) {
        return listAssociationsWith(mc,GeneratorHelper::namedToOneMElementAssociationPredicate);
    }

    public static String firstToUpperCase(String name) {
        return name.substring(0, 1).toUpperCase() + name.substring(1);
    }
}
