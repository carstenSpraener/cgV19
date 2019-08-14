package de.csp.nxtgen.model;

import java.util.HashMap;
import java.util.Map;

public interface Stereotype {
     void setTaggedValue(String name, String value);
     String getTaggedValue( String name ) ;
     String getName();
}
