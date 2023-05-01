package de.spraener.nxtgen.filestrategies;

import de.spraener.nxtgen.model.ModelElement;

import java.io.File;
import java.nio.file.Path;

/**
 * Open a File where the Generator should write the code to.
 * 
 */
public interface ToFileStrategy {
    File open();
}
