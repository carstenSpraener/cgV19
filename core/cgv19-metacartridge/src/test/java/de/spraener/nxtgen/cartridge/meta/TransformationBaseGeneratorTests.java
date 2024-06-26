package de.spraener.nxtgen.cartridge.meta;

import de.spraener.nxtgen.model.impl.StereotypeImpl;
import de.spraener.nxtgen.oom.model.MClass;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

public class TransformationBaseGeneratorTests extends AbstractOOModelTest {
    public static final String TR_BASE_START = """
            ///THIS FILE IS GENERATED AS LONG AS THIS LINE EXISTS
            package pkg;
                        
            import de.spraener.nxtgen.model.ModelElement;
            import de.spraener.nxtgen.oom.model.*;
            import de.spraener.nxtgen.oom.StereotypeHelper;
            import de.spraener.nxtgen.oom.model.*;
            import de.spraener.nxtgen.annotations.*;
            
            /*            
            @CGV19Transformation(
                    requiredStereotype = "null",
                    operatesOn = {{MODEL_ELEMENT}}
            )
            */
            public abstract class ATransformation implements de.spraener.nxtgen.Transformation {
                        
                @Override
                public void doTransformation(ModelElement element) {
            """;

    private TransformationBaseGenerator uut = new TransformationBaseGenerator();
    private StereotypeImpl stGen = new StereotypeImpl(MetaCartridge.STEREOTYPE_TRANSFORMATION+"Base");

    @BeforeEach
    public void setup() {
        super.setup();
    }


    @Test
    public void testTransformationGeneratorOnMClass() throws Exception {
        stGen.setTaggedValue("transformedMetaType", "MClass");
        MClass gen = oomObjectMother.createClass("ATransformation",
                c -> c.addStereotypes(stGen)
        );

        String code = uut.resolve(gen, "").toCode();
        assertThat(code)
                .contains(TR_BASE_START.replace("{{MODEL_ELEMENT}}", "MClass.class"))
                .contains("""
                                if( !(element instanceof MClass) ) {
                                    return;
                                }
                                doTransformationIntern((MClass)element);
                        """)
                .contains("    public abstract void doTransformationIntern(MClass modelElement);")
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
                .contains(TR_BASE_START.replace("{{MODEL_ELEMENT}}", "MOperation.class"))
                .contains("""
                                if( !(element instanceof MOperation) ) {
                                    return;
                                }
                                doTransformationIntern((MOperation)element);
                        """)
                .contains("    public abstract void doTransformationIntern(MOperation modelElement);")
        ;
    }


    @Test
    public void testTransformationGeneratorOUndefined() throws Exception {
        MClass gen = oomObjectMother.createClass("ATransformation",
                c -> c.addStereotypes(stGen)
        );

        String code = uut.resolve(gen, "").toCode();
        assertThat(code)
                .contains(TR_BASE_START.replace("{{MODEL_ELEMENT}}", "ModelElement.class"))
                .contains("""
                                if( !(element instanceof ModelElement) ) {
                                    return;
                                }
                                doTransformationIntern((ModelElement)element);
                        """)
                .contains("    public abstract void doTransformationIntern(ModelElement modelElement);")
        ;
    }

}
