package de.spraener.nextgen.vpplugin.oom;

import com.vp.plugin.model.IOperation;
import com.vp.plugin.model.IModelElement;
import java.io.PrintWriter;

public class OperationExporter extends OperationExporterBase {

    @Override
    protected void exportBody(OOMExporter exporter, PrintWriter pw, String indentation, IModelElement element) {
        super.exportBody(exporter, pw, indentation, element);
        IOperation attr = (IOperation)element;
        OOMExporter.exportType(pw, indentation, attr.getReturnTypeAsString(), attr.getReturnTypeAsElement(), null);
        pw.printf("%svisibility '%s'\n", indentation, attr.getVisibility());
        pw.printf("%stypeModifier '%s'\n", indentation, attr.getTypeModifier());
        pw.printf("%sscope '%s'\n", indentation, attr.getScope());
        pw.printf("%sisAbstract '%s'\n", indentation, ""+attr.isAbstract());
    }

}
