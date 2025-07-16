package de.spreaner.nxtgen.annoproc;

import de.spraener.nxtgen.ModelLoader;
import de.spraener.nxtgen.model.Model;

public class AnnotationModelLoader implements ModelLoader {
    public static final String CGV_19_AP_MODELNAME = "cgv19:AnnotationProcessor";
    private final Model actualModel;

    public AnnotationModelLoader(Model actualModel) {
        this.actualModel = actualModel;
    }

    @Override
    public boolean canHandle(String modelURI) {
        return CGV_19_AP_MODELNAME.equals(modelURI);
    }

    @Override
    public Model loadModel(String modelURI) {
        return actualModel;
    }

}
