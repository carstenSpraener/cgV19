package de.csp.nxtgen.model.impl;

import de.csp.nxtgen.model.Model;
import de.csp.nxtgen.model.ModelElement;
import de.csp.nxtgen.model.Relation;
import de.csp.nxtgen.model.Stereotype;

import java.util.ArrayList;
import java.util.List;

public class ModelImpl implements Model {
    private List<ModelElement> elements = new ArrayList<>();

    public void addModelElement(ModelElement e) {
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
