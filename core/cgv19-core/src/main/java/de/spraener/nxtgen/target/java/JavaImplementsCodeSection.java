package de.spraener.nxtgen.target.java;

import de.spraener.nxtgen.target.CodeBlockSnippet;
import de.spraener.nxtgen.target.CodeSnippet;
import de.spraener.nxtgen.target.UniqueLineSection;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * This code section evaluates to an empty string if it contains no snippets.
 * If it contains a snippet, it will add the keyword "implements" to the
 * code and a "," between to snippets code. It also removes double implements
 * snippets.
 * <p/>
 * It is used to easily create a Java "implements"-List
 */
public class JavaImplementsCodeSection extends UniqueLineSection {
    @Override
    public Collection<CodeSnippet> getSnippetsOrdered() {
        List<CodeSnippet> codeSnippetList = new ArrayList<>();
        Collection<CodeSnippet> snippetCollection = super.getSnippetsOrdered();
        if( !snippetCollection.isEmpty() ) {
            codeSnippetList.add(new CodeBlockSnippet("java", null, "implements "));
            boolean snippetCopied = false;
            for( CodeSnippet snippet : snippetCollection ) {
                if( snippetCopied ) {
                    codeSnippetList.add(new CodeBlockSnippet("java", null, ", "));
                }
                codeSnippetList.add(snippet);
                snippetCopied = true;
            }
        }
        return codeSnippetList;
    }
}
