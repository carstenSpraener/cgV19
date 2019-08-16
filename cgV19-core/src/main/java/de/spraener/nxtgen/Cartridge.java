package de.spraener.nxtgen;

import de.spraener.nxtgen.model.Model;

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
    String getName();
    List<Transformation> getTransformations();
    List<CodeGeneratorMapping> mapGenerators(Model m);
}
