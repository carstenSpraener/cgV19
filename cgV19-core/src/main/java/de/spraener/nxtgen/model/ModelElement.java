package de.spraener.nxtgen.model;

import java.util.List;
import java.util.Map;

public interface ModelElement {
    List<ModelElement> getChilds();
    ModelElement getParent();
    String getName();
    String getProperty(String key);
    String getMetaType();
    void setProperty(String key, String value);
    Map<String,String> getProperties();
    List<Stereotype> getStereotypes();
    List<Relation> getRelations();
}
