package de.spraener.nxtgen.target;

import de.spraener.nxtgen.CodeBlockImpl;

public class CodeTargetCodeBlockAdapter extends CodeBlockImpl {
    private CodeTarget codeTarget;
    private boolean withMarkers = false;
    private String commentPrefix = "//";
    /**
     * Create a new CodeBlock for the given codeTarget.
     *
     * @param codeTarget a CodeTarget this adapter operates on.
     */
    public CodeTargetCodeBlockAdapter(CodeTarget codeTarget) {
        super("CodeTargetAdapter");
        this.codeTarget = codeTarget;
    }

    public CodeTargetCodeBlockAdapter withCommentPrefix(String singleLineCommentPrefix) {
        this.commentPrefix = singleLineCommentPrefix;
        return this;
    }

    public CodeTargetCodeBlockAdapter withMarkers() {
        this.withMarkers = true;
        return this;
    }
    @Override
    public String toCode() {
        CodeTargetToCodeConverter codeConverter = new CodeTargetToCodeConverter(this.codeTarget)
                .withMarkers(this.withMarkers)
                .withSingleLineCommentPrefix(this.commentPrefix)
                ;
        StringBuilder sb = new StringBuilder();
        sb.append(codeConverter.toString());
        sb.append(super.toCode());
        return sb.toString();
    }
}
