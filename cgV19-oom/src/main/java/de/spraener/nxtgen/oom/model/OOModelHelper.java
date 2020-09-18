package de.spraener.nxtgen.oom.model;

import de.spraener.nxtgen.model.ModelElement;
import de.spraener.nxtgen.model.impl.ModelElementImpl;

import java.lang.reflect.Field;
import java.util.logging.Logger;

public class OOModelHelper {
    public static void mapProperties(Object obj, Class<?> c, ModelElement me) {
        for( Field f : c.getDeclaredFields() ) {
            String value = me.getProperty(f.getName());
            if( value != null ) {
                f.setAccessible(true);
                try {
                    f.set(obj, value);
                } catch( Exception e ) {
                    Logger.getGlobal().severe("can't set field "+f.getName()+" to value "+value);
                }
            }
        }
        if( obj instanceof ModelElementImpl ) {
            ModelElementImpl meImpl = (ModelElementImpl) obj;
            for( String prop : me.getProperties().keySet() ) {
                meImpl.setProperty(prop, me.getProperty(prop));
            }
        }
    }
}
