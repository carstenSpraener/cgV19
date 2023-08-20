package de.spraener.nxtgen.cartridge.meta;

import org.junit.Ignore;
import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class AnnotationBasedMetaCartridgeTests {
    AnnotationBasedMetaCartridge uut = new AnnotationBasedMetaCartridge();

    @Test
    public void testCartridgeLoading() {
        assertEquals(0, uut.getTransformations().size());
        assertEquals(10, uut.getGeneratorWrappers().size() );
    }

    @Test
    @Ignore("Annotated generators are mapped twice!")
    public void testDefaultMappings() {
        MetaCartridgeGeneratorMappingTests.assertDefaultMappings(uut);
    }
}
