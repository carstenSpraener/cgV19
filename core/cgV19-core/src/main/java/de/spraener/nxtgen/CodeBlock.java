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
     * @return Source of the code block
     */
    String toCode();

    /**
     * Add a single line to the code block
     * @param txt Some text to print into the codeblock
     */
    void println(String txt);

    /**
     * add a CodeBlock to this code block.
     *
     * @param subBlock a code block can contain other codeblocks. it is inserted at the current postion/line
     */
    void addCodeBlock(CodeBlock subBlock);

    /**
     * get the Name of this code block
     * @return the unique name of the code block inside the generation context
     */
    String getName();

    void writeOutput(String workingDir);

    void setToFileStrategy(ToFileStrategy strategy);

    default String getClassName() {
        return null;
    }
}
