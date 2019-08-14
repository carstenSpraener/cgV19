package de.csp.nxtgen;

import de.csp.nxtgen.model.Model;

public interface ModelLoader {
    boolean canHandle(String modelURI);
    Model loadModel(String modelURI);
}
