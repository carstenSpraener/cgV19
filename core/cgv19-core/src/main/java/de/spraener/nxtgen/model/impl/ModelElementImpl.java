package de.spraener.nxtgen.model.impl;

import de.spraener.nxtgen.model.Model;
import de.spraener.nxtgen.model.ModelElement;
import de.spraener.nxtgen.model.Stereotype;

import java.beans.Transient;
import java.util.HashMap;
import java.util.function.Function;
import java.util.stream.Stream;

public class ModelElementImpl extends ModelElementImplBase {

    private transient Model model;

    public ModelElementImpl() {
        super.setProperties(new HashMap<>());
    }
    @Override
    public String getProperty(String key) {
        return super.getProperties().get(key);
    }

    @Override
    public void setProperty(String key, String value) {
        super.getProperties().put(key, value);
    }


    public Stream<ModelElement> filterChilds(Function<ModelElement,Boolean> filter ) {
        return getChilds().stream().filter( me -> {
            return filter.apply(me);
        });
    }

    public String getTaggedValue( String stereotypeName, String valueName) {
        Stereotype sType =  getStereotypes().stream()
                .filter(st -> st.getName().equals(stereotypeName)).findFirst().orElse(null);
        if( sType == null ) {
            return null;
        }
        return sType.getTaggedValue(valueName);
    }

    @Override
    public Model getModel() {
        return model;
    }

    @Override
    public void setModel(Model model) {
        this.model = model;
    }
}
