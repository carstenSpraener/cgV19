package de.spraener.nextgen.vpplugin.oom;

import com.vp.plugin.model.IModelElement;

import java.io.PrintWriter;

public interface Exporter {
    void export(OOMExporter exporter, PrintWriter pw, String indentation, IModelElement element);
}
