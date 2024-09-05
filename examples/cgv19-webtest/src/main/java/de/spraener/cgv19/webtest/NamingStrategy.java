package de.spraener.cgv19.webtest;

import de.spraener.nxtgen.oom.cartridge.JavaHelper;

public class NamingStrategy {
    private static String charsToRemove = "-.:+@<>#'´`";

    public static String toClassName(String name) {
        return JavaHelper.firstToUpperCase(toJavaName(name, new StringBuilder()).toString());
    }

    private static StringBuilder toJavaName(String name, StringBuilder sb) {
        boolean nextToUpper = false;
        for( char c : name.toCharArray() ) {
            if( c == ' ' ) {
                nextToUpper = true;
                continue;
            }
            if( charsToRemove.indexOf(c) != -1 ) {
                continue;
            }
            if(nextToUpper) {
                c = Character.toUpperCase(c);
                nextToUpper = false;
            }
            translateUmlaute(c,sb);
        }
        return sb;
    }

    public static StringBuilder translateUmlaute(char c, StringBuilder sb) {
        switch(c) {
            case 'ä':
                sb.append("ae");
                break;
            case 'Ä':
                sb.append("Ae");
                break;
            case 'ö':
                sb.append("oe");
                break;
            case 'Ö':
                sb.append("Oe");
                break;
            case 'ü':
                sb.append("ue");
                break;
            case 'Ü':
                sb.append("Ue");
                break;
            case 'ß':
                sb.append("ss");
                break;
            default:
                sb.append(c);
                break;
        }
        return sb;
    }

    public static String toJavaName(String name) {
        return toJavaName(name, new StringBuilder()).toString();
    }
}
