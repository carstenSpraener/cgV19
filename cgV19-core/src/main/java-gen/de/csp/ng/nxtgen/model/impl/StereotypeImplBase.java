// THIS FILE IS GENERATED AS LONG AS THIS LINE EXISTS
package de.csp.ng.nxtgen.model.impl;

public abstract class StereotypeImplBase implements de.csp.ng.nxtgen.model.Stereotype {
    private String name;
    private List<de.csp.ng.nxtgen.model.TaggedValue> taggedValues = new java.util.ArrayList<>();

    public StereotypeImplBase() {
    }

    public String getName() {
        return this.name;
    }

    public void setName( String value) {
        this.name = value;
    }

    public List<de.csp.ng.nxtgen.model.TaggedValue> getTaggedValues() {
        return this.taggedValues;
    }

    public void setTaggedValues( List<de.csp.ng.nxtgen.model.TaggedValue> value) {
        this.taggedValues = value;
    }

    public de.csp.ng.nxtgen.model.TaggedValue addTaggedValues( de.csp.ng.nxtgen.model.TaggedValue value) {
        this.taggedValues.add(value);
        return value;
    }

    public de.csp.ng.nxtgen.model.TaggedValue removeTaggedValues( de.csp.ng.nxtgen.model.TaggedValue value) {
        this.taggedValues.remove(value);
        return value;
    }

    public boolean containsTaggedValues( de.csp.ng.nxtgen.model.TaggedValue value) {
        return this.taggedValues.contains(value);
    }


}
