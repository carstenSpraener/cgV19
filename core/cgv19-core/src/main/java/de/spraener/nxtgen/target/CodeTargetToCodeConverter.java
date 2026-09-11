package de.spraener.nxtgen.target;

import de.spraener.nxtgen.model.ModelHelper;

/**
 * <strong>Responsibility</strong>
 * A CodeTargetToCodeConverter converts a CodeTarget holding
 * CodeSections which holds CodeSnippets to a single String
 * of text. It reads all CodeSections from the target and from
 * each such CodeSection the CodeSnippets. Each CodeSnippets
 * content is than appended to a StringBuilder.
 *
 * The converting is implemented in the "toString()" method.
 */
public class CodeTargetToCodeConverter {
    private static final String INDENT_UNIT = "    ";

    private CodeTarget codeTarget;
    private boolean withMarkers = false;
    private String singleLineCommentPrefix = "//";

    public CodeTargetToCodeConverter(CodeTarget codeTarget) {
        this.codeTarget = codeTarget;
    }

    public String toString() {
        StringBuilder sb = new StringBuilder();
        for( CodeSection section : this.codeTarget.getSectionsOrdered() ) {
            renderSection(sb, section, 0);
        }
        return sb.toString();
    }

    private void renderSection(StringBuilder sb, CodeSection section, int indentLevel) {
        String pad = INDENT_UNIT.repeat(indentLevel);
        if( withMarkers ) {
            sb.append(String.format("%n%s<<section id=%s>>%n", this.singleLineCommentPrefix, section.getId()));
        }
        if( section.rendersChildrenInline() ) {
            for( CodeSnippet snippet : section.getSnippetsOrdered() ) {
                renderSnippet(sb, snippet, pad);
            }
        } else {
            for( CodeSnippet snippet : section.getOwnSnippets() ) {
                renderSnippet(sb, snippet, pad);
            }
            for( CodeSection child : section.getChildren() ) {
                renderSection(sb, child, indentLevel + 1);
            }
        }
        if( withMarkers ) {
            sb.append(String.format("%n%s<</section:%s>>%n", this.singleLineCommentPrefix, section.getId()));
        }
    }

    private void renderSnippet(StringBuilder sb, CodeSnippet snippet, String pad) {
        if( withMarkers ) {
            String fqName = "";
            if( snippet.getModelElement()!=null ) {
                fqName = " modelELement="+ModelHelper.getFQName(snippet.getModelElement(), ".");
            }

            sb.append(String.format("%n%s<<snippet aspect=%s%s>>%n", this.singleLineCommentPrefix, snippet.getAspect(), fqName));
        }
        StringBuilder snippetSb = new StringBuilder();
        snippet.evaluate(snippetSb);
        appendIndented(sb, snippetSb.toString(), pad);
        if( withMarkers ) {
            sb.append(String.format("%n%s<</snippet:%s>>%n", this.singleLineCommentPrefix, snippet.getAspect()));
        }
    }

    /**
     * Appends evaluated snippet code. With a non-empty pad, every non-empty line is prefixed
     * with the pad; the original newline structure is preserved. With an empty pad (depth 0)
     * the code is appended as-is, byte-identical to the former flat rendering.
     */
    private void appendIndented(StringBuilder sb, String code, String pad) {
        if( pad.isEmpty() ) {
            sb.append(code);
            return;
        }
        int start = 0;
        for( int i = 0; i <= code.length(); i++ ) {
            boolean atLineEnd = (i == code.length()) || code.charAt(i) == '\n';
            if( !atLineEnd ) {
                continue;
            }
            String line = code.substring(start, i);
            if( !line.isEmpty() && atLineStart(sb) ) {
                sb.append(pad);
            }
            sb.append(line);
            if( i < code.length() ) {
                sb.append('\n');
            }
            start = i + 1;
        }
    }

    private boolean atLineStart(StringBuilder sb) {
        return sb.length() == 0 || sb.charAt(sb.length() - 1) == '\n';
    }

    public CodeTargetToCodeConverter withSingleLineCommentPrefix( String singleLineCommentPrefix ) {
        this.singleLineCommentPrefix = singleLineCommentPrefix;
        return this;
    }

    public CodeTargetToCodeConverter withMarkers(boolean withMarkers) {
        this.withMarkers = withMarkers;
        return this;
    }
}
