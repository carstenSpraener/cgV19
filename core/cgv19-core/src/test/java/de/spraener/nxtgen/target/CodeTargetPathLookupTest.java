package de.spraener.nxtgen.target;

import de.spraener.nxtgen.target.java.JavaSections;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;

public class CodeTargetPathLookupTest {

    @Test
    void simpleKeyStillResolvesTopLevelSection() {
        CodeTarget target = new CodeTarget();
        SimpleCodeSection section = new SimpleCodeSection();
        target.addCodeSection("METHODS", section);

        assertSame(section, target.getSection("METHODS"));
    }

    @Test
    void pathResolvesNestedScope() {
        CodeTarget target = new CodeTarget();
        SimpleCodeSection methods = new SimpleCodeSection();
        CodeSection inOperation = methods.getOrCreateScope("IN_OPERATION", StandardSections.plain());
        target.addCodeSection("METHODS", methods);

        assertSame(inOperation, target.getSection("METHODS/IN_OPERATION"));
    }

    @Test
    void pathResolvesDeepScopes() {
        CodeTarget target = new CodeTarget();
        SimpleCodeSection methods = new SimpleCodeSection();
        CodeSection inOperation = methods.getOrCreateScope("IN_OPERATION", StandardSections.plain());
        CodeSection deep = inOperation.getOrCreateScope("DETAILS", StandardSections.plain());
        target.addCodeSection("METHODS", methods);

        assertSame(deep, target.getSection("METHODS/IN_OPERATION/DETAILS"));
    }

    @Test
    void missingScopeInPathReturnsNull() {
        CodeTarget target = new CodeTarget();
        SimpleCodeSection methods = new SimpleCodeSection();
        methods.getOrCreateScope("IN_OPERATION", StandardSections.plain());
        target.addCodeSection("METHODS", methods);

        assertNull(target.getSection("METHODS/NOPE"));
    }

    @Test
    void missingTopLevelSectionInPathReturnsNull() {
        CodeTarget target = new CodeTarget();
        target.addCodeSection("METHODS", new SimpleCodeSection());

        assertNull(target.getSection("UNKNOWN/IN_OPERATION"));
    }

    @Test
    void pathFallsBackToSectionIdForNonMatchingKeys() {
        CodeTarget target = new CodeTarget();
        SimpleCodeSection methods = new SimpleCodeSection();
        CodeSection inOperation = methods.getOrCreateScope("IN_OPERATION", StandardSections.plain());
        target.addCodeSection(JavaSections.METHODS, methods);

        assertSame(inOperation, target.getSection("METHODS/IN_OPERATION"));
    }

    @Test
    void exactKeyMatchTakesPrecedenceOverIdFallback() {
        CodeTarget target = new CodeTarget();
        SimpleCodeSection stringKeyed = new SimpleCodeSection();
        stringKeyed.getOrCreateScope("IN_OPERATION", StandardSections.plain());
        SimpleCodeSection enumKeyed = new SimpleCodeSection();
        target.addCodeSection("METHODS", stringKeyed);
        target.addCodeSection(JavaSections.METHODS, enumKeyed);

        assertSame(stringKeyed.getScope("IN_OPERATION"), target.getSection("METHODS/IN_OPERATION"));
    }

    @Test
    void nonStringKeyIsUnaffected() {
        CodeTarget target = new CodeTarget();
        SimpleCodeSection section = new SimpleCodeSection();
        Object key = new Object();
        target.addCodeSection(key, section);

        assertSame(section, target.getSection(key));
    }

    @Test
    void appendWorksOnNestedScopeViaPath() {
        CodeTarget target = new CodeTarget();
        SimpleCodeSection methods = new SimpleCodeSection();
        methods.getOrCreateScope("IN_OPERATION", StandardSections.plain());
        target.addCodeSection("METHODS", methods);

        target.append("METHODS/IN_OPERATION", "some code");

        assertThat(methods.getScope("IN_OPERATION").getSnippetsOrdered()).hasSize(1);
    }
}
