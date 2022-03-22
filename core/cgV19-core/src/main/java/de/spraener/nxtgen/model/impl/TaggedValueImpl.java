// THIS FILE IS GENERATED AS LONG AS THIS LINE EXISTS
package de.spraener.nxtgen.model.impl;

import de.spraener.nxtgen.model.TaggedValue;

public class TaggedValueImpl implements TaggedValue {
    String name;
    String value;

    public TaggedValueImpl() {
    }

    @Override
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @Override
    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }
}
