package de.spraener.nxtgen.cloud.model;

import de.spraener.nxtgen.cloud.CloudStereotypes;
import de.spraener.nxtgen.model.ModelElement;
import de.spraener.nxtgen.model.ModelHelper;
import de.spraener.nxtgen.model.impl.ModelElementImpl;
import de.spraener.nxtgen.oom.StereotypeHelper;
import de.spraener.nxtgen.oom.model.*;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class MComponent extends MAbstractModelElement {
    private ModelElementImpl originalModelElement;
    private List<MPort> portList = new ArrayList<>();
    private List<MDependency> dependencies = new ArrayList<>();

    public MComponent(ModelElementImpl meImpl) {
        setModel(meImpl.getModel());
        setName(meImpl.getName());
        setOriginalModelElement(meImpl);
        setParent(meImpl.getParent());
        getStereotypes().addAll(meImpl.getStereotypes());
        if (meImpl.getParent() != null) {
            meImpl.getParent().getChilds().add(this);
        }
        this.dependencies = meImpl.getChilds().stream()
                .filter(c -> c instanceof MDependency)
                .map(c -> (MDependency) c)
                .collect(Collectors.toList());
        this.originalModelElement = meImpl;
    }

    public static String getServiceName(MDependency mDependency) {
        OOModel model = (OOModel)mDependency.getModel();
        ModelElement me = ModelHelper.findByFQName(model, mDependency.getTarget(), ".");
        ModelElement targetComponent = me.getParent();
        return targetComponent.getName().toLowerCase();
    }

    public static String getServicePort(MDependency mDependency) {
        OOModel model = (OOModel)mDependency.getModel();
        ModelElement me = ModelHelper.findByFQName(model, mDependency.getTarget(), ".");
        return me.getName();
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
        for (MDependency dep : this.dependencies) {
            ModelElement target = ModelHelper.findByFQName(model, dep.getTarget(), ".");
            if (target instanceof MPackage pkg && StereotypeHelper.hasStereotype(pkg, CloudStereotypes.CLOUDMODULE.getName())) {
                return pkg;
            }
        }
        return null;
    }

    public List<MDependency> getDependenciesWithStereotype(String s) {
        return this.dependencies.stream()
                .filter( d -> StereotypeHelper.hasStereotype(d, s))
                .collect(Collectors.toList());
    }
}
