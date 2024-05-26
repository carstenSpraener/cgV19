package de.spraener.nxtgen;

import de.spraener.nxtgen.model.Model;

import java.io.File;

/**
 * This interface is called after the models are loaded and
 * before generation starts.
 */
public interface AfterEmptyDir {
    void afterEmptyRootDir(NextGen nextGen, File rootDir, Model m);
}
