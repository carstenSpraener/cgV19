package de.spraener.nxtgen;

import de.spraener.nxtgen.model.Model;

import java.io.File;

public interface OnEmptyRootDir {
    void onEmptyRootDir(NextGen nextGen, File rootDir, Model m);
}
