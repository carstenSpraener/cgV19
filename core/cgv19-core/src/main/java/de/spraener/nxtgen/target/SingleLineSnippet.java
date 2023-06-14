package de.spraener.nxtgen.target;

import de.spraener.nxtgen.model.ModelElement;

public class SingleLineSnippet extends CodeSnippet {
    private String lineOfCode;

    public SingleLineSnippet(String lineOfCode) {
        this(CodeTarget.activeAspect.get(), lineOfCode);
    }

    public SingleLineSnippet(Object aspect, String lineOfCode) {
        this(aspect, null, lineOfCode);
    }

    public SingleLineSnippet(Object aspect, ModelElement me, String lineOfCode) {
        super(aspect, me);
        this.lineOfCode = lineOfCode;
    }

    @Override
    public void evaluate(StringBuilder sb) {
        sb.append(lineOfCode).append("\n");
    }
}
