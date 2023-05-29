package de.spraener.nxtgen.oom.model;

import de.spraener.nxtgen.model.ModelElement;
import de.spraener.nxtgen.model.impl.ModelElementImpl;

import java.util.Map;
import java.util.HashMap;

public class OOModelRepository {
    private static OOModelRepository instance = new OOModelRepository();

    private Map<String,ModelElementImpl> repository = new HashMap<>();
    private OOModelRepository() {}

    public static OOModelRepository getInstance() {
        return instance;
    }

    public OOModelRepository put(String key, ModelElementImpl element ) {
        this.repository.put(key,element);
        return this;
    }

    public ModelElementImpl get(String key) {
        return this.repository.get(key);
    }

    public OOModelRepository reset() {
        this.repository.clear();
        return this;
    }
}
