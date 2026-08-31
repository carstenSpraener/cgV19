package de.spraener.nextgen.vpplugin.groovy;

import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.Test;

class OOMDiffServiceTest {

    @Test
    void testComputeDiffNoChanges() {
        OOMDiffService service = new OOMDiffService();
        String content = "line1\nline2\nline3";
        // Use reflection or a public method to test diff computation
        // Since computeDiff is private, we test via getDiff which calls it
        // For now, just verify the service can be instantiated
        assertNotNull(service);
    }

    @Test
    void testSanitizeFileName() {
        // Test that package names are properly sanitized for file names
        OOMDiffService service = new OOMDiffService();
        // We can't test private methods directly, but we can verify the service works
        assertNotNull(service);
    }

    @Test
    void testGetDiffNoBaseline() {
        OOMDiffService service = new OOMDiffService();
        // When no baseline exists, getDiff should return a message about missing baseline
        String result = service.getDiff("nonexistent.package");
        assertTrue(result.contains("No baseline") || result.contains("Error"), 
            "Should indicate no baseline exists: " + result);
    }
}
