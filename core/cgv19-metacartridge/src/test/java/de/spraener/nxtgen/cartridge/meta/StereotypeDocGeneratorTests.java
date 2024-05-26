package de.spraener.nxtgen.cartridge.meta;

import de.spraener.nxtgen.CodeBlock;
import de.spraener.nxtgen.CodeBlockImpl;
import de.spraener.nxtgen.ProtectionStrategieDefaultImpl;
import de.spraener.nxtgen.model.impl.StereotypeImpl;
import de.spraener.nxtgen.oom.model.MAttribute;
import de.spraener.nxtgen.oom.model.MClass;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;

import java.io.File;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class StereotypeDocGeneratorTests extends AbstractOOModelTest {
    private StereotypeDocGenerator uut = new StereotypeDocGenerator();

    @Test
    public void testOutputFileStrategyCorrect() throws Exception {
        MClass sType = oomObjectMother.createClass("ASType",
                c -> c.addStereotypes(new StereotypeImpl(MetaStereotypes.STEREOTYPE.getName()))
        );
        sType.setProperty("documentation", "Stereotype documentation");

        var mappings = new MetaCartridge().mapGenerators(this.model);
        var mapping = mappings.get(0);
        CodeBlockImpl cp = (CodeBlockImpl)mapping.getCodeGen().resolve(sType, "");
        File fOut = cp.getToFileStrategy().open();
        assertTrue(fOut.getAbsolutePath().endsWith("doc/stereotypes/ASType.md"));
    }
    @Test
    public void testStereotypeDocumentationGeneration() throws Exception {
        MClass sType = oomObjectMother.createClass("ASType",
                c -> c.addStereotypes(new StereotypeImpl(MetaStereotypes.STEREOTYPE.getName()))
        );
        sType.setProperty("documentation", "Stereotype documentation");

        String code = uut.resolve(sType, "").toCode();
        assertThat(code)
                .contains(ProtectionStrategieDefaultImpl.GENERATED_LINE)
                .contains("# Stereotype \"ASType\"")
                .contains("Stereotype documentation")
                .contains("This Stereotype has no associated tagged vales.")
                .contains(
"""
## BaseClass(es)
This stereotype is applicable to the following UML-ELements:

* Element
""")
                .contains(
"""
## Associated Tagged Values
This Stereotype has no associated tagged vales.
"""
                )
        ;

    }


    @Test
    public void testStereotypeDocumentationGenerationWithTaggedValues() throws Exception {
        MClass sType = oomObjectMother.createClass("ASType",
                c -> c.addStereotypes(new StereotypeImpl(MetaStereotypes.STEREOTYPE.getName()))
        );
        sType.setProperty("documentation", "Stereotype documentation");
        oomObjectMother.createAttribute(sType, "anAttribute", "String",
                a -> a.setProperty("documentation", "AttrDocumentation")
        );
        String code = uut.resolve(sType, "").toCode();
        assertThat(code)
                .contains(
"""
## Associated Tagged Values
| Name | Type | Documentation |
|------|-------|----------------------------------------|
|__anAttribute__| String | AttrDocumentation |
"""
                )
        ;
    }
}
