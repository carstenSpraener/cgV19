package de.spraener.nextgen.vpplugin.oom;

import com.vp.plugin.model.IDependency;
import com.vp.plugin.model.IModelElement;

import java.io.PrintWriter;

public class DependencyExporter extends DependencyExporterBase {

    @Override
    protected void exportBody(OOMExporter exporter, PrintWriter pw, String indentation, IModelElement element) {
        IDependency dep = (IDependency) element;
        IModelElement target = dep.getTo();
        pw.printf("%starget '%s'\n", indentation, OOMExporter.getFQName(target));
    }
}
