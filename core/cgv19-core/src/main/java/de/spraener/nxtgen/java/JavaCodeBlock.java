package de.spraener.nxtgen.java;

import de.spraener.nxtgen.CodeBlockImpl;
import de.spraener.nxtgen.NxtGenRuntimeException;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;

public class JavaCodeBlock extends CodeBlockImpl {
    String srcDir;
    String pkgName;
    String className;

    public JavaCodeBlock(String srcDir, String pkg, String className) {
        super(pkg+"."+className);
        this.pkgName = pkg;
        this.className = className;
        this.srcDir = srcDir;
    }

    public JavaCodeBlock setClassName(String name) {
        this.className = name;
        return this;
    }

    @Override
    public void writeOutput(String workingDir) {
        try {
            PrintWriter pw = getPrintWriter(workingDir);
            if (pw == null) return;
            pw.print( this.toCode() );
            pw.flush();
            pw.close();
        } catch( Exception e ) {
            throw new NxtGenRuntimeException(e);
        }
    }

    protected PrintWriter getPrintWriter(String workingDir) throws IOException {
        File outFile = getOutputFile(workingDir);
        if (outFile == null) return null;
        return  new PrintWriter(new FileWriter(outFile));
    }

    protected File getOutputFile(String workingDir) {
        File outFile = new File(workingDir +"/"+srcDir+"/"+toFilePath()+".java");
        if( outFile.exists() && checkProtected(outFile) ) {
            return null;
        }
        outFile.getParentFile().mkdirs();
        return outFile;
    }

    public String getSrcDir() {
        return srcDir;
    }

    private String toFilePath() {
        return this.pkgName.replaceAll("\\.", "/")+"/"+this.className;
    }
}
