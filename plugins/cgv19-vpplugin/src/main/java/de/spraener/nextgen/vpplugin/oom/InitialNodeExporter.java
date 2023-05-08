package de.spraener.nextgen.vpplugin.oom;

import com.vp.plugin.model.IInitialNode;
import com.vp.plugin.model.IModelElement;
import com.vp.plugin.model.IRelationship;

import java.io.PrintWriter;

public class InitialNodeExporter extends InitialNodeExporterBase {

    @Override
    protected void exportBody(OOMExporter exporter, PrintWriter pw, String indentation, IModelElement element) {
        super.exportBody(exporter, pw, indentation, element);
        IInitialNode initNode = (IInitialNode)element;
        pw.printf("%soutgoing {\n", indentation);
        for(IRelationship rel : element.toFromRelationshipArray()) {
            exporter.exportElement(pw, indentation+"  ", rel);
        }
        pw.printf("%s}\n", indentation);
    }
}
