package de.spraener.nxtgen.cartridge.meta;

import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class AnnotationBasedMetaCartridgeTests {
    AnnotationBasedMetaCartridge uut = new AnnotationBasedMetaCartridge();

    @Test
    public void testCartridgeLoading() {
        assertEquals(10, uut.getTransformations().size());
        assertEquals(9, uut.getGeneratorWrappers().size() );
    }

    @Test
    public void testDefaultMappings() {
        MetaCartridgeGeneratorMappingTests.assertDefaultMappings(uut);
    }
}
