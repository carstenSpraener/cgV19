package de.spraener.nxtgen.cartridge.rest.php;

public class PhpHelper {

    public static String toPhpORMType(String javaType) {
        if( javaType.startsWith("VARCHAR") ) {
            return "type=\"string\", length=255";
        }
        if( javaType.startsWith("DECIMAL") ) {
            return "type=\"decimal\"";
        }
        if( javaType.startsWith("DATE") ) {
            return "type=\"date\"";
        }
        if( javaType.startsWith("INTEGER") ) {
            return "type=\"integer\"";
        }
        if( javaType.startsWith("CHAR(1)") ) {
            return "type=\"boolean\"";
        }
        return "type=\"string\", length=255";
    }


    public static int toPhpORMLength(String javaType) {
        return 255;
    }

    public static String toClassName(String fqName) {
        return fqName.substring(fqName.lastIndexOf('.')+1);
    }
}
