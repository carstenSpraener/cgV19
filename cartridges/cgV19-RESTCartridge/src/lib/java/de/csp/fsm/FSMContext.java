package de.csp.fsm;

public interface FSMContext<T> {
    FSMContext put(String key, Object value);
    Object get(String key);
    T getRootObject();
}
