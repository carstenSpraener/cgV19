package de.spraener.nxtgen.filestrategies;

import java.io.File;

/**
 * Open a File where the Generator should write the code to.
 * 
 */
public interface ToFileStrategy {
    File open();
    File open(String workingDir);
}
