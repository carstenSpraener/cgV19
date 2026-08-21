//THIS FILE IS GENERATED AS LONG AS THIS LINE EXISTS
package de.spraener.nxtgen.angular;

import de.spraener.nxtgen.model.ModelElement;
import de.spraener.nxtgen.model.ModelHelper;
import de.spraener.nxtgen.oom.OOModelBuilder;
import de.spraener.nxtgen.oom.model.*;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Disabled;

import de.spraener.nxtgen.angular.OOModelMother;

public class TSTypeGeneratorTest {
    private TSTypeGenerator uut = new TSTypeGenerator();

    private OOModel model;
    private ModelElement testME;
    
    @BeforeEach
    void setup() {
        // Create the required model elements with the OOModelBuilder
        model = OOModelMother.createDefaultModel();
        MPackage pkg = (MPackage)ModelHelper.findByFQName(model,"my.test.model", ".");
        OOModelBuilder.createMClass(pkg, "MyTestClass",
                  c -> OOModelBuilder.createStereotype(c, "TSType")
        );
        testME = model.findClassByName("my.test.model.MyTestClass");
        // Optionaly run Transformations
        // new ...Transformation().doTransformation(testME)
        
        // Optionaly: Relocate the testME
        // testME = model.findClassByName(...);
    }
    
    @Test
    @Disabled("TODO: Not implememted yet!")
    void testCodeGenerator() throws Exception {
        // Generate the Code
        // String code = uut.resolve(testME, "").toCode();
        
        // Check the generated code to contain...

        //assertThat(code)
        //        .contains("public class MyTestClass {")
        //;
    }
    
    // TODO: Add additional test cases here
}
