package de.spraener.nxtgen;

import de.spraener.nxtgen.filestrategies.ToFileStrategy;

public class SimpleStringCodeBlock implements CodeBlock {
    StringBuilder text;

    public SimpleStringCodeBlock(String txt) {
        this.text = new StringBuilder();
        this.text.append(txt).append("\n");
    }


    @Override
    public String toCode() {
        return text.toString();
    }

    @Override
    public void println(String txt) {
        this.text.append(txt).append("\n");
    }

    @Override
    public void addCodeBlock(CodeBlock subBlock) {
        this.text.append(subBlock.toCode());
    }

    @Override
    public String getName() {
        return null;
    }

    @Override
    public void writeOutput(String workingDir) {
        throw new UnsupportedOperationException("writeOutput not supported by SimpleStringCodeblock");
    }

    @Override
    public void setToFileStrategy(ToFileStrategy strategy) {
        throw new UnsupportedOperationException("setToFileStrategy not supported by SimpleStringCodeblock");
    }
}
