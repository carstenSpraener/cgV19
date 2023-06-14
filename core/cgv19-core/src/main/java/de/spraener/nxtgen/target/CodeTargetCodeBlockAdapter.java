package de.spraener.nxtgen.target;

import de.spraener.nxtgen.CodeBlockImpl;

public class CodeTargetCodeBlockAdapter extends CodeBlockImpl {
    private CodeTarget codeTarget;
    /**
     * Create a new CodeBlock for the given codeTarget.
     *
     * @param codeTarget a CodeTarget this adapter operates on.
     */
    public CodeTargetCodeBlockAdapter(CodeTarget codeTarget) {
        super("CodeTargetAdapter");
        this.codeTarget = codeTarget;
    }

    @Override
    public String toCode() {
        StringBuilder sb = new StringBuilder();
        sb.append(new CodeTargetToCodeConverter(this.codeTarget).toString());
        sb.append(super.toCode());
        return sb.toString();
    }
}
