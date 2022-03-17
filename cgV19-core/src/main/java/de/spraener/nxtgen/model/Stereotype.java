package de.spraener.nxtgen.model;

import java.util.List;

public interface Stereotype {
     void setTaggedValue(String name, String value);
     String getTaggedValue( String name ) ;
     String getName();
     List<TaggedValue> getTaggedValues();
}
