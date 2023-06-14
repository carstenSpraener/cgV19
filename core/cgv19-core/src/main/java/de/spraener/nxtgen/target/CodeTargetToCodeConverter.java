package de.spraener.nxtgen.target;

/**
 * <h4>Responsibility</h4>
 * A CodeTargetToCodeConverter converts a CodeTarget holding
 * CodeSections which holds CodeSnippets to a single String
 * of text. It reads all CodeSections from the target and from
 * each such CodeSection the CodeSnippets. Each CodeSnippets
 * content is than appended to a StringBuilder.
 *
 * The converting is implemented in the "toString()" method.
 */
public class CodeTargetToCodeConverter {
    private CodeTarget codeTarget;

    public CodeTargetToCodeConverter(CodeTarget codeTarget) {
        this.codeTarget = codeTarget;
    }

    public String toString() {
        StringBuilder sb = new StringBuilder();
        for( CodeSection section : this.codeTarget.getSectionsOrdered() ) {
            for( CodeSnippet snippet : section.getSnippetsOrdered() ) {
                snippet.evaluate(sb);
            }
        }
        return sb.toString();
    }
}
