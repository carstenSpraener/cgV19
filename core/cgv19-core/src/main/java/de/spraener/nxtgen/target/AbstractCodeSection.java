package de.spraener.nxtgen.target;

import de.spraener.nxtgen.model.ModelElement;

import java.util.*;
import java.util.function.Supplier;
import java.util.stream.Collectors;

public abstract class AbstractCodeSection implements CodeSection {
    /** Global ordered list of all snippets — preserves insertion and insertBefore/After order. */
    List<CodeSnippet> allSnippets = new ArrayList<>();
    private Object id = UUID.randomUUID();
    /** Child scopes in deterministic insertion order. All access is synchronized on this map. */
    private final Map<String, CodeSection> children = new LinkedHashMap<>();
    private CodeSection parent;

    public void setParent(CodeSection parent) {
        this.parent = parent;
    }

    @Override
    public CodeSection getParent() {
        return this.parent;
    }

    @Override
    public synchronized CodeSection getOrCreateScope(String scopeName, Supplier<CodeSection> scopeSupplier) {
        return children.computeIfAbsent(scopeName, name -> {
            CodeSection child = scopeSupplier.get();
            child.setId(name);
            if (child instanceof AbstractCodeSection abs) {
                abs.setParent(this);
            }
            return child;
        });
    }

    @Override
    public synchronized CodeSection getScope(String scopeName) {
        return children.get(scopeName);
    }

    @Override
    public synchronized Collection<CodeSection> getChildren() {
        return new ArrayList<>(children.values());
    }

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
        CodeSection owner = findOwnerOf(snippet);
        if (owner != null) {
            return owner.insertBefore(snippet, snippetToInsert);
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
        CodeSection owner = findOwnerOf(snippet);
        if (owner != null) {
            return owner.insertAfter(snippet, snippetToInsert);
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
            return this;
        }
        CodeSection owner = findOwnerOf(snippet);
        if (owner != null) {
            return owner.replace(snippet, snippetToInsert);
        }
        return this;
    }

    @Override
    public CodeSection insert(String path, CodeSnippet snippet) {
        CodeSection target = resolveScopePath(path);
        target.add(snippet);
        return target;
    }

    /**
     * Resolves a '/'-separated scope path relative to this section. A leading '/' is
     * tolerated. Every segment must name an existing child scope; otherwise an
     * IllegalArgumentException is thrown (missing scopes are not created).
     */
    private CodeSection resolveScopePath(String path) {
        String normalized = normalizePath(path);
        CodeSection current = this;
        for (String segment : normalized.split("/", -1)) {
            CodeSection next = current.getScope(segment);
            if (next == null) {
                throw new IllegalArgumentException("Cannot resolve scope path '" + path + "': segment '" + segment + "' not found in section with id " + current.getId());
            }
            current = next;
        }
        return current;
    }

    private static String normalizePath(String path) {
        return path.startsWith("/") ? path.substring(1) : path;
    }

    /**
     * Finds the section in this subtree that directly contains the given snippet,
     * searching own snippets first and then all child scopes recursively.
     */
    private CodeSection findOwnerOf(CodeSnippet snippet) {
        if (allSnippets.contains(snippet)) {
            return this;
        }
        for (CodeSection child : getChildren()) {
            if (child instanceof AbstractCodeSection abs) {
                CodeSection owner = abs.findOwnerOf(snippet);
                if (owner != null) {
                    return owner;
                }
            } else if (child.getOwnSnippets().contains(snippet)) {
                return child;
            }
        }
        return null;
    }

    /**
     * Depth-first, pre-order aggregation: own snippets first, then the snippets of each
     * child scope in insertion order (recursively).
     */
    @Override
    public Collection<CodeSnippet> getSnippetsOrdered() {
        List<CodeSnippet> result = new ArrayList<>(allSnippets);
        for (CodeSection child : getChildren()) {
            result.addAll(child.getSnippetsOrdered());
        }
        return result;
    }

    @Override
    public Collection<CodeSnippet> getOwnSnippets() {
        return new ArrayList<>(allSnippets);
    }
}
