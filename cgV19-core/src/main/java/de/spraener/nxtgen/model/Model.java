package de.spraener.nxtgen.model;

import java.util.List;

public interface Model {
    List<ModelElement> getModelElements();
    ModelElement createModelElement();
    Stereotype createStereotype(String name);
    Relation createRelation();
}
