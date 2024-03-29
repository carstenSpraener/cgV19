package de.spraener.nextgen.vpplugin.oom;

import com.vp.plugin.model.IModelElement;
import com.vp.plugin.model.IParameter;

import java.io.PrintWriter;

public class ParameterExporter extends ParameterExporterBase {

    protected void exportBody(OOMExporter exporter, PrintWriter pw, String indentation, IModelElement element) {
        IParameter param = (IParameter) element;
        pw.printf("%stype '%s'\n", indentation, AttributeExporter.formatType(param.getTypeAsString()) );
    }
}
