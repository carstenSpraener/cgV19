package de.spraener.nxtgen.oom.model;

import de.spraener.nxtgen.model.ModelElement;
import de.spraener.nxtgen.model.Stereotype;
import de.spraener.nxtgen.model.impl.ModelElementImpl;
import de.spraener.nxtgen.oom.ModelHelper;
import de.spraener.nxtgen.oom.StereotypeHelper;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class MClass extends ModelElementImpl {
    private List<MAttribute> attributes = null;
    private List<MAssociation> associations = null;
    private List<MReference> references = null;
    private List<MOperation> operations = null;
    private List<MActivity> activities = null;
    private transient MPackage parent = null;

    private MClassRef inheritsFrom = null;

    public MClass() {
    }

    public static MClass from(ModelElement parent, ModelElement me) {
        return from(new MClass(), parent, me);
    }

    public static MClass from(MClass mc, ModelElement parent, ModelElement me) {
        mc.setParent(parent);
        mc.setName(me.getName());
        mc.attributes = ((ModelElementImpl) me).filterChilds(child -> {
            return child.getMetaType().equals("mAttribute");
        }).map(child -> {
            MAttribute attr = new MAttribute(child);
            attr.setParent(mc);
            return attr;
        }).collect(Collectors.toList());

        mc.associations = ((ModelElementImpl) me).filterChilds(child -> {
            return child.getMetaType().equals("mAssociation");
        }).map(child -> {
            MAssociation assoc = new MAssociation(child);
            assoc.setParent(mc);
            return assoc;
        }).collect(Collectors.toList());

        mc.references = ((ModelElementImpl) me).filterChilds(child -> {
            return child.getMetaType().equals("mReference");
        }).map(child -> {
            MReference ref = new MReference(child);
            ref.setParent(mc);
            return ref;
        }).collect(Collectors.toList());

        mc.operations = ((ModelElementImpl)me).filterChilds( child -> {
            return child.getMetaType().equals("mOperation");
        }).map(child -> {
            MOperation op = new MOperation(child);
            op.setParent(mc);
            return op;
        }).collect(Collectors.toList());

        mc.activities = ((ModelElementImpl)me).filterChilds( child -> {
            return child.getMetaType().equals("mActivity");
        }).map(childAcitvity-> {
            MActivity activity = new MActivity(childAcitvity);
            activity.setParent(mc);
            return activity;
        }).collect(Collectors.toList());

        String extendsFQClassName = ((ModelElementImpl)me).getRelations().stream()
                .filter(r -> "extends".equals(r.getType()))
                        .map(r->r.getTargetType())
                .findFirst().orElse(null);
        if( extendsFQClassName!=null ) {
            mc.inheritsFrom = new MClassRef(extendsFQClassName);
        } else {
            mc.inheritsFrom = null;
        }
        mc.setMetaType(me.getMetaType());
        mc.setStereotypes(me.getStereotypes());
        mc.setRelations(me.getRelations());
        mc.setProperties(me.getProperties());
        return mc;
    }

    public MClass(ModelElement me) {
        MClass.from(this, null, me);
    }

    public List<MAttribute> getAttributes() {
        if( attributes == null ) {
            attributes = new ArrayList<>();
        }
        return attributes;
    }

    public List<MReference> getReferences() {
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
            this.operations = new ArrayList<>();
        }
        return operations;
    }

    public void setOperations(List<MOperation> operations) {
        this.operations = operations;
    }

    public List<MActivity> getActivities() {
        if( this.activities == null ) {
            this.activities = new ArrayList<>();
        }
        return activities;
    }

    public List<MAssociation> getAssociations() {
        if( this.associations == null ) {
            this.associations = new ArrayList<>();
        }
        return associations;
    }

    @Override
    public MPackage getParent() {
        return parent;
    }
}
