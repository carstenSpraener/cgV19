package de.spraener.nxtgen.target;

import de.spraener.nxtgen.model.ModelElement;

public class CodeBlockSnippet extends CodeSnippet {

    private final String codeBlock;

    public CodeBlockSnippet(Object key, ModelElement me, String codeBlock ) {
        super(key, me);
        this.codeBlock = codeBlock;
    }

    @Override
    public void evaluate(StringBuilder sb) {
        sb.append(this.codeBlock);
    }
}
