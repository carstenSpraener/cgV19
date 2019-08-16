package de.spraener.nxtgen;

import de.spraener.nxtgen.filestrategies.ToFileStrategy;

/**
 * A CodeBlock is a recursive structure to hold all blocks that together build a
 * peace of generated code.
 *
 * It is the result of a CodeGenerator.rsolve Method call.
 *
 */
public interface CodeBlock {
    /**
     * Transform the CodeBlock to source code.
     * @return
     */
    String toCode();

    /**
     * Add a single line to the code block
     * @param txt
     */
    void println(String txt);

    /**
     * add a CodeBlock to this code block.
     */
    void addCodeBlock(CodeBlock subBlock);

    /**
     * get the Name of this code block
     * @return
     */
    String getName();

    public void writeOutput(String workingDir);

    void setToFileStrategy(ToFileStrategy strategy);
}
