package de.spraener.nxtgen.incubator;

import java.io.InputStreamReader;

public interface BlueprintSupplier {
    String getContent(String path);

    InputStreamReader getInputStream(String resource);
}
