package de.spraener.nxtgen.target;

import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;

public class StandardSectionsTest {

    @Test
    void plainCreatesSimpleCodeSectionWithoutChildren() {
        CodeSection section = StandardSections.plain().get();

        assertTrue(section instanceof SimpleCodeSection);
        assertThat(section.getChildren()).isEmpty();
    }

    @Test
    void operationCreatesStandardOperationScopesInOrder() {
        CodeSection section = StandardSections.operation().get();

        List<String> names = new ArrayList<>();
        for (CodeSection child : section.getChildren()) {
            names.add(child.getId());
        }
        assertThat(names).containsExactly(
                SectionName.BEFORE_OPERATION.name(),
                SectionName.IN_OPERATION.name(),
                SectionName.AFTER_OPERATION.name());

        for (CodeSection child : section.getChildren()) {
            assertSame(section, child.getParent());
        }
    }

    @Test
    void preambleCreatesPreambleAndEpilogueScopes() {
        CodeSection section = StandardSections.preamble().get();

        List<String> names = new ArrayList<>();
        for (CodeSection child : section.getChildren()) {
            names.add(child.getId());
        }
        assertThat(names).containsExactly(SectionName.PREAMBLE.name(), SectionName.EPILOGUE.name());
    }

    @Test
    void scopesAreAddressableByName() {
        CodeSection section = StandardSections.operation().get();

        assertNotNull(section.getScope(SectionName.IN_OPERATION.name()));
        assertNull(section.getScope("UNKNOWN"));
    }

    @Test
    void eachSupplierInvocationCreatesFreshInstances() {
        CodeSection first = StandardSections.operation().get();
        CodeSection second = StandardSections.operation().get();

        assertNotSame(first, second);
    }
}
