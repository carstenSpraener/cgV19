package de.csp.nxtgen;

import java.io.File;

public interface ProtectionStrategie {
    String GENERATED_LINE = "THIS FILE IS GENERATED AS LONG AS THIS LINE EXISTS";

    boolean isProtected(File f);
}
