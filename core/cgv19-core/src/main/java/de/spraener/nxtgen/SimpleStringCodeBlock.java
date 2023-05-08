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
        System.out.println(workingDir);
    }

    @Override
    public void setToFileStrategy(ToFileStrategy strategy) {
    }
}
