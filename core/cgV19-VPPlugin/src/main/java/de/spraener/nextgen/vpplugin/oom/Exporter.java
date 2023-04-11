package de.spraener.nextgen.vpplugin.oom;

import com.vp.plugin.model.IModelElement;

import java.io.PrintWriter;

public interface Exporter {
    void export(PrintWriter pw, String indentation, IModelElement element);
}
