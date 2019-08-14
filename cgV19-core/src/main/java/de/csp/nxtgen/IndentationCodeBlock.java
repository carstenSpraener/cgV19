package de.csp.nxtgen;

public class IndentationCodeBlock extends SimpleStringCodeBlock {
    private final String linePrefix;

    public IndentationCodeBlock(String linePrefix) {
        super("");
        this.linePrefix = linePrefix;
    }

    public void println(String lineText) {
        super.println(this.linePrefix+lineText);
    }

    public String getLinePrefix() {
        return this.linePrefix;
    }
}
