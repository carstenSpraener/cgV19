package de.spraener.nxtgen;

import de.spraener.nxtgen.filestrategies.ToFileStrategy;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;

public class CodeBlockImpl implements CodeBlock {
    List<CodeBlock> codeBlocks = new ArrayList<>();
    String name;
    private ToFileStrategy toFileStrategy = null;

    public CodeBlockImpl(String name) {
        this.name = name;
    }

    @Override
    public String toCode() {
        StringBuilder sb = new StringBuilder();
        for( CodeBlock cb : codeBlocks ) {
            sb.append(cb.toCode());
        }
        return sb.toString();
    }

    @Override
    public void println(String txt) {
        addCodeBlock( new SimpleStringCodeBlock(txt) );
    }

    @Override
    public void addCodeBlock(CodeBlock subBlock) {
        codeBlocks.add(subBlock);
    }

    @Override
    public String getName() {
        return name;
    }


    protected boolean checkProtected(File outFile) {
        return NextGen.getProtectionStrategie().isProtected(outFile);
    }

    @Override
    public void writeOutput(String workingDir) {
        try {
            File outFile = getToFileStrategy().open();
            if (outFile.exists() && checkProtected(outFile)) {
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
