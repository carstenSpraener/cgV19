package de.spraener.nxtgen.cartridge.meta;

import de.spraener.nxtgen.model.impl.StereotypeImpl;
import de.spraener.nxtgen.oom.StereotypeHelper;
import de.spraener.nxtgen.oom.model.MClass;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

public class CartridgeBaseGeneratorTests extends AbstractOOModelTest {

    private static final String CLASS_STANDARDS = """
            //THIS FILE IS GENERATED AS LONG AS THIS LINE EXISTS
            package pkg;
                        
            import de.spraener.nxtgen.cartridges.AnnotatedCartridgeImpl;
            import de.spraener.nxtgen.CodeGeneratorMapping;
            import de.spraener.nxtgen.Transformation;
            import de.spraener.nxtgen.model.Model;
            import de.spraener.nxtgen.model.ModelElement;
            import de.spraener.nxtgen.oom.StereotypeHelper;
            import de.spraener.nxtgen.oom.model.*;
                        
            import java.util.List;
            import java.util.ArrayList;
                        
            public class ACgv19Cartridge extends AnnotatedCartridgeImpl {
                        
                @Override
                public String getName() {
                    return "ACgv19Cartridge";
                }
            """;
    public static final String OVERWRITEABLE_MAPPING_METHOD = """
    /**
     * Use this method to override default mappings. Return null for default mapping.
     */
    protected CodeGeneratorMapping createMapping(ModelElement me, String stereotypeName) {
        return null;
    }
""";
    public static final String EMPTY_TRANSFORMATION_MAPPING = """
                @Override
                public List<Transformation> getTransformations() {
                    List<Transformation> result = super.getTransformations();
                        
                    return result;
                }
            """;
    public static final String GENERATOR_MAPPING_START = """
                @Override
                public List<CodeGeneratorMapping> mapGenerators(Model m) {
                    List<CodeGeneratorMapping> result = new ArrayList<>();
                    for( ModelElement me : m.getModelElements() ) {
            """;
    public static final String GENERATOR_MAPPING_END = """
                        
                    }
            
                    return result;
                }
            """;

    private CartridgeBaseGenerator uut = new CartridgeBaseGenerator();
    private StereotypeImpl stGen = new StereotypeImpl(MetaCartridge.STEREOTYPE_CODE_GENERATOR);

    @BeforeEach
    public void setup() {
        super.setup();
        stGen.setTaggedValue("generatesOn", "MClass");
        stGen.setTaggedValue(MetaCartridge.TV_REQUIRED_STEREOTYPE, "AStereoType");
        stGen.setTaggedValue("templateScript", "/Template.groovy");
        stGen.setTaggedValue("outputTo", "src-gen");
    }

    @Test
    public void testCartridgeBaseGeneratorDefaults() {
        MClass gen = oomObjectMother.createClass("ACgv19Cartridge",
                c -> c.addStereotypes(stGen)
        );

        String code = uut.resolve(gen, "").toCode();
        assertThat(code)
                .contains(CLASS_STANDARDS)
                .contains(EMPTY_TRANSFORMATION_MAPPING)
                .contains(GENERATOR_MAPPING_START)
                .contains(GENERATOR_MAPPING_END)
                .contains(OVERWRITEABLE_MAPPING_METHOD)
                .contains("""
                                    if( StereotypeHelper.hasStereotype(me, "AStereoType") ) {
                                        CodeGeneratorMapping mapping = null;
                                        if( me instanceof MClass tME ) {
                                            mapping = createMapping(tME, "AStereoType");
                                            if (mapping == null) {
                                                mapping = CodeGeneratorMapping.create(me, new pkg.ACgv19Cartridge());
                                            }
                                            mapping.setStereotype("AStereoType");
                                            result.add(mapping);
                                        }
                                    }
                        """)
        ;
    }

    @Test
    public void testTransformationListing() throws Exception {
        MClass t1 = oomObjectMother.createClass("ATransformation1",
                c -> c.addStereotypes(new StereotypeImpl("Transformation")),
                c -> StereotypeHelper.getStereotype(c, "Transformation").setTaggedValue("transformedMetaType", "MClass"),
                c -> StereotypeHelper.getStereotype(c, "Transformation").setTaggedValue("requiredStereotype", "AStereoType"),
                c -> StereotypeHelper.getStereotype(c, "Transformation").setTaggedValue("priority", "10")
        );
        MClass t2 = oomObjectMother.createClass("ATransformation2",
                c -> c.addStereotypes(new StereotypeImpl("Transformation")),
                c -> StereotypeHelper.getStereotype(c, "Transformation").setTaggedValue("transformedMetaType", "MClass"),
                c -> StereotypeHelper.getStereotype(c, "Transformation").setTaggedValue("requiredStereotype", "AStereoType"),
                c -> StereotypeHelper.getStereotype(c, "Transformation").setTaggedValue("priority", "5")
        );
        MClass t3 = oomObjectMother.createClass("ATransformationNix",
                c -> c.addStereotypes(new StereotypeImpl("Transformation")),
                c -> StereotypeHelper.getStereotype(c, "Transformation").setTaggedValue("transformedMetaType", "MClass"),
                c -> StereotypeHelper.getStereotype(c, "Transformation").setTaggedValue("requiredStereotype", "AStereoType")
        );
        MClass gen = oomObjectMother.createClass("ACgv19Cartridge",
                c -> c.addStereotypes(stGen)
        );

        EnsureTransformationDefinitionsTransformation t = new EnsureTransformationDefinitionsTransformation();
        t.doTransformation(t3);

        String code = uut.resolve(gen, "").toCode();

        assertThat(code)
                .contains("""
                                result.add( new pkg.ATransformation2() );
                                result.add( new pkg.ATransformation1() );
                                result.add( new pkg.ATransformationNix() );
                        """)
        ;
    }
}
