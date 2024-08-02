package de.spraener.nxtgen.cartridge.meta;


import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;

import static de.spraener.nxtgen.cartridge.meta.MetaCartridgeGeneratorMappingTests.assertDefaultMapping;
import static org.junit.jupiter.api.Assertions.assertEquals;

public class AnnotationBasedMetaCartridgeTests {
    AnnotationBasedMetaCartridge uut = new AnnotationBasedMetaCartridge();

    @Test
    public void testCartridgeLoading() {
        assertEquals(0, uut.getTransformations().size());
        assertEquals(1, uut.getGeneratorWrappers().size() );
    }

    @Test
    public void testDefaultMappings() {
        assertDefaultMapping(uut, "cgV19CartridgeBase", 0);
        assertDefaultMapping(uut, "GroovyScript", 0);
        assertDefaultMapping(uut, "Transformation", 0);
        assertDefaultMapping(uut, "cgV19Cartridge", 0);
        assertDefaultMapping(uut, "cgV19CartridgeServiceDefinition", 1);
        assertDefaultMapping(uut, "StereotypeEnum", 0);
        assertDefaultMapping(uut, "Stereotype", 0);
        assertDefaultMapping(uut, "CodeGenerator", 0);
    }
}
