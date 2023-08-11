package de.spraener.nxtgen;

import de.spraener.nxtgen.model.Model;
import de.spraener.nxtgen.model.ModelElement;
import de.spraener.nxtgen.model.Stereotype;

import java.util.List;

/**
 * A Cartridge is a set of Generators on a Model that is responsible for generating
 * a certain set of classes. This set typically implements a abstract functionality.
 * For example a Cartridge can implement the generation of Entity-Classes along with
 * it's DDL.
 * Another Cartridge can implement a REST-Controller for some of these Entities.
 *
 * @see Transformation
 * @see CodeGenerator
 */
public interface Cartridge {
    /**
     * The nane of the cartridge. For logging purpose.
     *
     * @return The name of this cartridge
     */
    String getName();

    /**
     * <p>
     * Returns a list of Transformations to be executed before the generators are mapped. NextGen
     * will execute all Transformations of this cartridge on the same instance of a model. But each
     * cartridge will start with a brand new model instance.
     * </p>
     * <p>
     *     The Transformations are executed in exact the order they are listed here.
     * </p>
     * @see Transformation
     *
     * @return a (maybe empty) list of Transformations to be executed.
     */
    List<Transformation> getTransformations();

    /**
     * Do a Mapping of ModelElements inside the Model to CodeGeneratos. This method is called
     * after all transformations have been executed.
     *
     * @param m
     * @return
     */
    List<CodeGeneratorMapping> mapGenerators(Model m);

    /**
     * Optional Method that enables other cartridges to evaluate a specific code block
     * from this cartridge.
     * @param m the Model to be used for evaluation
     * @param me the ModelElement to be evaluated
     * @param sType One of the ModelElements stereotype to be used for evaluation
     * @param aspect an optional (maybe null) aspect to narrowing the desired evaluation
     *
     * @return A String containing block of code as the result of the evaluation.
     */
    default String evaluate(Model m, ModelElement me, Stereotype sType, String aspect ) {
        return "This cartridge does not support evaluation.";
    }
}
