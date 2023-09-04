package de.spraener.nextgen.vpplugin.dslimport;

import java.util.ArrayList;
import java.util.List;

public class Stereotype {
    private String name;
    private String baseClass;
    private List<TaggedValue> taggedValues = new ArrayList<>();

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public List<TaggedValue> getTaggedValues() {
        return taggedValues;
    }

    public void setTaggedValues(List<TaggedValue> taggedValues) {
        this.taggedValues = taggedValues;
    }

    public String getBaseClass() {
        return baseClass;
    }

    public void setBaseClass(String baseClass) {
        this.baseClass = baseClass;
    }
}
