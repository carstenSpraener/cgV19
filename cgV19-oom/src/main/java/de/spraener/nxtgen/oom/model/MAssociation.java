package de.spraener.nxtgen.oom.model;

import de.spraener.nxtgen.model.ModelElement;
import de.spraener.nxtgen.model.impl.ModelElementImpl;

public class MAssociation extends ModelElementImpl {
    private String assocId;
    private String opositeAttribute;
    private String opositeMultiplicity;
    private String associationType;
    private String type;
    private String multiplicity;
    private String composite;

    public MAssociation(ModelElement meCF) {
        this.setName(meCF.getName());
        super.setXmiID(assocId);
        OOModelHelper.mapProperties( this, getClass(), meCF);
        OOModelRepository.getInstance().put(this.assocId, this);
    }

    public String getAssocId() {
        return assocId;
    }

    public String getOpositeAttribute() {
        return opositeAttribute;
    }

    public String getOpositeMultiplicity() {
        return opositeMultiplicity;
    }

    public String getAssociationType() {
        return associationType;
    }

    public String getType() {
        return type;
    }

    public String getMultiplicity() {
        return multiplicity;
    }

    public String getComposite() {
        return composite;
    }

    public MAssociation setAssocId(String assocId) {
        this.assocId = assocId;
        return this;
    }

    public MAssociation setOpositeAttribute(String opositeAttribute) {
        this.opositeAttribute = opositeAttribute;
        return this;
    }

    public MAssociation setOpositeMultiplicity(String opositeMultiplicity) {
        this.opositeMultiplicity = opositeMultiplicity;
        return this;
    }

    public MAssociation setAssociationType(String associationType) {
        this.associationType = associationType;
        return this;
    }

    public MAssociation setType(String type) {
        this.type = type;
        return this;
    }

    public MAssociation setMultiplicity(String multiplicity) {
        this.multiplicity = multiplicity;
        return this;
    }

    public MAssociation setComposite(String composite) {
        this.composite = composite;
        return this;
    }
}
