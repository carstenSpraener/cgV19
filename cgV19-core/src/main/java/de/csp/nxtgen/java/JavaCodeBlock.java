package de.csp.nxtgen.java;

import de.csp.nxtgen.CodeBlockImpl;
import de.csp.nxtgen.NxtGenRuntimeException;

import java.io.*;

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

    @Override
    public void writeOutput(String workingDir) {
        try {
            File outFile = new File(workingDir+"/"+srcDir+"/"+toFilePath()+".java");
            if( outFile.exists() && checkProtected(outFile) ) {
                return;
            }
            outFile.getParentFile().mkdirs();
            PrintWriter pw = new PrintWriter(new FileWriter(outFile));
            pw.print( this.toCode() );
            pw.flush();
            pw.close();
        } catch( Exception e ) {
            throw new NxtGenRuntimeException(e);
        }
    }

    private String toFilePath() {
        return this.pkgName.replaceAll("\\.", "/")+"/"+this.className;
    }
}
