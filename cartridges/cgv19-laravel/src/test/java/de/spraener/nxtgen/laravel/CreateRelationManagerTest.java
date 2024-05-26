package de.spraener.nxtgen.laravel;

import de.spraener.nxtgen.Transformation;
import de.spraener.nxtgen.cartridge.rest.RESTStereotypes;
import de.spraener.nxtgen.model.ModelElement;
import de.spraener.nxtgen.oom.OOModelBuilder;
import de.spraener.nxtgen.oom.model.MClass;
import de.spraener.nxtgen.oom.model.MPackage;
import de.spraener.nxtgen.oom.model.OOModel;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Disabled;

import static org.junit.jupiter.api.Assertions.*;

public class CreateRelationManagerTest {

    private OOModel model;
    private ModelElement testME;
    private CreateRelationManager uut = new CreateRelationManager();

    @BeforeEach
    void setup() {
        model = OOModelMother.createDefaultModel();
        testME = OOModelMother.getEventResourceController(model);
    }

    @Test
    void testTransformation() throws Exception {
        // given: TestModel as defined in setup

        // when: Calling Transformation on a matched ModelElement
        uut.doTransformation(this.testME);

        // then: Transformation should create...
        // TODO: Assert the transformations results
        // assertNotNull(model.findClassByName("my.test.pkg.MyTestClass"));
    }
}
