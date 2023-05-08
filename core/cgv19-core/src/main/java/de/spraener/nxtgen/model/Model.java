package de.spraener.nxtgen.model;

import java.io.Serializable;
import java.util.List;

public interface Model extends Serializable {
    List<ModelElement> getModelElements();
    ModelElement createModelElement();
    Stereotype createStereotype(String name);
    Relation createRelation();
}
