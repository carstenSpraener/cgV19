package de.spraener.nxtgen;

import de.spraener.nxtgen.model.Model;

/**
 * <p>
 *     A ModelLoader is responsible for creating an instane of Model
 * from a given URI. It is called from the NextGen whenever a new
 * instance of the model is needed. Typically every cartridge has at least
 * one ModelLoader.
 * </p>
 * <p>
 * You should not buffer the model instances. This can lead to unpredictable
 * side effects when transformations running on that model.
 * </p>
 * <p>
 * The ModelLoader is located via the java intern ServiceLoader-Mechanism. So it is possible
 * that there are more than one model loader in a generation session.
 * </p>
 * <p>
 * The ModelLoader can tell via the canHandle method if it is able to handle the requested
 * </p>
 */
public interface ModelLoader {
    /**
     * Is this instance of ModelLoader able to handle the requested model?
     *
     * @param modelURI and URI to the model. It could be a file, a URL ore any other URI
     * @return true if the ModelLoader can handle the model.
     */
    boolean canHandle(String modelURI);

    /**
     * Load the Data from the given URI and return a fresh new instance of Model.
     * @see Model
     *
     * @param modelURI
     * @return Model
     */
    Model loadModel(String modelURI);
}
