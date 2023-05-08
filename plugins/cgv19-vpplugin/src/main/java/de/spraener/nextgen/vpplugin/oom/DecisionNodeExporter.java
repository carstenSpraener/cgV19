package de.spraener.nextgen.vpplugin.oom;

import com.vp.plugin.model.IDecisionNode;
import com.vp.plugin.model.IModelElement;
import com.vp.plugin.model.IRelationship;

import java.io.PrintWriter;

public class DecisionNodeExporter extends DecisionNodeExporterBase {
    @Override
    protected void exportBody(OOMExporter exporter, PrintWriter pw, String indentation, IModelElement element) {
        super.exportBody(exporter, pw, indentation, element);
        pw.printf("%sincoming {\n", indentation);
        exportIncoming(exporter, pw,indentation, (IDecisionNode)element);
        pw.printf("%s}\n", indentation);
        pw.printf("%soutgoing {\n", indentation);
        exportOutgoing(exporter, pw,indentation, (IDecisionNode)element);
        pw.printf("%s}\n", indentation);
    }

    private void exportIncoming(OOMExporter exporter, PrintWriter pw, String indentation, IDecisionNode element) {
        for(IRelationship rel : element.toToRelationshipArray() ) {
            exporter.exportElement(pw, indentation+"  ", rel);
        }
    }

    private void exportOutgoing(OOMExporter exporter, PrintWriter pw, String indentation, IDecisionNode element) {
        for(IRelationship rel : element.toFromRelationshipArray() ) {
            exporter.exportElement(pw, indentation+"  ", rel);
        }
    }
}
