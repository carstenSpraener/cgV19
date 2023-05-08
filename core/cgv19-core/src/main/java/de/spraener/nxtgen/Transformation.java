package de.spraener.nxtgen;

import de.spraener.nxtgen.model.ModelElement;

/**
 * <p>
 * A Transformation holds the logic to do a specific operation on a model. The generator
 * will call a transformation on each model element.
 * </p>
 * <p>
 * Such an operation can be adding new classes to the model depending on the ModelElement
 * given to transformation. Or adding default values to unsepcified variables.
 * </p>
 * <p>
 *     Transformations play hand in hand with the code generation. If you feel the templates getting to
 *     complex consider applying a transformation to simplify template implementation.
 * </p>
 * <p>
 *     The order of execution can ba very important. The transformations of a cartridge are executed
 *     in exact the order as they are listed by the cartridge.
 * </p>
 * <p>
 *     Every cartridge runs on a new instance of Model. But the modified model as transformed by transformation 1
 *     is given to transformation 2 inside the same cartridge.
 * </p>
 */
public interface Transformation {
    /**
     * Do the transformation on the given ModelElement. If you need access to the root of the model
     * you can use element.getModel()
     * @see ModelElement
     * @param element the ModelELement to (envetually) the transformation on.
     */
    void doTransformation(ModelElement element);
}
