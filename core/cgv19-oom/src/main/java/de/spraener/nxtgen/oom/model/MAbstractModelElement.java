package de.spraener.nxtgen.oom.model;

import de.spraener.nxtgen.model.impl.ModelElementImpl;

import java.security.Provider;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Consumer;
import java.util.function.Supplier;

public class MAbstractModelElement extends ModelElementImpl {
    private Map<String, Object> objectMap = null;

    public static <T extends MAbstractModelElement> T createMElement(MAbstractModelElement parent, Supplier<T> instanceCreator, Consumer<T>... modifiers) {
        T result = instanceCreator.get();
        result.setModel(parent.getModel());
        parent.addChilds(result);
        result.setParent(parent);
        return applyModifiers(result, modifiers);
    }

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

    public MDependency createDependency(String targetFQName) {
        MDependency dep = new MDependency();
        dep.setParent(this);
        dep.setModel(this.getModel());
        dep.setTarget(targetFQName);
        getChilds().add(dep);

        return dep;
    }

    public static <T> T applyModifiers(T obj, Consumer<T>... modifiers) {
        if( modifiers==null || modifiers.length==0 ) {
            return obj;
        }
        for( Consumer<T> modifier : modifiers ) {
            modifier.accept(obj);
        }
        return obj;
    }
}
