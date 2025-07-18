package de.spraener.nxtgen.oom.cartridge;

import de.spraener.nxtgen.model.ModelElement;
import de.spraener.nxtgen.model.ModelHelper;
import de.spraener.nxtgen.oom.OOModelBuilder;
import de.spraener.nxtgen.oom.model.MPackage;
import de.spraener.nxtgen.oom.model.OOModel;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

public class MElementGeneratorTest {
    private MElementGenerator uut = new MElementGenerator();
    private OOMMetaCartridge cartridge = new OOMMetaCartridge();

    private OOModel model;
    private ModelElement testME;

    @BeforeEach
    void setup() {
        // Create the required model elements with the OOModelBuilder
        model = OOModelMother.createDefaultModel();
        MPackage pkg = (MPackage)ModelHelper.findByFQName(model,"my.test.model", ".");
        OOModelBuilder.createMClass(pkg, "MyTestClass",
                  c -> OOModelBuilder.createStereotype(c, "MElement")
        );
        OOModelMother.runTransformations(cartridge, model);
        testME = model.findClassByName("my.test.model.MyTestClass");
    }

    @Test
    void testCodeGenerator() throws Exception {
        // Generate the Code
        String code = uut.resolve(testME, "").toCode();

        // Check the generated code to contain...
        assertThat(code)
                .contains("public class MyTestClass extends my.test.model.MyTestClassBase {")
        ;
    }

    // TODO: Add additional test cases here
}
