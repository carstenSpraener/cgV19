package de.spraener.nxtgen.oom.model;

import de.spraener.nxtgen.model.ModelElement;
import de.spraener.nxtgen.model.Relation;
import de.spraener.nxtgen.model.Stereotype;
import de.spraener.nxtgen.model.impl.ModelElementImpl;
import de.spraener.nxtgen.oom.ModelHelper;
import de.spraener.nxtgen.oom.StereotypeHelper;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class MClass extends MAbstractModelElement {
    private List<MAttribute> attributes = null;
    private List<MAssociation> associations = null;
    private List<MReference> references = null;
    private List<MOperation> operations = null;
    private List<MActivity> activities = null;
    private List<MUsage> usages = null;
    private List<MDependency> dependencies = null;
    private transient MPackage parent = null;

    private MClassRef inheritsFrom = null;

    public MClass() {
    }

    public void postDefinition() {
        Relation rExtendsFrom = getRelations().stream()
                .filter( r -> r.getType().equals("extends"))
                .findFirst()
                .orElse(null);
        if( rExtendsFrom != null ) {
            this.inheritsFrom = new MClassRef(rExtendsFrom.getTargetType());
        }

    }

    public List<MAttribute> getAttributes() {
        if( attributes == null ) {
            attributes = filterChilds(child -> child instanceof MAttribute)
                    .map(child -> (MAttribute)child)
                    .collect(Collectors.toList());
        }
        return attributes;
    }

    public List<MReference> getReferences() {
        if( references == null ) {
            references = filterChilds(child -> child instanceof MReference)
                    .map(child -> (MReference)child)
                    .collect(Collectors.toList());
        }
        return references;
    }

    public void setParent(ModelElement me) {
        this.parent = (MPackage) me;
    }

    public MPackage getPackage() {
        return parent;
    }

    public boolean hasStereotype(String stereotype) {
        for (Stereotype st : this.getStereotypes()) {
            if (st.getName().equals(stereotype)) {
                return true;
            }
        }
        return false;
    }

    public MAttribute addAttribute(MAttribute attr) {
        getAttributes().add(attr);
        attr.setParent(this);
        return attr;
    }

    public String getFQName() {
        return getPackage().getFQName()+"."+getName();
    }

    public MAttribute createAttribute(String name, String type) {
        MAttribute attr = new MAttribute(name, type);
        attr.setParent(this);
        addAttribute(attr);

        return attr;
    }

    public MOperation createOperation(String name) {
        MOperation op = new MOperation(this, name);
        getOperations().add(op);
        return op;
    }

    public MClass cloneTo(MPackage targetPkg, String className) {
        MClass target = targetPkg.createMClass(className);
        List<MAttribute> attrList = target.getAttributes();

        StereotypeHelper.cloneStereotypes(this, target);
        ModelHelper.cloneProperties(this,target);

        for( MAttribute myAttr : getAttributes() ) {
            myAttr.cloneTo(target);
        }
        for( MReference ref : this.getReferences() ) {
            ref.cloneTo(target);
        }
        if( this.inheritsFrom != null ) {
            target.inheritsFrom = new MClassRef(this.inheritsFrom.getFullQualifiedClassName());
        }
        return target;
    }

    public MClassRef getInheritsFrom() {
        return inheritsFrom;
    }

    public void setInheritsFrom(MClassRef inheritsFrom) {
        this.inheritsFrom = inheritsFrom;
    }

    public List<MOperation> getOperations() {
        if( this.operations == null ) {
            operations = filterChilds(child -> child instanceof MOperation)
                    .map(child -> (MOperation)child)
                    .collect(Collectors.toList());
        }
        return operations;
    }

    public void setOperations(List<MOperation> operations) {
        this.operations = operations;
    }

    public List<MActivity> getActivities() {
        if( this.activities == null ) {
            this.activities = filterChilds(child -> child instanceof MActivity)
                    .map(child -> (MActivity)child)
                    .collect(Collectors.toList());
        }
        return activities;
    }

    public List<MAssociation> getAssociations() {
        if( this.associations == null ) {
            this.associations = filterChilds(child -> child instanceof MAssociation)
                    .map(child -> (MAssociation)child)
                    .collect(Collectors.toList());
        }
        return associations;
    }

    public List<MUsage> getUsages() {
        if( this.usages == null ) {
            this.usages =  filterChilds(child -> child instanceof MUsage)
                    .map(child -> (MUsage)child)
                    .collect(Collectors.toList());
        }
        return usages;
    }

    public List<MDependency> getDependencies() {
        if( this.dependencies == null ) {
            this.dependencies =  filterChilds(child -> child instanceof MDependency)
                    .map(child -> (MDependency)child)
                    .collect(Collectors.toList());
        }
        return dependencies;
    }

    @Override
    public MPackage getParent() {
        return parent;
    }
}
