package de.spraener.nxtgen.cartridge.meta;

import de.spraener.nxtgen.model.impl.StereotypeImpl;
import de.spraener.nxtgen.oom.model.MClass;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

public class TransformationGeneratorTests extends AbstractOOModelTest {
    public static final String TRANSFORMATION_START = """
            ///THIS FILE IS GENERATED AS LONG AS THIS LINE EXISTS
            package pkg;
                        
            import de.spraener.nxtgen.oom.model.*;
            import de.spraener.nxtgen.oom.StereotypeHelper;
                        
            public class ATransformation extends ATransformationBase {
                        
                @Override
            """;

    public static final String TRANSFORMATION_END = """
                    //TODO: Implement this transformation
                }
            }
            """;
    private TransformationGenerator uut = new TransformationGenerator();
    private StereotypeImpl stGen = new StereotypeImpl(MetaStereotypes.TRANSFORMATION.getName());

    @BeforeEach
    public void setup() {
        super.setup();
    }


    @Test
    public void testTransformationGeneratorDefaults() throws Exception {
        stGen.setTaggedValue("transformedMetaType", "MClass");
        MClass gen = oomObjectMother.createClass("ATransformation",
                c -> c.addStereotypes(stGen)
        );

        String code = uut.resolve(gen, "").toCode();
        assertThat(code)
                .contains(TRANSFORMATION_START)
                .contains("    public void doTransformationIntern(MClass me) {")
                .contains(TRANSFORMATION_END)
        ;
    }

    @Test
    public void testTransformationGeneratorOnMOperation() throws Exception {
        stGen.setTaggedValue("transformedMetaType", "MOperation");
        MClass gen = oomObjectMother.createClass("ATransformation",
                c -> c.addStereotypes(stGen)
        );

        String code = uut.resolve(gen, "").toCode();
        assertThat(code)
                .contains("    public void doTransformationIntern(MOperation me) {");
        ;
    }

    @Test
    public void testTransformationGeneratorOnUndefined() throws Exception {
        MClass gen = oomObjectMother.createClass("ATransformation",
                c -> c.addStereotypes(stGen)
        );

        String code = uut.resolve(gen, "").toCode();
        assertThat(code)
                .contains("    public void doTransformationIntern(ModelElement me) {");
        ;
    }
}
