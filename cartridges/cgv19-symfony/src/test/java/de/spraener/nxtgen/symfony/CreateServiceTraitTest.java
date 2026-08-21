//THIS FILE IS GENERATED AS LONG AS THIS LINE EXISTS
package de.spraener.nxtgen.symfony;

import de.spraener.nxtgen.Transformation;
import de.spraener.nxtgen.model.ModelElement;
import de.spraener.nxtgen.oom.OOModelBuilder;
import de.spraener.nxtgen.oom.model.OOModel;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Disabled;

import static org.junit.jupiter.api.Assertions.*;

public class CreateServiceTraitTest {

    private OOModel model;
    private ModelElement testME;
    private CreateServiceTrait uut = new CreateServiceTrait();

    @BeforeEach
    void setup() {
        model = OOModelBuilder.createModel(
            m -> OOModelBuilder.createPackage(m, "my.test.pkg",
                pkg -> OOModelBuilder.createMClass(pkg, "MyTestClass",
                        c -> OOModelBuilder.createStereotype(c, "null")
                )
            )
        );
        testME = model.findClassByName("my.test.pkg.MyTestClass");
    }

    @Test
    @Disabled("TODO: Not implemented yet.")
    void testTransformation() throws Exception {
        // given: TestModel as defined in setup

        // when: Calling Transformation on a matched ModelElement
        // uut.doTransformation(this.testME);

        // then: Transformation should create...
        // TODO: Assert the transformations results
        // assertNotNull(model.findClassByName("my.test.pkg.MyTestClass"));
    }
}
