package de.spraener.nxtgen.target;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;

public class CodeSectionPathInsertTest {

    private SimpleCodeSection operationWithScopes() {
        SimpleCodeSection op = new SimpleCodeSection();
        op.getOrCreateScope("BEFORE_OPERATION", StandardSections.plain());
        op.getOrCreateScope("IN_OPERATION", StandardSections.plain());
        op.getOrCreateScope("AFTER_OPERATION", StandardSections.plain());
        return op;
    }

    @Test
    void insertIntoSingleScopeReturnsTargetSection() {
        SimpleCodeSection op = operationWithScopes();

        CodeSection target = op.insert("IN_OPERATION", new SingleLineSnippet("a", "body"));

        assertSame(op.getScope("IN_OPERATION"), target);
        assertThat(target.getSnippetsOrdered()).hasSize(1);
    }

    @Test
    void insertAppendsToEnd() {
        SimpleCodeSection op = operationWithScopes();
        CodeSection in = op.getScope("IN_OPERATION");
        in.add(new SingleLineSnippet("a", "first"));

        op.insert("IN_OPERATION", new SingleLineSnippet("b", "second"));

        assertThat(in.getSnippetsOrdered()).hasSize(2);
    }

    @Test
    void insertIntoNestedScopeViaPath() {
        SimpleCodeSection op = operationWithScopes();
        CodeSection in = op.getScope("IN_OPERATION");
        in.getOrCreateScope("DETAILS", StandardSections.plain());

        CodeSection target = op.insert("IN_OPERATION/DETAILS", new SingleLineSnippet("a", "detail"));

        assertSame(in.getScope("DETAILS"), target);
    }

    @Test
    void insertThrowsWhenScopeMissing() {
        SimpleCodeSection op = operationWithScopes();

        assertThrows(IllegalArgumentException.class, () -> op.insert("NOPE", new SingleLineSnippet("a", "x")));
    }

    @Test
    void insertThrowsWhenNestedScopeMissing() {
        SimpleCodeSection op = operationWithScopes();

        assertThrows(IllegalArgumentException.class, () -> op.insert("IN_OPERATION/NOPE", new SingleLineSnippet("a", "x")));
    }

    @Test
    void insertToleratesLeadingSlash() {
        SimpleCodeSection op = operationWithScopes();

        CodeSection target = op.insert("/IN_OPERATION", new SingleLineSnippet("a", "body"));

        assertSame(op.getScope("IN_OPERATION"), target);
    }
}
