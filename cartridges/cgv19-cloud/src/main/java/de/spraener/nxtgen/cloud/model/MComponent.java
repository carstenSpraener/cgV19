package de.spraener.nxtgen.cloud.model;

import de.spraener.nxtgen.cloud.CloudStereotypes;
import de.spraener.nxtgen.model.ModelElement;
import de.spraener.nxtgen.model.ModelHelper;
import de.spraener.nxtgen.model.Relation;
import de.spraener.nxtgen.model.Stereotype;
import de.spraener.nxtgen.model.impl.ModelElementImpl;
import de.spraener.nxtgen.oom.StereotypeHelper;
import de.spraener.nxtgen.oom.model.*;

import java.util.ArrayList;
import java.util.List;

public class MComponent extends MAbstractModelElement {
    private ModelElementImpl originalModelElement;
    private List<MPort> portList = new ArrayList<>();

    public MComponent(ModelElementImpl meImpl) {
        setModel(meImpl.getModel());
        setName(meImpl.getName());
        setOriginalModelElement(meImpl);
        setParent(meImpl.getParent());
        getStereotypes().addAll(meImpl.getStereotypes());
        if( meImpl.getParent() != null ) {
            meImpl.getParent().getChilds().add(this);
        }
        this.originalModelElement = meImpl;
    }

    public ModelElementImpl getOriginalModelElement() {
        return originalModelElement;
    }

    public void setOriginalModelElement(ModelElementImpl originalModelElement) {
        this.originalModelElement = originalModelElement;
    }

    public List<MPort> getPortList() {
        return portList;
    }

    public void addPort(MPort p) {
        p.setParent(this);
        this.addChilds(p);
        this.portList.add(p);
    }

    public MPackage getProvidedCloudModule() {
        OOModel model = (OOModel) this.originalModelElement.getModel();
        for(Relation dep : this.originalModelElement.getRelations() ) {
            ModelElement target = ModelHelper.findByFQName(model, dep.getTargetType(), ".");
            if( target instanceof MPackage pkg && StereotypeHelper.hasStereotype(pkg, CloudStereotypes.CLOUDMODULE.getName())) {
                return pkg;
            }
        }
        return null;
    }
}
