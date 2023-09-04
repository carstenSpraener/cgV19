package de.spraener.nxtgen.cartridge.meta;


import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class AnnotationBasedMetaCartridgeTests {
    AnnotationBasedMetaCartridge uut = new AnnotationBasedMetaCartridge();

    @Test
    public void testCartridgeLoading() {
        assertEquals(0, uut.getTransformations().size());
        assertEquals(10, uut.getGeneratorWrappers().size() );
    }

    @Test
    @Disabled("Annotated generators are mapped twice!")
    public void testDefaultMappings() {
        MetaCartridgeGeneratorMappingTests.assertDefaultMappings(uut);
    }
}
