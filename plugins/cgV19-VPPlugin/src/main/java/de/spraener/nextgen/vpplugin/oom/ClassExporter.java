package de.spraener.nextgen.vpplugin.oom;

import com.vp.plugin.model.*;

import java.io.PrintWriter;

import static de.spraener.nextgen.vpplugin.oom.PropertiesExporter.exportProperties;

public class ClassExporter extends ClassExporterBase {
    @Override
    protected void exportBody(OOMExporter exporter, PrintWriter pw, String indentation, IModelElement element) {
        super.exportBody(exporter, pw, indentation, element);
        IClass clazz = (IClass) element;
        for (IRelationshipEnd rel : clazz.toToRelationshipEndArray()) {
            exportRelationshipEnd(pw, indentation, rel);
        }
        for (IRelationshipEnd rel : clazz.toFromRelationshipEndArray()) {
            exportRelationshipEnd(pw, indentation, rel);
        }
        for (IRelationship rel : clazz.toFromRelationshipArray()) {
            exportRelationship(pw, indentation, rel);
        }
    }


    private void exportRelationshipEnd(PrintWriter pw, String indent, IRelationshipEnd relEnd) {
        IRelationship rel = relEnd.getEndRelationship();
        if (rel instanceof IAssociation) {
            exportAssociation(pw, indent, (IAssociation) rel, relEnd.getOppositeEnd());
        } else {
            pw.printf("%smRelation {\n", indent);
            PropertiesExporter.exportProperties(pw, indent + "  ", rel);
            pw.printf("%s}\n", indent);
        }
    }


    private void exportRelationship(PrintWriter pw, String indent, IRelationship rel) {
        pw.printf("%smRelation {\n", indent);
        PropertiesExporter.exportProperties(pw, indent + "  ", rel);
        pw.printf("%s}\n", indent);
    }

    private void exportAssociation(PrintWriter pw, String indent, IAssociation assoc, IRelationshipEnd relEnd) {
        if (relEnd instanceof IAssociationEnd) {
            IAssociationEnd assocEnd = (IAssociationEnd) relEnd;
            if( assocEnd.getNavigable()==2 ) {
                return;
            }
            pw.printf("%smAssociation {\n", indent);
            PropertiesExporter.exportProperties(pw, indent + "  ", assocEnd);
            pw.printf("%s  type '%s'\n", indent, assocEnd.getAggregationKind());
            pw.printf("%s  multiplicity '%s'\n", indent, assocEnd.getMultiplicity());
            pw.printf("%s  target '%s'\n", indent, OOMExporter.getFQName(assocEnd.getModelElement()));
            pw.printf("%s  navigable '%s'\n", indent, assocEnd.getNavigable()==2 ? "false" : "true");
            pw.printf("%s}\n", indent);
        }
    }

}
