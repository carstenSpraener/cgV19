package de.spraener.nxtgen.oom.model;

import de.spraener.nxtgen.model.ModelElement;
import de.spraener.nxtgen.model.impl.ModelElementImpl;
import de.spraener.nxtgen.oom.ModelHelper;
import de.spraener.nxtgen.oom.StereotypeHelper;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;
import java.util.stream.Collectors;

public class MOperation extends MAbstractModelElement {
    public List<MParameter> parameters = null;
    private String type = "void";

    public void postDefinition() {
        this.parameters = filterChilds( child -> child instanceof MParameter)
                .map(child -> (MParameter)child)
                .collect(Collectors.toList());
        super.postDefinition();
    }

    protected MOperation() {}

    protected MOperation(MClass parent, String name) {
        this.setParent(parent);
        this.setName(name);
        this.setModel(parent.getModel());
    }

    public String getType() {
        return type;
    }

    public MOperation setType(String type) {
        this.type = type;
        return this;
    }

    public MOperation setParameters(List<MParameter> parameters) {
        this.parameters = parameters;
        return this;
    }

    public List<MParameter> getParameters() {
        if( parameters==null ) {
            parameters = new ArrayList<>();
        }
        return parameters;
    }

    @Override
    public MClass getParent() {
        ModelElement p = super.getParent();
        return (p instanceof MClass) ? (MClass) p : null;   // defensive, never a hard cast
    }

    public MOperation cloneTo(MClass mc ) {
        MOperation clone = new MOperation(mc, getName());
        clone.setName(this.getName());
        StereotypeHelper.cloneStereotypes(this, clone);
        ModelHelper.cloneProperties(this,clone);
        clone.type = this.type;
        clone.parameters = new ArrayList<>();
        for( MParameter p : this.getParameters()) {
            p.cloneTo(clone);
        }
        mc.getOperations().add(clone);
        return clone;
    }

    public MParameter createParameter(String name, String type) {
        // Model and Parent is set in the constructor
        MParameter p = new MParameter(this, name, type);
        getParameters().add(p);
        getChilds().add(p);

        return p;
    }

    public MOperation createParameter(String name, Consumer<MParameter>... modifiers) {
        MParameter p = new MParameter(this, name, null);   // protected ctor (same package); type via consumer
        getParameters().add(p);
        getChilds().add(p);
        if (modifiers != null) { for (Consumer<MParameter> m : modifiers) { m.accept(p); } }
        return this;                                       // parent → parameter chaining on the operation
    }
}
