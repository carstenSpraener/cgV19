package de.spraener.nextgen.vpplugin.oom;

import com.vp.plugin.model.IAttribute;
import com.vp.plugin.model.IModelElement;

import java.io.PrintWriter;

public class AttributeExporter extends AttributeExporterBase {
    @Override
    protected void exportBody(OOMExporter exporter, PrintWriter pw, String indentation, IModelElement element) {
        super.exportBody(exporter, pw, indentation, element);
        IAttribute attr = (IAttribute)element;
        pw.printf("%stype '%s'\n", indentation, attr.getTypeAsString());
    }
}
