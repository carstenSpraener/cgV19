package de.csp.codegen.model.oom;

import de.csp.nxtgen.model.ModelElement;
import de.csp.nxtgen.model.Stereotype;
import de.csp.nxtgen.model.impl.ModelElementImpl;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class MClass extends ModelElementImpl {
    private List<MAttribute> attributes = null;
    private List<MReference> references = null;
    private MPackage parent = null;
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

        mc.references = ((ModelElementImpl) me).filterChilds(child -> {
            return child.getMetaType().equals("mReference");
        }).map(child -> {
            MReference ref = new MReference(child);
            ref.setParent(mc);
            return ref;
        }).collect(Collectors.toList());
        mc.inheritsFrom = new MClassRef(me.getProperty("inheritsFrom"));
        mc.setMetaType(me.getMetaType());
        mc.setStereotypes(me.getStereotypes());
        mc.setRelations(me.getRelations());
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
        return getPackage().getName()+"."+getName();
    }

    public MAttribute createAttribute(String name, String type) {
        MAttribute attr = new MAttribute(name, type);
        attr.setParent(this);
        addAttribute(attr);

        return attr;
    }

}
