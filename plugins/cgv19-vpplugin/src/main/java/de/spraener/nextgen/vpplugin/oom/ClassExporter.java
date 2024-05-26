package de.spraener.nextgen.vpplugin.oom;

import com.vp.plugin.model.*;
import de.spraener.nextgen.vpplugin.CgV19Plugin;

import java.io.PrintWriter;

import static de.spraener.nextgen.vpplugin.oom.PropertiesExporter.exportProperties;

public class ClassExporter extends ClassExporterBase {
    @Override
    protected void exportBody(OOMExporter exporter, PrintWriter pw, String indentation, IModelElement element) {
        super.exportBody(exporter, pw, indentation, element);
        IClass clazz = (IClass) element;
        for (IRelationshipEnd rel : clazz.toToRelationshipEndArray()) {
            exportRelationshipEnd(exporter, pw, indentation, rel, "to");
        }
        for (IRelationshipEnd rel : clazz.toFromRelationshipEndArray()) {
            exportRelationshipEnd(exporter, pw, indentation, rel, "from");
        }
        for (IRelationship rel : clazz.toFromRelationshipArray()) {
            if( !"Generalization".equals(rel.getModelType()) ) {
                exporter.exportElement(pw, indentation, rel);
            }
        }
        for (IRelationship rel : clazz.toToRelationshipArray()) {
            if( !"Generalization".equals(rel.getModelType()) ) {
                exporter.exportElement(pw, indentation, rel);
            } else {
                exportExtends(pw,indentation, rel);
            }
        }
    }

    private void exportExtends(PrintWriter pw, String indent, IRelationship rel) {
        pw.printf("%srelation {\n", indent);
        pw.printf("%s  type='extends'\n", indent);
        pw.printf("%s  targetXmID='%s'\n", indent, rel.getFrom().getAddress());
        pw.printf("%s  targetType '%s'\n", indent, OOMExporter.getFQName(rel.getFrom()));
        pw.printf("%s}\n", indent);
    }


    private void exportRelationshipEnd(OOMExporter exporter,PrintWriter pw, String indent, IRelationshipEnd relEnd, String direction) {
        IRelationship rel = relEnd.getEndRelationship();
        if (rel instanceof IAssociation) {
            exportAssociation(exporter, pw, indent, (IAssociation) rel, relEnd.getOppositeEnd(), direction);
        }
    }

    private void exportAssociation(OOMExporter exporter, PrintWriter pw, String indent, IAssociation assoc, IRelationshipEnd relEnd, String direction) {
        if (relEnd instanceof IAssociationEnd) {
            IAssociationEnd assocEnd = (IAssociationEnd) relEnd;
            if( assocEnd.getNavigable()==2 ) {
                return;
            }
            pw.printf("%smAssociation {\n", indent);
            PropertiesExporter.exportProperties(pw, indent + "  ", assocEnd);
            IAssociationEnd opposite = (IAssociationEnd)assocEnd.getOppositeEnd();

            pw.printf("%s  assocId '%s'\n", indent, assocEnd.getAddress());
            pw.printf("%s  direction '%s'\n", indent, direction);
            pw.printf("%s  opositeAttribute '%s'\n", indent, opposite.getName());
            pw.printf("%s  opositeMultiplicity '%s'\n", indent, opposite.getMultiplicity());
            pw.printf("%s  associationType '%s'\n", indent, toAssociationType(assocEnd));
            pw.printf("%s  type '%s'\n", indent, OOMExporter.getFQName(assocEnd.getModelElement()));
            pw.printf("%s  multiplicity '%s'\n", indent, assocEnd.getMultiplicity());
            pw.printf("%s  navigable '%s'\n", indent, assocEnd.getNavigable()==2 ? "false" : "true");
            pw.printf("%s  composite '%s'\n", indent, isComposited(assocEnd) ? "true" : "false");
            pw.printf("%s  oppositeAggregationKind '%s'\n", indent, opposite.getAggregationKind());
            for( IModelElement child : assoc.toChildArray() ) {
                exporter.exportElement(pw, indent+"  ", child);
            }
            PropertiesExporter.exportStereotypes(pw, indent+"  ", assoc);
            pw.printf("%s}\n", indent);
        }
    }

    private boolean isComposited(IAssociationEnd assocEnd) {
        return "composite".equalsIgnoreCase(((IAssociationEnd)assocEnd.getOppositeEnd()).getAggregationKind());
    }

    private String toAssociationType(IAssociationEnd assocEnd) {
        String thisMulti = assocEnd.getMultiplicity();
        String oppositeMulti = ((IAssociationEnd)assocEnd.getOppositeEnd()).getMultiplicity();
        if( thisMulti.contains("*") ) {
            if( oppositeMulti.contains("*") ) {
                return "ManyToMany";
            } else {
                return "OneToMany";
            }
        } else {
            if( oppositeMulti.contains("*") ) {
                return "ManyToOne";
            } else {
                return "OneToOne";
            }
        }
    }

}
