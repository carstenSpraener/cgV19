package de.spraener.nextgen.vpplugin.oom;

import com.vp.plugin.model.IControlFlow;
import com.vp.plugin.model.IModelElement;

import java.io.PrintWriter;
import java.util.List;

public class ControlFlowExporter extends ControlFlowExporterBase {
    @Override
    protected void exportBody(OOMExporter exporter, PrintWriter pw, String indentation, IModelElement element) {
        super.exportBody(exporter, pw, indentation, element);
        IControlFlow cFlow = (IControlFlow)element;
        pw.printf("%sguard '%s'\n", indentation, cFlow.getGuard());
        pw.printf("%ssource '%s'\n", indentation, cFlow.getFrom().getName());
        pw.printf("%ssourceID '%s'\n", indentation, cFlow.getFrom().getAddress());
        pw.printf("%starget '%s'\n", indentation, cFlow.getTo().getName());
        pw.printf("%stargetID '%s'\n", indentation, cFlow.getTo().getAddress());
    }

    @Override
    protected List<IModelElement> listChilds(IModelElement element) {
        List<IModelElement> childs = super.listChilds(element);
        IControlFlow cFlow = (IControlFlow)element;
        childs.addAll(List.of(cFlow.toFromRelationshipArray()));
        childs.addAll(List.of(cFlow.toToRelationshipArray()));
        return childs;
    }
}
