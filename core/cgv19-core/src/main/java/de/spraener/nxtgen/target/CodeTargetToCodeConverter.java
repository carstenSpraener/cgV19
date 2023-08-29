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
    private CodeTarget codeTarget;
    private boolean withMarkers = false;
    private String singleLineCommentPrefix = "//";

    public CodeTargetToCodeConverter(CodeTarget codeTarget) {
        this.codeTarget = codeTarget;
    }

    public String toString() {
        StringBuilder sb = new StringBuilder();
        for( CodeSection section : this.codeTarget.getSectionsOrdered() ) {
            if( withMarkers ) {
                sb.append(String.format("%n%s<<section id=%s>>%n", this.singleLineCommentPrefix, section.getId()));
            }
            for( CodeSnippet snippet : section.getSnippetsOrdered() ) {
                if( withMarkers ) {
                    String fqName = "";
                    if( snippet.getModelElement()!=null ) {
                        fqName = " modelELement="+ModelHelper.getFQName(snippet.getModelElement(), ".");
                    }

                    sb.append(String.format("%n%s<<snippet aspect=%s%s>>%n", this.singleLineCommentPrefix, snippet.getAspect(), fqName));
                }
                snippet.evaluate(sb);
                if( withMarkers ) {
                    sb.append(String.format("%n%s<</snippet:%s>>%n", this.singleLineCommentPrefix, snippet.getAspect()));
                }
            }
            if( withMarkers ) {
                sb.append(String.format("%n%s<</section:%s>>%n", this.singleLineCommentPrefix, section.getId()));
            }
        }
        return sb.toString();
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
