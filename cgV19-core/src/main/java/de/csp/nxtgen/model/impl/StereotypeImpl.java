package de.csp.nxtgen.model.impl;

import de.csp.nxtgen.model.TaggedValue;

public class StereotypeImpl extends StereotypeImplBase {
    public StereotypeImpl(String name) {
        this.setName(name);
    }
    @Override
    public void setTaggedValue(String name, String value) {
        TaggedValueImpl tv = new TaggedValueImpl();
        tv.setName(name);
        tv.setValue(value);
        super.getTaggedValues().add(tv);
    }

    @Override
    public String getTaggedValue(String name) {
        for( TaggedValue tv : super.getTaggedValues() ) {
            if( tv.getName().equals(name) ) {
                return tv.getValue();
            }
        }
        return null;
    }
}
