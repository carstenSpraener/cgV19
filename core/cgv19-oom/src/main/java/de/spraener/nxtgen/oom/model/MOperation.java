package de.spraener.nxtgen.oom.model;

import de.spraener.nxtgen.model.ModelElement;
import de.spraener.nxtgen.model.impl.ModelElementImpl;
import de.spraener.nxtgen.oom.ModelHelper;
import de.spraener.nxtgen.oom.StereotypeHelper;

import java.util.ArrayList;
import java.util.List;
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
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public void setParameters(List<MParameter> parameters) {
        this.parameters = parameters;
    }

    public List<MParameter> getParameters() {
        if( parameters==null ) {
            parameters = new ArrayList<>();
        }
        return parameters;
    }

    public MOperation cloneTo(MClass mc ) {
        MOperation clone = new MOperation();
        clone.setName(this.getName());
        StereotypeHelper.cloneStereotypes(this, clone);
        ModelHelper.cloneProperties(this,clone);
        clone.type = this.type;
        clone.parameters = new ArrayList<>();
        for( MParameter p : this.parameters) {
            p.cloneTo(clone);
        }
        mc.getOperations().add(clone);
        return clone;
    }

    public MParameter createParameter(String name, String type) {
        MParameter p = new MParameter(this, name, type);
        getParameters().add(p);
        return p;
    }
}
