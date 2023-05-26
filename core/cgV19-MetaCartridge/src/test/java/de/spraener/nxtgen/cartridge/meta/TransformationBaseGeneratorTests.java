package de.spraener.nxtgen.cartridge.meta;

import de.spraener.nxtgen.model.impl.StereotypeImpl;
import de.spraener.nxtgen.oom.model.MClass;
import org.junit.Before;
import org.junit.Test;

import static org.assertj.core.api.Assertions.assertThat;

public class TransformationBaseGeneratorTests extends AbstractOOModelTest {
    public static final String TR_BASE_START = """
            ///THIS FILE IS GENERATED AS LONG AS THIS LINE EXISTS
            package pkg;
                        
            import de.spraener.nxtgen.model.ModelElement;
            import de.spraener.nxtgen.oom.model.*;
            import de.spraener.nxtgen.oom.StereotypeHelper;
                        
            public abstract class ATransformationBase implements de.spraener.nxtgen.Transformation {
                        
                @Override
                public void doTransformation(ModelElement element) {
            """;

    private TransformationBaseGenerator uut = new TransformationBaseGenerator();
    private StereotypeImpl stGen = new StereotypeImpl(MetaCartridge.STEREOTYPE_TRANSFORMATION);

    @Before
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
                .contains(TR_BASE_START)
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
                .contains(TR_BASE_START)
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
                .contains(TR_BASE_START)
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
