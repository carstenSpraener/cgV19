package de.csp.fsm;

import java.util.HashMap;
import java.util.Map;

public class FSMContextImpl<T> implements FSMContext<T> {
    private Map<String,Object> valueMap = new HashMap<>();
    private T rootObject;

    FSMContextImpl(T rootObject) {
        this.rootObject = rootObject;
    }

    @Override
    public FSMContext put(String key, Object value) {
        this.valueMap.put(key, value);
        return this;
    }

    @Override
    public Object get(String key) {
        return this.valueMap.get(key);
    }

    Iterable<Map.Entry<String,Object>> listEntries() {
        return this.valueMap.entrySet();
    }

    @Override
    public T getRootObject() {
        return rootObject;
    }
}
