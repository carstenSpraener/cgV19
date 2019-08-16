package de.spraener.nxtgen;

import de.spraener.nxtgen.model.Model;

public interface ModelLoader {
    boolean canHandle(String modelURI);
    Model loadModel(String modelURI);
}
