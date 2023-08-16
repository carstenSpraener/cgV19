package de.spraener.nxtgen;

import de.spraener.nxtgen.filestrategies.ToFileStrategy;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;

/**
 * A base implementation of a CodeBlock. Most real CodeBlocks do extend this class.
 *
 */
public class CodeBlockImpl implements CodeBlock {
    List<CodeBlock> codeBlocks = new ArrayList<>();
    String name;
    /**
     * Someone has to decide what's the name of the output file. This is the
     * interface that give access to this decission. Consider Java-Naming or
     * PHP/Symfony Naming are completly different.
     *
     * @see ToFileStrategy
     */
    private ToFileStrategy toFileStrategy = null;

    /**
     * Create a new CodeBlock with the given name.
     *
     * @param name a name of the codeblock for reference purpose.
     */
    public CodeBlockImpl(String name) {
        this.name = name;
    }

    /**
     * Write your and the code of all contained codeBlocks to an output string.
     * The implementation calls toCode on all containing CodeBlocks and append them
     * to one long output string.
     *
     * @return a string with the output of all contained CodeBlocks.
     */
    @Override
    public String toCode() {
        StringBuilder sb = new StringBuilder();
        for( CodeBlock cb : codeBlocks ) {
            sb.append(cb.toCode());
        }
        return sb.toString();
    }

    /**
     * Adds the given String to a new SimpleStringCodeBlock and adds them to the contained CodeBlocks.
     * @see SimpleStringCodeBlock
     *
     * @param txt Some text to print into the codeblock
     *
     */
    @Override
    public void println(String txt) {
        addCodeBlock( new SimpleStringCodeBlock(txt) );
    }

    /**
     * Adds onother CodeBlock to the list of conained CodeBlocks.
     * @param subBlock a code block can contain other codeblocks. it is inserted at the current postion/line
     */
    @Override
    public void addCodeBlock(CodeBlock subBlock) {
        codeBlocks.add(subBlock);
    }

    /**
     * returns the Name of this CodeBlock. The CodeBlock can be referenced by this name.
     *
     * @return the name
     */
    @Override
    public String getName() {
        return name;
    }

    /**
     * <p>
     * Checks if the considered outputFile is maybe protected from overwriting. This does not mean
     * that the file is write protected on file system level. It just decides (some how) that the
     * considered file should no be overwritten.
     * </p>
     * <p>
     *     This implementation delegeates the decision to a changeable ProtectionStrategy.
     * </p>
     * @see ProtectionStrategie
     * @param outFile
     * @return
     */
    protected boolean checkProtected(File outFile) {
        return NextGen.getProtectionStrategie().isProtected(outFile);
    }

    /**
     * Do the output wrtiing into the given working directory. The output file
     * is determined with an changeable ToFileStrategy.
     *
     * @see ToFileStrategy
     * @param workingDir the full qualified path to the working directory.
     */
    @Override
    public void writeOutput(String workingDir) {
        try {
            File outFile = getToFileStrategy().open();
            if (outFile.exists() && checkProtected(outFile)) {
                return;
            }
            if( outFile.getParentFile() == null ){
                NextGen.LOGGER.fine("missconfigured generator tries to open illegal file: "+this.getName());
                return;
            }
            outFile.getParentFile().mkdirs();
            PrintWriter pw = new PrintWriter(new FileWriter(outFile));
            pw.print(this.toCode());
            pw.flush();
            pw.close();
        } catch( IOException ioXc) {
            throw new NxtGenRuntimeException(ioXc);
        }
    }

    public ToFileStrategy getToFileStrategy() {
        return toFileStrategy;
    }

    @Override
    public void setToFileStrategy(ToFileStrategy toFileStrategy) {
        this.toFileStrategy = toFileStrategy;
    }

}
