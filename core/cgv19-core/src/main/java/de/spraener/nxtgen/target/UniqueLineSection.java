package de.spraener.nxtgen.target;

import java.util.HashSet;
import java.util.Set;

/**
 * This section evaluates, so that there is no duplicate line. It can be used to
 * generate import statements.
 */
public class UniqueLineSection extends AbstractCodeSection {
    private Set<String> containedCode = new HashSet<>();

    private boolean isContained(CodeSnippet snippet) {
        StringBuilder sb = new StringBuilder();
        snippet.evaluate(sb);
        String code = sb.toString().replaceAll("\n", "");
        if( containedCode.contains(code) ) {
            return true;
        }
        containedCode.add(code);
        return false;
    }

    @Override
    public AbstractCodeSection withSnippet(Object key, CodeSnippet snippet) {
        if( isContained( snippet ) ) {
            return this;
        }
        return super.withSnippet(key, snippet);
    }

    @Override
    public AbstractCodeSection add(CodeSnippet snippet) {
        if( isContained( snippet ) ) {
            return this;
        }
        return super.add(snippet);
    }

    @Override
    public CodeSection insertBefore(CodeSnippet snippet, CodeSnippet snippetToInsert) {
        if( isContained( snippetToInsert ) ) {
            return this;
        }
        return super.insertBefore(snippet, snippetToInsert);
    }

    @Override
    public CodeSection insertAfter(CodeSnippet snippet, CodeSnippet snippetToInsert) {
        if( isContained( snippetToInsert ) ) {
            return this;
        }
        return super.insertAfter(snippet, snippetToInsert);
    }

    @Override
    public CodeSection replace(CodeSnippet snippet, CodeSnippet snippetToInsert) {
        if( isContained( snippetToInsert ) ) {
            return this;
        }
        return super.replace(snippet, snippetToInsert);
    }
}
