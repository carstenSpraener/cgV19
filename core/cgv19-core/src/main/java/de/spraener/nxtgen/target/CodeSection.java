package de.spraener.nxtgen.target;

import de.spraener.nxtgen.model.ModelElement;

import java.util.Collection;
import java.util.List;

public interface CodeSection {
    List<CodeSnippetRef> getSnippetsForAspect(Object aspect);
    CodeSnippetRef getLastSnippetForAspect(Object aspect);
    CodeSnippetRef getFirstSnippetForAspect(Object aspect);
    List<CodeSnippetRef> getSnippetsForAspectAndModelElement(Object key, ModelElement me);
    CodeSnippetRef getFirstSnippetForAspectAndModelElement(Object key, ModelElement me);
    CodeSnippetRef getLastSnippetForAspectAndModelElement(Object key, ModelElement me);

    CodeSection add(CodeSnippet snippet);
    CodeSection add(Object aspect, String code);
    CodeSection add(Object aspect, ModelElement me, String code);
    CodeSection insertBefore(CodeSnippet snippet, CodeSnippet snippetToInsert);
    CodeSection insertAfter(CodeSnippet snippet, CodeSnippet snippetToInsert);
    CodeSection replace(CodeSnippet snippet, CodeSnippet snippetToInsert);

    Collection<CodeSnippet> getSnippetsOrdered();

}
