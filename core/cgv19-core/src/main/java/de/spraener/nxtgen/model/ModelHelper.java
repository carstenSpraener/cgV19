package de.spraener.nxtgen.model;

public class ModelHelper {

    public static String getFQName(ModelElement me, String separator ) {
        if( me.getParent()==null || me.getParent() instanceof Model ) {
            return me.getName();
        }
        return getFQName(me.getParent(), separator)+separator+me.getName();
    }
}
