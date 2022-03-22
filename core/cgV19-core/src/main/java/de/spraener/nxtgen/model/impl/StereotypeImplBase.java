// THIS FILE IS GENERATED AS LONG AS THIS LINE EXISTS
package de.spraener.nxtgen.model.impl;

import de.spraener.nxtgen.model.Stereotype;
import de.spraener.nxtgen.model.TaggedValue;

import java.util.ArrayList;
import java.util.List;

public abstract class StereotypeImplBase implements Stereotype {
    private String name;
    private List<TaggedValue> taggedValues = new ArrayList<>();

    public StereotypeImplBase() {
    }

    public String getName() {
        return this.name;
    }

    public void setName( String value) {
        this.name = value;
    }

    public List<TaggedValue> getTaggedValues() {
        return this.taggedValues;
    }

    public void setTaggedValues( List<TaggedValue> value) {
        this.taggedValues = value;
    }

    public TaggedValue addTaggedValues( TaggedValue value) {
        this.taggedValues.add(value);
        return value;
    }

    public TaggedValue removeTaggedValues( TaggedValue value) {
        this.taggedValues.remove(value);
        return value;
    }

    public boolean containsTaggedValues( TaggedValue value) {
        return this.taggedValues.contains(value);
    }


}
