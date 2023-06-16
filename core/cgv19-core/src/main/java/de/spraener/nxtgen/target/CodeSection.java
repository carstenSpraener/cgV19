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

    /**
     * DANGER! This replace-method will REPLACE! the snippet with the snippetToInsert. Only use this method
     * when you are really sure to do so. The aspect of the snippetToInsert will be overwritten with the
     * aspect of the replaced snippet. USE WITH CARE!
     *
     * @param snippet the snippet to be replaced and to take the aspect from
     * @param snippetToInsert the snippet to replace the old snippet and get the aspect from the old snippet.
     * @return the CodeSection with replaced snippet.
     */
    CodeSection replace(CodeSnippet snippet, CodeSnippet snippetToInsert);

    Collection<CodeSnippet> getSnippetsOrdered();

}
