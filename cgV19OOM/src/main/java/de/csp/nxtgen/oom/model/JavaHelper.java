package de.csp.nxtgen.oom.model;

public class JavaHelper {

    public static String toPorpertyName( MAttribute attr) {
        return attr.getName().substring(0,1).toUpperCase() + attr.getName().substring(1);
    }

    public static boolean isEmpty(String str) {
        return str==null || "".equals(str.trim());
    }
}
