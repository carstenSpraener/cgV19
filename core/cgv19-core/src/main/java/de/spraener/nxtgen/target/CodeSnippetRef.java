package de.spraener.nxtgen.target;

import de.spraener.nxtgen.model.ModelElement;

public class CodeSnippetRef {
    private CodeSection owningSection;
    private CodeSnippet snippet;

    public CodeSnippetRef(CodeSection owningSection, CodeSnippet snippet) {
        this.owningSection = owningSection;
        this.snippet = snippet;
    }

    public CodeSnippetRef insertBefore(CodeSnippet snippetToInsert) {
        this.owningSection.insertBefore(snippet, snippetToInsert);
        return this;
    }

    public CodeSnippetRef insertBefore(Object aspect, ModelElement attr, String code) {
        return insertBefore(new CodeBlockSnippet(aspect, attr, code));
    }

    public CodeSnippetRef insertBefore(Object aspect, String code) {
        return insertBefore(new CodeBlockSnippet(aspect, null, code));
    }

    public CodeSnippetRef replace(CodeSnippet snippetToInsert) {
        this.owningSection.replace(this.snippet, snippetToInsert);
        this.snippet = snippetToInsert;
        return this;
    }

    public CodeSnippetRef replace(Object aspect, ModelElement attr, String code) {
        return replace(new CodeBlockSnippet(aspect, attr, code));
    }

    public CodeSnippetRef replace(Object aspect, String code) {
        return replace(new CodeBlockSnippet(aspect, null, code));
    }

    public CodeSnippetRef insertAfter(CodeSnippet snippetToInsert) {
        this.owningSection.insertAfter(this.snippet, snippetToInsert);
        return this;
    }

    public CodeSnippetRef insertAfter(Object aspect, ModelElement attr, String code) {
        insertAfter(new CodeBlockSnippet(aspect, attr, code));
        return this;
    }

    public CodeSnippetRef insertAfter(Object aspect, String code) {
        insertAfter(new CodeBlockSnippet(aspect, null, code));
        return this;
    }

    public CodeSnippet get() {
        return this.snippet;
    }
}
