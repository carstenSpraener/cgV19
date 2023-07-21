package de.spraener.nxtgen.pojo;

import java.util.Map;

public class StringEvaluator {
    public static String evaluate(String code, Map<String, String> varMap) {
        for( Map.Entry e : varMap.entrySet() ) {
            code = code.replaceAll("\\$\\{"+e.getKey()+"\\}", ""+e.getValue());
        }
        return code;
    }
}
