import de.spraener.nxtgen.ProtectionStrategieDefaultImpl
import de.spraener.nxtgen.cartridge.meta.EnsureGeneratorDefinitionsTransformation
import de.spraener.nxtgen.cartridge.meta.MetaStereotypes
import de.spraener.nxtgen.oom.cartridge.GeneratorGapTransformation
import de.spraener.nxtgen.oom.model.MClass
import de.spraener.nxtgen.oom.model.OOModel

MClass mClass = this.getProperty("modelElement");
MClass orgClass = GeneratorGapTransformation.getOriginalClass(mClass);
MClass ooModelMother = EnsureGeneratorDefinitionsTransformation.getOOModelMother(mClass);

String reqStereotype = orgClass.getTaggedValue(MetaStereotypes.CODEGENERATOR.name, "requiredStereotype");
String generatesOn = orgClass.getTaggedValue(MetaStereotypes.CODEGENERATOR.name, "generatesOn");

OOModel model = mClass.getModel();


"""//${ProtectionStrategieDefaultImpl.GENERATED_LINE}
package ${mClass.getPackage().getFQName()};

import de.spraener.nxtgen.model.ModelElement;
import de.spraener.nxtgen.model.ModelHelper;
import de.spraener.nxtgen.oom.OOModelBuilder;
import de.spraener.nxtgen.oom.model.*;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Disabled;

import ${ooModelMother.getFQName()};

public class ${mClass.getName()} {
    private ${orgClass.getName()} uut = new ${orgClass.getName()}();

    private OOModel model;
    private ModelElement testME;
    
    @BeforeEach
    void setup() {
        // Create the required model elements with the OOModelBuilder
        model = OOModelMother.createDefaultModel();
        MPackage pkg = (MPackage)ModelHelper.findByFQName(model,"my.test.model", ".");
        OOModelBuilder.createMClass(pkg, "MyTestClass",
                  c -> OOModelBuilder.createStereotype(c, "${reqStereotype}")
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
"""
