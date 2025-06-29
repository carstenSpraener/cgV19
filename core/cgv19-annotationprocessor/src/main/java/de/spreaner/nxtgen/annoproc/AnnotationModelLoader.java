package de.spreaner.nxtgen.annoproc;

import de.spraener.nxtgen.ModelLoader;
import de.spraener.nxtgen.model.Model;

public class AnnotationModelLoader implements ModelLoader {
    private final Model actualModel;

    public AnnotationModelLoader(Model actualModel) {
        this.actualModel = actualModel;
    }

    @Override
    public boolean canHandle(String modelURI) {
        return "cgv19:AnnotationProcessor".equals(modelURI);
    }

    @Override
    public Model loadModel(String modelURI) {
        return actualModel;
    }

}
