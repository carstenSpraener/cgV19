package de.spraener.nextgen.vpplugin.oom;

import com.vp.plugin.model.IActivityAction;
import com.vp.plugin.model.IModelElement;
import com.vp.plugin.model.IRelationship;
import de.spraener.nextgen.vpplugin.CgV19Plugin;

import java.io.PrintWriter;
import java.util.List;

public class ActionExporter extends ActionExporterBase {
    @Override
    protected List<IModelElement> listChilds(IModelElement element) {
        List<IModelElement> childList = super.listChilds(element);
        return childList;
    }

    @Override
    protected void exportBody(OOMExporter exporter, PrintWriter pw, String indentation, IModelElement element) {
        super.exportBody(exporter, pw, indentation, element);
        if( element instanceof IActivityAction ) {
            IActivityAction activity = (IActivityAction) element;
            pw.printf("%sincoming {\n", indentation);
            for( IRelationship rel : activity.toToRelationshipArray() ) {
                exporter.exportElement(pw,indentation+"  ", rel);
            }
            pw.printf("%s}\n", indentation);
            pw.printf("%soutgoing {\n", indentation);
            for( IRelationship rel : activity.toFromRelationshipArray() ) {
                exporter.exportElement(pw,indentation+"  ", rel);
            }
            pw.printf("%s}\n", indentation);
        } else {
            CgV19Plugin.log("Unknown type '"+element.getClass().getName()+"' in ActionExporter.listChilds().");
        }
    }
}
