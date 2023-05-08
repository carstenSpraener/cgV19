// THIS FILE IS GENERATED AS LONG AS THIS LINE EXISTS
package de.spraener.nxtgen.model.impl;

import de.spraener.nxtgen.model.ModelElement;
import de.spraener.nxtgen.model.Relation;
import de.spraener.nxtgen.model.Stereotype;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public abstract class ModelElementImplBase implements ModelElement {
    private List<Stereotype> stereotypes = new ArrayList<>();
    private String name;
    private List<ModelElement> childs = new ArrayList<>();
    private transient ModelElement parent;
    private String metaType;
    private Map<String,String> properties;
    private String xmiID;
    private List<Relation> relations = new ArrayList<>();

    public ModelElementImplBase() {
    }

    public List<Stereotype> getStereotypes() {
        return this.stereotypes;
    }

    public void setStereotypes( List<Stereotype> value) {
        this.stereotypes = value;
    }

    public Stereotype addStereotypes( Stereotype value) {
        this.stereotypes.add(value);
        return value;
    }

    public Stereotype removeStereotypes( Stereotype value) {
        this.stereotypes.remove(value);
        return value;
    }

    public boolean containsStereotypes( Stereotype value) {
        return this.stereotypes.contains(value);
    }

    public String getName() {
        return this.name;
    }

    public void setName( String value) {
        this.name = value;
    }

    public List<ModelElement> getChilds() {
        return this.childs;
    }

    public void setChilds( List<ModelElement> value) {
        this.childs = value;
    }

    public ModelElement addChilds( ModelElement value) {
        this.childs.add(value);
        return value;
    }

    public ModelElement removeChilds( ModelElement value) {
        this.childs.remove(value);
        return value;
    }

    public boolean containsChilds( ModelElement value) {
        return this.childs.contains(value);
    }

    public ModelElement getParent() {
        return this.parent;
    }

    public void setParent( ModelElement value) {
        this.parent = value;
    }

    public String getMetaType() {
        return this.metaType;
    }

    public void setMetaType( String value) {
        this.metaType = value;
    }

    public Map<String,String> getProperties() {
        return this.properties;
    }

    public void setProperties( Map<String,String> value) {
        this.properties = value;
    }

    public String getXmiID() {
        return this.xmiID;
    }

    public void setXmiID( String value) {
        this.xmiID = value;
    }

    public List<Relation> getRelations() {
        return this.relations;
    }

    public void setRelations( List<Relation> value) {
        this.relations = value;
    }

    public Relation addRelations( Relation value) {
        this.relations.add(value);
        return value;
    }

    public Relation removeRelations( Relation value) {
        this.relations.remove(value);
        return value;
    }

    public boolean containsRelations( Relation value) {
        return this.relations.contains(value);
    }

    public void postDefinition() {}
}
