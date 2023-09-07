package de.spraener.nxtgen.laravel;

import de.spraener.nxtgen.model.ModelElement;
import de.spraener.nxtgen.model.ModelHelper;
import de.spraener.nxtgen.oom.OOModelBuilder;
import de.spraener.nxtgen.oom.model.*;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Disabled;

import de.spraener.nxtgen.laravel.OOModelMother;

import static org.assertj.core.api.Assertions.assertThat;

public class ModelGeneratorTest {
    private ModelGenerator uut = new ModelGenerator();

    private OOModel model;
    private MClass testME;
    
    @BeforeEach
    void setup() {
        // Create the required model elements with the OOModelBuilder
        model = OOModelMother.createDefaultModel();
        testME = OOModelMother.getEvent(model);
        new CreateModelInfrastructureTransformation().doTransformation(testME);

    }
    
    @Test
    void testCodeGenerator() throws Exception {
        // Generate the Code
        MClass eventModel = this.model.getClassesByStereotype(LaravelStereotypes.LARAVELMODEL.getName()).get(0);
        String code = uut.resolve(eventModel, "").toCode();
        
        // Check the generated code to contain...
        assertThat(code)
                .startsWith("<?php")
                .contains("namespace App\\Models\\Events;")
                .contains("protected $fillable = [")
                .contains("'name',")
                .contains("'abstract',")
                .contains("'description',")
                .contains("'start_ts',")
                .contains("'end_ts',")
        ;
    }
    
    // TODO: Add additional test cases here
}
