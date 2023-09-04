package de.spraener.nxtgen.cartridge.meta;

import de.spraener.nxtgen.*;
import de.spraener.nxtgen.model.impl.StereotypeImpl;
import de.spraener.nxtgen.oom.model.MClass;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;

import java.io.File;

import static org.junit.jupiter.api.Assertions.*;

public class MetaCartridgeGeneratorMappingTests extends AbstractOOModelTest {
    private MetaCartridge uut = new MetaCartridge();

    @Test
    public void testStereotypeDocGeneratorMapping() throws Exception {
        MClass sTYpe = oomObjectMother.createClass("AStype",
                c -> c.addStereotypes(new StereotypeImpl("Stereotype")
                )
        );

        var mappings = uut.mapGenerators(this.model);

        // FIXME: Annotated generators a mapped twice!
        //  assertEquals(1, mappings.size());
        CodeGenerator codeGen = mappings.get(0).getCodeGen();
        assertTrue( codeGen instanceof StereotypeDocGenerator );
        CodeBlock cBlock = codeGen.resolve(sTYpe,"");
        assertTrue( cBlock instanceof GroovyCodeBlockImpl);
        GroovyCodeBlockImpl gcb = (GroovyCodeBlockImpl) cBlock;
        assertTrue(gcb.getToFileStrategy() instanceof DocumentationOutputFileStrategy );
    }

    @Test
    public void testServiceLocatorGeneratorMapping() throws Exception {
        MClass sType = oomObjectMother.createClass("AServiceLocater",
                c -> c.addStereotypes(new StereotypeImpl(MetaCartridge.STYPE_CGV19CARTRIDGE_SERVICE_DEFINITION)
                )
        );
        var mappings = uut.mapGenerators(this.model);
        CodeGeneratorMapping mapping = mappings.get(0);
        CodeBlockImpl cBlock = (CodeBlockImpl) mapping.getCodeGen().resolve(sType, "");
        File f = cBlock.getToFileStrategy().open();
        assertTrue(f.getAbsolutePath().endsWith("src/main/resources/META-INF/services/de.spraener.nxtgen.Cartridge"));
    }

    @Test
    public void testDefaultMappings() {
        assertDefaultMappings(uut);
    }

    public static void assertDefaultMappings(Cartridge uut) {
        // FIXME: Annotated generators are mapped twice.
        assertDefaultMapping(uut, "cgV19CartridgeBase", 2);
        assertDefaultMapping(uut, "GroovyScript", 2);
        assertDefaultMapping(uut, "Transformation", 2);
        assertDefaultMapping(uut, "cgV19Cartridge", 2);
        assertDefaultMapping(uut, "cgV19CartridgeServiceDefinition", 2);
        assertDefaultMapping(uut, "StereotypeEnum", 2);
        assertDefaultMapping(uut, "Stereotype", 2);
        assertDefaultMapping(uut, "CodeGenerator", 2);
    }

    public static void assertDefaultMapping(Cartridge uut, String stereotype, int nofMappingsExpected) {
        OOMetaModelObjectMother objectMother = new OOMetaModelObjectMother();
        objectMother.createDefaultOOModel();
        objectMother.createClass("Test"+stereotype+"Mapping",
            c -> c.addStereotypes(new StereotypeImpl(stereotype))
        );
        var mappings = uut.mapGenerators(objectMother.getModel());
        assertEquals( nofMappingsExpected, mappings.size() );
        for( int i=0; i<nofMappingsExpected; i++ ) {
            CodeGenerator codeGen = mappings.get(i).getCodeGen();
            assertNotNull(codeGen);
        }
    }

    @Test
    public void testTransformationListing() throws Exception {
        MetaCartridge mc = new MetaCartridge();
        Assertions.assertThat(mc.getTransformations())
                .map(t -> t.getClass().getSimpleName() )
                .containsSequence(
                        "RemoveModelRootPackage"
                        )
                .contains(
                        "EnsureGeneratorDefinitionsTransformation",
                        "EnsureTransformationDefinitionsTransformation")
                .contains(
                        "CartridgeServicesLocatorTransformation",
                        "CartridgeBaseForCartridgeTransformation"
                );
    }
}
