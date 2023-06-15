package de.spraener.nxtgen.target;

import de.spraener.nxtgen.model.ModelElement;

public class CodeBlockSnippet extends CodeSnippet {

    private final String codeBlock;

    public CodeBlockSnippet( String codeBlock ) {
        this(CodeTargetContext.getActiveContext().getAspect(), CodeTargetContext.getActiveContext().getModelElement(), codeBlock);
    }

    public CodeBlockSnippet(Object aspect, ModelElement me, String codeBlock ) {
        super(aspect, me);
        this.codeBlock = codeBlock;
    }

    @Override
    public void evaluate(StringBuilder sb) {
        sb.append(this.codeBlock);
    }
}
