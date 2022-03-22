package de.spraener.nxtgen.cartridge.rest.transformations;

public class TransformationHelper {

    public static String firstToUpperCaser(String name) {
        return ""+Character.toUpperCase(name.charAt(0))+name.substring(1);
    }
}
