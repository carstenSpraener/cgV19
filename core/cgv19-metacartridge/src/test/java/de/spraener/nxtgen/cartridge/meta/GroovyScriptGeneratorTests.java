package de.spraener.nxtgen.cartridge.meta;

import de.spraener.nxtgen.GroovyCodeBlockImpl;
import de.spraener.nxtgen.model.impl.StereotypeImpl;
import de.spraener.nxtgen.oom.model.MClass;
import org.junit.Before;
import org.junit.Test;

import java.io.File;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class GroovyScriptGeneratorTests extends AbstractOOModelTest {
    private GroovyScriptGenerator uut = new GroovyScriptGenerator();
    private StereotypeImpl stGen = new StereotypeImpl(MetaCartridge.STYPE_GROOVY_SCRIPT);

    @Before
    public void setup() {
        super.setup();
        stGen.setTaggedValue(MetaCartridge.TV_SCRIPT_FILE, "/Template.groovy");
    }

    @Test
    public void testGroovyScriptGeneratorDefaults() throws Exception {
        MClass gen = oomObjectMother.createClass("AGroovyScript",
                c -> c.addStereotypes(stGen)
        );

        String code = uut.resolve(gen, "").toCode();
        assertThat(code)
                .contains("""
                        // THIS FILE IS GENERATED AS LONG AS THIS LINE EXISTS
                        import de.spraener.nxtgen.ProtectionStrategieDefaultImpl
                        import de.spraener.nxtgen.oom.model.MClass
                        import de.spraener.nxtgen.oom.model.OOModel
                                                
                        MClass mClass = this.getProperty("modelElement");
                        OOModel model = mClass.getModel();
                                                
                                                
                        ""\"//${ProtectionStrategieDefaultImpl.GENERATED_LINE}
                        package ${mClass.getPackage().getFQName()};
                                                
                        public class ${mClass.getName()} {
                        }
                        ""\"                 
                        """);
    }

    @Test
    public void testGeneratorFileStrategy() throws Exception {
        MClass gen = oomObjectMother.createClass("AGroovyScript",
                c -> c.addStereotypes(stGen)
        );

        GroovyCodeBlockImpl gcb = (GroovyCodeBlockImpl) uut.resolve(gen, "");
        File f = gcb.getToFileStrategy().open();
        assertTrue(f.getAbsolutePath().endsWith("src/main/resources/Template.groovy"));
    }
}
