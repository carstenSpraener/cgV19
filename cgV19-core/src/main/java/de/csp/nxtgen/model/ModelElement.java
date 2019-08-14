package de.csp.nxtgen.model;

import java.util.List;
import java.util.Map;

/**
 * @startuml
 *
 * Model "1" *-- "many" ModelElement
 * ModelElement "many" *-- ModelElement: childs
 * ModelElement "1" *-- ModelElement: parent
 *
 * class ModelElement {
 *     +name: String
 *     +childs: ModelElement[]
 *     +parent: ModelElement
 *     +taggedValues: Map
 * }
 * @enduml
 */
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
