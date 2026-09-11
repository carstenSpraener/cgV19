package de.spraener.nxtgen.target;

import de.spraener.nxtgen.model.ModelElement;

import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.UUID;
import java.util.function.Supplier;

public interface CodeSection {
    String getId();
    void setId(Object id);
    List<CodeSnippetRef> getSnippetsForAspect(Object aspect);
    CodeSnippetRef getLastSnippetForAspect(Object aspect);
    CodeSnippetRef getFirstSnippetForAspect(Object aspect);
    List<CodeSnippetRef> getSnippetsForAspectAndModelElement(Object key, ModelElement me);
    CodeSnippetRef getFirstSnippetForAspectAndModelElement(Object key, ModelElement me);
    CodeSnippetRef getLastSnippetForAspectAndModelElement(Object key, ModelElement me);

    CodeSection add(CodeSnippet snippet);
    CodeSection addFirst(CodeSnippet snippet);
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

    /**
     * Returns the snippets contained directly in this section, without any child scopes.
     * The default implementation returns {@link #getSnippetsOrdered()}, which is correct
     * for flat sections without children.
     */
    default Collection<CodeSnippet> getOwnSnippets() {
        return getSnippetsOrdered();
    }

    /**
     * Returns the child scope with the given name, creating it via the supplier on first
     * access. The supplier is invoked at most once (deterministic caching).
     */
    default CodeSection getOrCreateScope(String scopeName, Supplier<CodeSection> scopeSupplier) {
        throw new UnsupportedOperationException("Scopes are not supported by " + getClass().getSimpleName());
    }

    /**
     * Returns the child scope with the given name or null if it does not exist.
     */
    default CodeSection getScope(String scopeName) {
        return null;
    }

    /**
     * Returns the parent section or null if this is a root section.
     */
    default CodeSection getParent() {
        return null;
    }

    /**
     * Returns the child scopes in insertion order.
     */
    default Collection<CodeSection> getChildren() {
        return Collections.emptyList();
    }

    /**
     * If true, child scopes are rendered as pure logical groupings: no own indentation,
     * no line breaks — the section renders its full recursive snippet list as one flat block.
     */
    default boolean rendersChildrenInline() {
        return false;
    }

}
