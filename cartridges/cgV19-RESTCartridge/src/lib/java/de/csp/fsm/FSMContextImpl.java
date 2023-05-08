package de.csp.fsm;

import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;

public class FSMContextImpl<T> implements FSMContext<T> {
    private Map<String,Object> valueMap = new HashMap<>();
    private Class<?> fsmClass;
    private T rootObject;
    private Method returnFromMethod;
    private boolean interrupted;

    FSMContextImpl(Class<?> fsmClass, T rootObject) {
        this.fsmClass = fsmClass;
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

    public Class<?> getFsmClass() {
        return fsmClass;
    }

    public Method getReturnFromMethod() {
        return returnFromMethod;
    }

    public void setReturnFromMethod(Method returnFromMethod) {
        this.returnFromMethod = returnFromMethod;
    }

    public boolean isInterrupted() {
        return interrupted;
    }

    public void setInterrupted(boolean interrupted) {
        this.interrupted = interrupted;
    }
}
