package de.spraener.nextgen.vpplugin.oom;

import com.vp.plugin.model.IModelElement;

import java.io.PrintWriter;

public class PackageExporter extends PackageExporterBase{
    @Override
    protected void exportBody(OOMExporter exporter, PrintWriter pw, String indentation, IModelElement element) {
        super.exportBody(exporter, pw, indentation, element);
    }
}
