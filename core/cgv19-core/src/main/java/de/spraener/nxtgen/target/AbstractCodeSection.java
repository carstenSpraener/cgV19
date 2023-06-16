package de.spraener.nxtgen.target;

import de.spraener.nxtgen.model.ModelElement;

import java.util.*;
import java.util.stream.Collectors;

public abstract class AbstractCodeSection implements CodeSection {
    Map<Object, List<CodeSnippet>> mySnippets = new LinkedHashMap<>();

    private List<CodeSnippet> getCodeSnippetList(Object key) {
        List<CodeSnippet> list = mySnippets.get(key);
        if (list == null) {
            list = new ArrayList<>();
            mySnippets.put(key, list);
        }
        return list;
    }

    public AbstractCodeSection withSnippet(Object key, CodeSnippet snippet) {
        getCodeSnippetList(key).add(snippet);
        return this;
    }

    public AbstractCodeSection withSnippet(Object key, String lineOfCode) {
        return withSnippet(key, new SingleLineSnippet(key, lineOfCode));
    }

    @Override
    public AbstractCodeSection add(CodeSnippet snippet) {
        getCodeSnippetList(snippet.getAspect()).add(snippet);
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
        return getCodeSnippetList(aspect).stream()
                .filter(s -> {
                    if( aspect==null ) {
                        return true;
                    }
                    return aspect.equals(s.getAspect());
                })
                .map(s -> new CodeSnippetRef(this, s))
                .collect(Collectors.toList());

    }

    @Override
    public CodeSnippetRef getFirstSnippetForAspect(Object aspect) {
        return getCodeSnippetList(aspect).stream()
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
        return this.mySnippets.values().stream()
                .flatMap(l -> l.stream())
                .filter(s -> s.matches(aspect, me))
                .map(s -> new CodeSnippetRef(this, s))
                .collect(Collectors.toList());
    }

    @Override
    public CodeSnippetRef getFirstSnippetForAspectAndModelElement(Object aspect, ModelElement me) {
        return this.mySnippets.values().stream()
                .flatMap(l -> l.stream())
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
        for (List<CodeSnippet> snippetList : this.mySnippets.values()) {
            int idx = snippetList.indexOf(snippet);
            if (idx >= 0) {
                snippetList.add(idx, snippetToInsert);
                return this;
            }
        }
        throw new IllegalArgumentException("Snippet " + snippet + " not part of CodeSection " + this);
    }

    @Override
    public CodeSection insertAfter(CodeSnippet snippet, CodeSnippet snippetToInsert) {
        for (List<CodeSnippet> snippetList : this.mySnippets.values()) {
            int idx = snippetList.indexOf(snippet);
            if (idx >= 0) {
                snippetList.add(idx + 1, snippetToInsert);
                return this;
            }
        }
        throw new IllegalArgumentException("Snippet " + snippet + " not part of CodeSection " + this);
    }

    @Override
    public CodeSection replace(CodeSnippet snippet, CodeSnippet snippetToInsert) {
        for (List<CodeSnippet> snippetList : this.mySnippets.values()) {
            int idx = snippetList.indexOf(snippet);
            if (idx >= 0) {
                snippetList.add(idx + 1, snippetToInsert);
                snippetToInsert.updateAspect(snippet);
                snippetList.remove(idx);
            }
        }
        return this;
    }

    @Override
    public Collection<CodeSnippet> getSnippetsOrdered() {
        List<CodeSnippet> snippetList = new ArrayList<>();
        for (List<CodeSnippet> aSnippetList : this.mySnippets.values()) {
            snippetList.addAll(aSnippetList);
        }
        return snippetList;
    }
}


