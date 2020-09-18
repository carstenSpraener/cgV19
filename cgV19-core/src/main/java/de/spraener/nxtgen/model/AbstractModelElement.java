package de.spraener.nxtgen.model;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Stream;

public abstract class AbstractModelElement implements ModelElement {
    private List<ModelElement> childs = new ArrayList<>();
    private String name = "UNKNOWN";
    private ModelElement parent;
    private List<Stereotype> stereotypes = null;
    private List<Relation> relations = new ArrayList<>();
    private Map<String,String> properties = new HashMap<>();
    private Model model = null;

    public AbstractModelElement() {}

    public AbstractModelElement(ModelElement me) {
        setName(me.getName());
        this.childs = me.getChilds();
        this.properties = me.getProperties();
        this.stereotypes = me.getStereotypes();
        this.parent = me.getParent();
    }

    @Override
    public List<ModelElement> getChilds() {
        return childs;
    }

    public ModelElement addModelElement(ModelElement child) {
        childs.add(child);
        return this;
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public String getProperty(String key) {
        return properties.get(key);
    }

    @Override
    public void setProperty(String key, String value) {
        properties.put(key,value);
    }

    public ModelElement setName(String name){
        this.name = name;
        return this;
    }

    public Stream<ModelElement> filterChilds(Function<ModelElement,Boolean> filter ) {
        return childs.stream().filter( me -> {
            return filter.apply(me);
        });
    }

    @Override
    public ModelElement getParent() {
        return parent;
    }

    public void setParent(ModelElement parent) {
        this.parent = parent;
    }

    @Override
    public Map<String, String> getProperties() {
        return properties;
    }

    public List<Stereotype> getStereotypes() {
        return this.stereotypes;
    }

    public List<Relation> getRelations() {
        return this.relations;
    }

    public Model getModel() {
        return model;
    }

    public void setModel(Model model) {
        this.model = model;
    }
}
