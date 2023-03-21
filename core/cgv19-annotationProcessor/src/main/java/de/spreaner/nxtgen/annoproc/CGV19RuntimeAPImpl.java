package de.spreaner.nxtgen.annoproc;

import de.spraener.nxtgen.CGV19Runtime;
import de.spraener.nxtgen.CodeBlock;
import de.spraener.nxtgen.GroovyCodeBlockImpl;
import de.spraener.nxtgen.java.JavaCodeBlock;

import javax.annotation.processing.ProcessingEnvironment;
import javax.tools.JavaFileObject;
import java.io.IOException;
import java.io.PrintWriter;

public class CGV19RuntimeAPImpl implements CGV19Runtime {
    private final ProcessingEnvironment procEnv;
    private final Cgv19AP cgv19AP;
    private String workingDir = null;

    CGV19RuntimeAPImpl(Cgv19AP cgv19AP, ProcessingEnvironment procEnv) {
        this.procEnv = procEnv;
        this.cgv19AP = cgv19AP;
    }
    @Override
    public void writeCodeBlock(String workingDir, CodeBlock codeBlock) {
        if( isJavaCodeBlock(codeBlock) ) {
            try {
                JavaFileObject jfo = procEnv.getFiler().createSourceFile(codeBlock.getName());
                try (PrintWriter pw = new PrintWriter(jfo.openWriter())) {
                    pw.println(codeBlock.toCode());
                }
                this.cgv19AP.codeWasGenerated();
            } catch (IOException xc) {
                throw new RuntimeException(xc);
            }
        } else {
            codeBlock.writeOutput(getWorkingDir());
        }
    }

    private boolean isJavaCodeBlock(CodeBlock codeBlock) {
        // TODO: This needs some more reliable implementation
        if( codeBlock instanceof GroovyCodeBlockImpl ) {
            return ((GroovyCodeBlockImpl)codeBlock).getToFileStrategy().open().getName().endsWith(".java");
        }
        if( codeBlock instanceof JavaCodeBlock ) {
            if( ((JavaCodeBlock)codeBlock).getToFileStrategy()==null) {
                return true;
            }
            return ((JavaCodeBlock)codeBlock).getToFileStrategy().open().getName().endsWith(".java");
        }
        return false;
    }

    @Override
    public String getWorkingDir() {
        // TODO: How could we get the project directory?
        if( this.workingDir == null ) {
            try {
                JavaFileObject jfo = this.procEnv.getFiler().createSourceFile("cgv19_Dummy");
                String path = jfo.toUri().getPath();
                if( path.contains("/build/")) {
                    path = path.substring(0, path.indexOf("/build/"));
                }
                this.workingDir = path;
                jfo.delete();
            } catch (Exception e) {
            }
        }
        return this.workingDir;
    }

    public void setWorkingDir(String workingDir) {
        this.workingDir = workingDir;
    }
}
