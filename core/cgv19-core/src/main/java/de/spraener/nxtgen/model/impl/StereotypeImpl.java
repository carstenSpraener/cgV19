package de.spraener.nxtgen.model.impl;

import de.spraener.nxtgen.model.TaggedValue;

import java.util.function.Consumer;

public class StereotypeImpl extends StereotypeImplBase {
    public StereotypeImpl(String name, Consumer<StereotypeImpl>... modifiers) {
        this.setName(name);
        if(modifiers!=null) {
            for( Consumer<StereotypeImpl> c : modifiers ) {
                c.accept(this);
            }
        }
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
