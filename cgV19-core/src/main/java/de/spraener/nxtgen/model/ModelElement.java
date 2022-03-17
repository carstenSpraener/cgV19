package de.spraener.nxtgen.model;

import java.io.Serializable;
import java.util.List;
import java.util.Map;

public interface ModelElement extends Serializable {
    List<ModelElement> getChilds();
    ModelElement getParent();
    String getName();
    String getProperty(String key);
    String getMetaType();
    void setProperty(String key, String value);
    Map<String,String> getProperties();
    List<Stereotype> getStereotypes();
    List<Relation> getRelations();
    Model getModel();
    void setModel(Model m);
}
