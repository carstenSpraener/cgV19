package de.csp.fsm;

public interface FSMStateStorage<T,K> {
    K storeSate(FSMContextImpl<T> context);
    FSMContextImpl<T> loadState(K key);
}
