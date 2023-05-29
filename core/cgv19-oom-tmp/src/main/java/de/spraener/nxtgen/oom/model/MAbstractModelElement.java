package de.spraener.nxtgen.oom.model;

import de.spraener.nxtgen.model.impl.ModelElementImpl;

import java.util.HashMap;
import java.util.Map;

public class MAbstractModelElement extends ModelElementImpl {
    private Map<String, Object> objectMap = null;

    @Override
    public void postDefinition() {
        OOModelHelper.mapProperties(this, this.getClass(), this);
        OOModelRepository.getInstance().put(this.getXmiID(), this);
    }

    private Map<String, Object> getObjectMap() {
        if( this.objectMap == null ) {
            this.objectMap = new HashMap<>();
        }
        return objectMap;
    }

    public MAbstractModelElement putObject(String key, Object value) {
        getObjectMap().put(key, value);
        return this;
    }

    public Object getObject(String key) {
        return getObjectMap().get(key);
    }
}
