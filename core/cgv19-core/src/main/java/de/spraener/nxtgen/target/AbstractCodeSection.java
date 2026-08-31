package de.spraener.nxtgen.target;

import de.spraener.nxtgen.model.ModelElement;

import java.util.*;
import java.util.stream.Collectors;

public abstract class AbstractCodeSection implements CodeSection {
    /** Global ordered list of all snippets — preserves insertion and insertBefore/After order. */
    List<CodeSnippet> allSnippets = new ArrayList<>();
    private Object id = UUID.randomUUID();

    @Override
    public String getId() {
        return this.id.toString();
    }

    public void setId(Object id) {
        this.id = id;
    }

    public AbstractCodeSection withSnippet(Object key, CodeSnippet snippet) {
        allSnippets.add(snippet);
        return this;
    }

    public AbstractCodeSection withSnippet(Object key, String lineOfCode) {
        return withSnippet(key, new SingleLineSnippet(key, lineOfCode));
    }

    @Override
    public AbstractCodeSection add(CodeSnippet snippet) {
        allSnippets.add(snippet);
        return this;
    }

    @Override
    public AbstractCodeSection addFirst(CodeSnippet snippet) {
        if (!allSnippets.isEmpty()) {
            allSnippets.add(0, snippet);
        } else {
            allSnippets.add(snippet);
        }
        return this;
    }

    @Override
    public CodeSection add(Object aspect, String code) {
        return add(new CodeBlockSnippet(aspect, null, code));
    }

    @Override
    public AbstractCodeSection add(Object aspect, ModelElement me, String code) {
        return add(new CodeBlockSnippet(aspect, me, code));
    }

    @Override
    public List<CodeSnippetRef> getSnippetsForAspect(Object aspect) {
        return allSnippets.stream()
                .filter(s -> {
                    if (aspect == null) return true;
                    return aspect.equals(s.getAspect());
                })
                .map(s -> new CodeSnippetRef(this, s))
                .collect(Collectors.toList());
    }

    @Override
    public CodeSnippetRef getFirstSnippetForAspect(Object aspect) {
        return allSnippets.stream()
                .filter(s -> aspect == null || aspect.equals(s.getAspect()))
                .map(s -> new CodeSnippetRef(this, s))
                .findFirst().orElse(null);
    }

    @Override
    public CodeSnippetRef getLastSnippetForAspect(Object aspect) {
        List<CodeSnippetRef> snippetList = getSnippetsForAspect(aspect);
        if (snippetList == null || snippetList.isEmpty()) {
            return null;
        }
        return snippetList.get(snippetList.size() - 1);
    }

    @Override
    public List<CodeSnippetRef> getSnippetsForAspectAndModelElement(Object aspect, ModelElement me) {
        return allSnippets.stream()
                .filter(s -> s.matches(aspect, me))
                .map(s -> new CodeSnippetRef(this, s))
                .collect(Collectors.toList());
    }

    @Override
    public CodeSnippetRef getFirstSnippetForAspectAndModelElement(Object aspect, ModelElement me) {
        return allSnippets.stream()
                .filter(s -> s.matches(aspect, me))
                .map(s -> new CodeSnippetRef(this, s))
                .findFirst().orElse(null);
    }

    @Override
    public CodeSnippetRef getLastSnippetForAspectAndModelElement(Object aspect, ModelElement me) {
        List<CodeSnippetRef> snippetList = getSnippetsForAspectAndModelElement(aspect, me);
        if (snippetList == null || snippetList.isEmpty()) {
            return null;
        }
        return snippetList.get(snippetList.size() - 1);
    }

    @Override
    public CodeSection insertBefore(CodeSnippet snippet, CodeSnippet snippetToInsert) {
        int idx = allSnippets.indexOf(snippet);
        if (idx >= 0) {
            allSnippets.add(idx, snippetToInsert);
            return this;
        }
        throw new IllegalArgumentException("Snippet " + snippet + " not part of CodeSection " + this);
    }

    @Override
    public CodeSection insertAfter(CodeSnippet snippet, CodeSnippet snippetToInsert) {
        int idx = allSnippets.indexOf(snippet);
        if (idx >= 0) {
            allSnippets.add(idx + 1, snippetToInsert);
            return this;
        }
        throw new IllegalArgumentException("Snippet " + snippet + " not part of CodeSection " + this);
    }

    @Override
    public CodeSection replace(CodeSnippet snippet, CodeSnippet snippetToInsert) {
        int idx = allSnippets.indexOf(snippet);
        if (idx >= 0) {
            allSnippets.add(idx, snippetToInsert);
            snippetToInsert.updateAspect(snippet);
            allSnippets.remove(idx + 1);
        }
        return this;
    }

    @Override
    public Collection<CodeSnippet> getSnippetsOrdered() {
        return new ArrayList<>(allSnippets);
    }
}
