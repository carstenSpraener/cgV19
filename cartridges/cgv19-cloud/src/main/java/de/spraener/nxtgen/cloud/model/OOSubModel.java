package de.spraener.nxtgen.cloud.model;

import de.spraener.nxtgen.model.ModelElement;
import de.spraener.nxtgen.oom.model.OOModel;

import java.util.ArrayList;
import java.util.List;

public class OOSubModel extends OOModel {
    private ModelElement modelRoot;

    public OOSubModel(ModelElement modelRoot) {
        this.modelRoot = modelRoot;
    }

    @Override
    public List<ModelElement> getModelElements() {
        List<ModelElement> elements = new ArrayList<>();
        super.collectElements(modelRoot, elements);
        return elements;
    }
}
