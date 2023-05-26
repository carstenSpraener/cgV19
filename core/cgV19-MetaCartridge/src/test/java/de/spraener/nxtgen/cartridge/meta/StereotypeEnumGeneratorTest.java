package de.spraener.nxtgen.cartridge.meta;

import de.spraener.nxtgen.model.impl.StereotypeImpl;
import de.spraener.nxtgen.oom.model.MClass;
import org.junit.Before;
import org.junit.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.Assert.assertEquals;

public class StereotypeEnumGeneratorTest extends AbstractOOModelTest {
    private static final String ENUM_IMPL = """
            // THIS FILE IS GENERATED AS LONG AS THIS LINE EXISTS
            package pkg;
                        
            public enum AGroovyScript {
                    ASTEREOTYPE("AStereoType")
                ;
                        
                private String name;
                        
                AGroovyScript(String name) {
                    this.name = name;
                }
                        
                public String toString() {
                    return this.name;
                }
                        
                public String getName() {
                    return this.name;
                }
            }
            """;
    private StereotypeEnumGenerator uut = new StereotypeEnumGenerator();
    private StereotypeImpl stGen = new StereotypeImpl(MetaCartridge.STEREOTYPE_CODE_GENERATOR);

    @Before
    public void setup() {
        super.setup();
        stGen.setTaggedValue("generatesOn", "MClass");
        stGen.setTaggedValue(MetaCartridge.TV_REQUIRED_STEREOTYPE, "AStereoType");
        stGen.setTaggedValue("templateScript", "/Template.groovy");
        stGen.setTaggedValue("outputTo", "src-gen");
    }

    @Test
    public void testStereotypeEnumGeneratorDefaults() throws Exception {

        MClass sType = oomObjectMother.createClass("AStereoType",
                c -> c.addStereotypes(new StereotypeImpl(MetaStereotypes.STEREOTYPE.getName()))
        );
        MClass gen = oomObjectMother.createClass("AGroovyScript",
                c -> c.addStereotypes(stGen)
        );

        String code = uut.resolve(gen, "").toCode();
        assertThat(code)
                .contains(ENUM_IMPL)
        ;
    }

    @Test
    public void testNameEqualsToString() {
        assertEquals(MetaStereotypes.STEREOTYPE.getName(), MetaStereotypes.STEREOTYPE.toString());
    }
}
