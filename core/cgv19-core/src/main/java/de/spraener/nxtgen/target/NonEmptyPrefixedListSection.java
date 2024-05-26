package de.spraener.nxtgen.target;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * This Section renders a list of code snippets when a snippet is added and stay completely empty
 * if nothing is inserted. The List has an aspect, a prefix and a list separator. It is intended to
 * be used for an "implements" or "extends" list in a class.
 * <p>
 * When snippets are added the result will be: <br>
 * &lt;nonEmptyPrefix&gt; &lt;snippet1&gt;&lt;listSeparator&gt;&lt;snippet2&gt;&lt;listSeparator&gt;&lt;snippet3&gt;...
 * <p>
 * When nothing is added it will be completely empty.
 */
public class NonEmptyPrefixedListSection extends UniqueLineSection {
    private String nonEmptyPrefix;
    private Object aspect;
    private String listSeparator;

    public NonEmptyPrefixedListSection(Object aspect, String nonEmptyPrefix, String listSeparator) {
        this.aspect = aspect;
        this.nonEmptyPrefix = nonEmptyPrefix;
        this.listSeparator = listSeparator;
    }

    @Override
    public Collection<CodeSnippet> getSnippetsOrdered() {
        List<CodeSnippet> codeSnippetList = new ArrayList<>();
        Collection<CodeSnippet> snippetCollection = super.getSnippetsOrdered();
        if (!snippetCollection.isEmpty()) {
            codeSnippetList.add(new CodeBlockSnippet(aspect, null, this.nonEmptyPrefix));
            boolean snippetCopied = false;
            for (CodeSnippet snippet : snippetCollection) {
                if (snippetCopied) {
                    codeSnippetList.add(new CodeBlockSnippet(aspect, null, listSeparator));
                }
                codeSnippetList.add(snippet);
                snippetCopied = true;
            }
        }
        return codeSnippetList;
    }

}
