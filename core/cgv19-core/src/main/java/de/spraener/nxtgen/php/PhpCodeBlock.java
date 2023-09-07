package de.spraener.nxtgen.php;

import de.spraener.nxtgen.CGV19Config;
import de.spraener.nxtgen.CodeBlockImpl;
import de.spraener.nxtgen.NextGen;
import de.spraener.nxtgen.NxtGenRuntimeException;

import java.io.File;
import java.io.FileWriter;
import java.io.PrintWriter;
import java.util.logging.Logger;

public class PhpCodeBlock extends CodeBlockImpl {
    private static final Logger LOGGER = Logger.getLogger("PhpCodeBlock");
    private static String outputPath = null;
    String srcDir;
    String pkgName;
    String className;

    public PhpCodeBlock(String srcDir, String pkg, String className) {
        super(pkg+"."+className);
        this.pkgName = pkg;
        this.className = className;
        this.srcDir = srcDir;
    }

    @Override
    public void writeOutput(String workingDir) {
        try {
            File outFile = toFile(workingDir);
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

    private File toFile(String workingDir) {
        if( getToFileStrategy() != null ) {
            return getToFileStrategy().open();
        } else {
            String phpProjectDir = getOutputPath(workingDir);
            String filePath = phpProjectDir + "/"+this.srcDir + toFilePath() + ".php";
            return new File(filePath);
        }
    }

    public static String getOutputPath() {
        if( outputPath==null ) {
            outputPath = CGV19Config.definitionOf("phpProjectDir", NextGen.getWorkingDir());
        }
        return outputPath;
    }

    private static String getOutputPath(String workingDir) {
        if( outputPath==null ) {
            outputPath = CGV19Config.definitionOf("phpProjectDir", NextGen.getWorkingDir());
            if (outputPath == null || "".equals(outputPath)) {
                outputPath = workingDir;
            }
            LOGGER.fine("Writing PHP_Files to "+workingDir);
        }
        return outputPath;
    }

    private String toFilePath() {
        return this.pkgName.replaceAll("\\.", "/")
                .replaceAll("\\\\", "/")
                +"/"+this.className;
    }

    public static void setOutputPath(String outputPath) {
        PhpCodeBlock.outputPath = outputPath;
    }

    public String getSrcDir() {
        return srcDir;
    }

    public void setSrcDir(String srcDir) {
        this.srcDir = srcDir;
    }

    public String getPkgName() {
        return pkgName;
    }

    public void setPkgName(String pkgName) {
        this.pkgName = pkgName;
    }

    public String getClassName() {
        return className;
    }

    public void setClassName(String className) {
        this.className = className;
    }
}
