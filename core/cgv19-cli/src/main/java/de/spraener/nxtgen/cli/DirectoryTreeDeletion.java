package de.spraener.nxtgen.cli;

import de.spraener.nxtgen.NextGen;
import de.spraener.nxtgen.ProtectionStrategie;

import java.io.File;
import java.util.logging.Level;

public class DirectoryTreeDeletion implements Runnable {
    private File rootDir;
    private ProtectionStrategie protectionStrategie;

    public DirectoryTreeDeletion(String workDir) {
        rootDir = new File(workDir);
    }

    @Override
    public void run() {
        try {
            this.protectionStrategie = NextGen.getProtectionStrategie();
            clearDirectory(rootDir);            
        } catch( Exception e ) {
            NextGen.LOGGER.log(Level.SEVERE, "Error while cleaning directory tres: "+rootDir.getAbsolutePath(), e);
        }
    }

    private void clearDirectory(File dir) {
        if( dir != null && dir.listFiles()!=null && dir.listFiles().length > 0 ) {
            for (File f : dir.listFiles()) {
                if (f.exists() && f.isFile() && !protectionStrategie.isProtected(f)) {
                    f.delete();
                }
                if (f.isDirectory()) {
                    clearDirectory(f);
                    if (f.isDirectory() && (f.listFiles() == null || f.listFiles().length == 0)) {
                        f.delete();
                    }
                }
            }
        }
    }

}
