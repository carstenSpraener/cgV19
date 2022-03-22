package de.spraener.nxtgen.cartridge.rest.php;

import de.spraener.nxtgen.CodeBlockImpl;
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
        String phpProjectDir = getOutputPath();
        this.srcDir = phpProjectDir+"/"+srcDir;
    }
    @Override
    public void writeOutput(String workingDir) {
        try {
            String phpProjectDir = getOutputPath(workingDir);
            String filePath = phpProjectDir+"/src/"+toFilePath()+".php";
            File outFile = new File(filePath);
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

    private static String getOutputPath() {
        if( outputPath==null ) {
            outputPath = System.getenv("php_project_dir");
            if (outputPath == null || "".equals(outputPath)) {
                outputPath = "./php";
            }
            LOGGER.info("Writing PHP_Files to "+outputPath);
        }
        return outputPath;
    }

    private static String getOutputPath(String workingDir) {
        if( outputPath==null ) {
            outputPath = System.getenv("php_project_dir");
            if (outputPath == null || "".equals(outputPath)) {
                outputPath = workingDir;
            }
            LOGGER.info("Writing PHP_Files to "+workingDir);
        }
        return outputPath;
    }
    private String toFilePath() {
        return this.pkgName.replaceAll("\\.", "/")+"/"+this.className;
    }
}
