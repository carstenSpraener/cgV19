package de.csp.nxtgen.model.impl;

import de.csp.nxtgen.model.ModelElement;

import java.util.HashMap;
import java.util.function.Function;
import java.util.stream.Stream;

public class ModelElementImpl extends ModelElementImplBase {

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
        return getStereotypes().stream()
                .filter(st -> st.getName().equals(stereotypeName))
                .map(st -> st.getTaggedValue(valueName))
                .findFirst()
                .orElse(null);
    }

}
