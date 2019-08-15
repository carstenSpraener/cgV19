// THIS FILE IS GENERATED AS LONG AS THIS LINE EXISTS
package de.csp.ng.nxtgen.model.impl;

public abstract class ModelElementImplBase implements de.csp.ng.nxtgen.model.ModelElement {
    private List<de.csp.ng.nxtgen.model.Stereotype> stereotypes = new java.util.ArrayList<>();
    private String name;
    private List<de.csp.ng.nxtgen.model.ModelElement> childs = new java.util.ArrayList<>();
    private de.csp.ng.nxtgen.model.ModelElement parent;
    private String metaType;
    private java.util.Map<String,String> properties;
    private String xmiID;
    private List<de.csp.ng.nxtgen.model.Relation> relations = new java.util.ArrayList<>();

    public ModelElementImplBase() {
    }

    public List<de.csp.ng.nxtgen.model.Stereotype> getStereotypes() {
        return this.stereotypes;
    }

    public void setStereotypes( List<de.csp.ng.nxtgen.model.Stereotype> value) {
        this.stereotypes = value;
    }

    public de.csp.ng.nxtgen.model.Stereotype addStereotypes( de.csp.ng.nxtgen.model.Stereotype value) {
        this.stereotypes.add(value);
        return value;
    }

    public de.csp.ng.nxtgen.model.Stereotype removeStereotypes( de.csp.ng.nxtgen.model.Stereotype value) {
        this.stereotypes.remove(value);
        return value;
    }

    public boolean containsStereotypes( de.csp.ng.nxtgen.model.Stereotype value) {
        return this.stereotypes.contains(value);
    }

    public String getName() {
        return this.name;
    }

    public void setName( String value) {
        this.name = value;
    }

    public List<de.csp.ng.nxtgen.model.ModelElement> getChilds() {
        return this.childs;
    }

    public void setChilds( List<de.csp.ng.nxtgen.model.ModelElement> value) {
        this.childs = value;
    }

    public de.csp.ng.nxtgen.model.ModelElement addChilds( de.csp.ng.nxtgen.model.ModelElement value) {
        this.childs.add(value);
        return value;
    }

    public de.csp.ng.nxtgen.model.ModelElement removeChilds( de.csp.ng.nxtgen.model.ModelElement value) {
        this.childs.remove(value);
        return value;
    }

    public boolean containsChilds( de.csp.ng.nxtgen.model.ModelElement value) {
        return this.childs.contains(value);
    }

    public de.csp.ng.nxtgen.model.ModelElement getParent() {
        return this.parent;
    }

    public void setParent( de.csp.ng.nxtgen.model.ModelElement value) {
        this.parent = value;
    }

    public String getMetaType() {
        return this.metaType;
    }

    public void setMetaType( String value) {
        this.metaType = value;
    }

    public java.util.Map<String,String> getProperties() {
        return this.properties;
    }

    public void setProperties( java.util.Map<String,String> value) {
        this.properties = value;
    }

    public String getXmiID() {
        return this.xmiID;
    }

    public void setXmiID( String value) {
        this.xmiID = value;
    }

    public List<de.csp.ng.nxtgen.model.Relation> getRelations() {
        return this.relations;
    }

    public void setRelations( List<de.csp.ng.nxtgen.model.Relation> value) {
        this.relations = value;
    }

    public de.csp.ng.nxtgen.model.Relation addRelations( de.csp.ng.nxtgen.model.Relation value) {
        this.relations.add(value);
        return value;
    }

    public de.csp.ng.nxtgen.model.Relation removeRelations( de.csp.ng.nxtgen.model.Relation value) {
        this.relations.remove(value);
        return value;
    }

    public boolean containsRelations( de.csp.ng.nxtgen.model.Relation value) {
        return this.relations.contains(value);
    }


}
