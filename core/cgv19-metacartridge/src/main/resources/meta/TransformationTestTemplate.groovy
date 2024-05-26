import de.spraener.nxtgen.ProtectionStrategieDefaultImpl
import de.spraener.nxtgen.cartridge.meta.MetaStereotypes
import de.spraener.nxtgen.oom.cartridge.GeneratorGapTransformation
import de.spraener.nxtgen.oom.model.MClass
import de.spraener.nxtgen.oom.model.OOModel

MClass mClass = this.getProperty("modelElement");
MClass orgClass = GeneratorGapTransformation.getOriginalClass(mClass);

String reqStereotype = orgClass.getTaggedValue(MetaStereotypes.CODEGENERATOR.name, "requiredStereotype");
String generatesOn = orgClass.getTaggedValue(MetaStereotypes.CODEGENERATOR.name, "generatesOn");

OOModel model = mClass.getModel();


"""//${ProtectionStrategieDefaultImpl.GENERATED_LINE}
package ${mClass.getPackage().getFQName()};

import de.spraener.nxtgen.Transformation;
import de.spraener.nxtgen.model.ModelElement;
import de.spraener.nxtgen.oom.OOModelBuilder;
import de.spraener.nxtgen.oom.model.OOModel;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Disabled;

import static org.junit.jupiter.api.Assertions.*;

public class ${mClass.getName()} {

    private OOModel model;
    private ModelElement testME;
    private ${orgClass.getName()} uut = new ${orgClass.getName()}();

    @BeforeEach
    void setup() {
        model = OOModelBuilder.createModel(
            m -> OOModelBuilder.createPackage(m, "my.test.pkg",
                pkg -> OOModelBuilder.createMClass(pkg, "MyTestClass",
                        c -> OOModelBuilder.createStereotype(c, "${reqStereotype}")
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
"""
