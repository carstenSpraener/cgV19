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
        TaggedValueImpl tv = (TaggedValueImpl)getTaggedValues().stream().filter(v -> v.getName().equals(name)).findFirst().orElse(null);
        if( tv == null ) {
            tv = new TaggedValueImpl();
            tv.setName(name);
            super.getTaggedValues().add(tv);
        }
        tv.setValue(value);
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

    public StereotypeImpl removeTaggedValue(String name) {
        TaggedValue tv = getTaggedValues().stream().filter( v -> v.getName().equals(name)).findFirst().orElse(null);
        if( tv!=null ) {
            getTaggedValues().remove(tv);
        }
        return this;
    }
}
