package de.spraener.nxtgen.oom;

import de.spraener.nxtgen.model.ModelElement;
import de.spraener.nxtgen.oom.model.MAttribute;
import de.spraener.nxtgen.oom.model.MClass;

public class ModelHelper {
    public static void cloneProperties(ModelElement from, ModelElement to) {
        for( String pName : from.getProperties().keySet() ) {
            to.setProperty(pName, from.getProperty(pName));
        }
    }
}
