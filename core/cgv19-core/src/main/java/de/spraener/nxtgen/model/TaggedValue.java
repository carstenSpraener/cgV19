package de.spraener.nxtgen.model;

import de.spraener.nxtgen.model.impl.TaggedValueImpl;

public interface TaggedValue {
    String getName();
    String getValue();

    static TaggedValue of(String name, Object value) {
        TaggedValueImpl tv = new TaggedValueImpl();
        tv.setName(name);
        tv.setValue(value == null ? null : String.valueOf(value));
        return tv;
    }
}
