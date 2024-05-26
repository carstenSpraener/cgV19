package de.spraener.nxtgen.model.impl;

import de.spraener.nxtgen.model.Model;
import de.spraener.nxtgen.model.ModelElement;
import de.spraener.nxtgen.model.Relation;
import de.spraener.nxtgen.model.Stereotype;

import java.util.ArrayList;
import java.util.List;

public class ModelImpl implements Model {
    private List<ModelElement> elements = new ArrayList<>();

    public void addModelElement(ModelElement e) {
        e.setModel(this);
        elements.add(e);
    }

    @Override
    public List<ModelElement> getModelElements() {
        List<ModelElement> flattenElements = new ArrayList<>();
        for( ModelElement e : elements ) {
            collectElements(e, flattenElements);
        }
        return flattenElements;
    }

    public void collectElements(ModelElement e, List<ModelElement> flattenElements) {
        e.setModel(this);
        flattenElements.add(e);
        for( ModelElement child : e.getChilds() ) {
            collectElements(child,flattenElements);
        }
    }

    @Override
    public ModelElement createModelElement() {
        return new ModelElementImpl();
    }

    @Override
    public Stereotype createStereotype(String name) {
        return new StereotypeImpl(name);
    }

    @Override
    public Relation createRelation() {
        return new RelationImpl();
    }

    public List<ModelElement> getChilds() {
        return this.elements;
    }
}
