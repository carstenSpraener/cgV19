package de.spraener.nxtgen.laravel;

import de.spraener.nxtgen.model.ModelElement;
import de.spraener.nxtgen.oom.ModelHelper;
import de.spraener.nxtgen.oom.OOModelBuilder;
import de.spraener.nxtgen.oom.model.*;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Disabled;

import de.spraener.nxtgen.laravel.OOModelMother;

import static org.assertj.core.api.Assertions.assertThat;

public class FactoryGeneratorTest {
    private FactoryGenerator uut = new FactoryGenerator();

    private OOModel model;
    private MClass testME;
    
    @BeforeEach
    void setup() {
        // Create the required model elements with the OOModelBuilder
        model = OOModelMother.createDefaultModel();
        new CreateModelInfrastructureTransformation().doTransformationIntern(OOModelMother.getEvent(model));
        testME = model.findClassByName("my.test.model.app.models.events.EventFactory");
    }
    
    @Test
    void testCodeGenerator() throws Exception {
        // Generate the Code
        String code = uut.resolve(testME, "").toCode();
        
        // Check the generated code to contain...

        assertThat(code)
                .contains("class EventFactory extends Factory")
        ;
    }
}
