package de.spraener.nextgen.vpplugin.oom;

import com.vp.plugin.model.IModelElement;
import com.vp.plugin.model.IStereotype;
import com.vp.plugin.model.ITaggedValue;
import com.vp.plugin.model.ITaggedValueDefinition;

import java.io.PrintWriter;

public class StereotypeExporter  implements Exporter {
    @Override
    public void export(OOMExporter exporter, PrintWriter pw, String indentation, IModelElement element) {
        IStereotype sType = (IStereotype)element;
        pw.printf("%smClass {\n",indentation);
        pw.printf("%s  stereotype 'Stereotype'\n", indentation);
        PropertiesExporter.exportProperties(pw,indentation+"  ", element);
        if( sType.getTaggedValueDefinitions() != null ) {
            for (ITaggedValueDefinition tv : sType.getTaggedValueDefinitions().toTaggedValueDefinitionArray()) {
                exportTaggedValue(pw, indentation + "  ", tv);
            }
        }
        pw.printf("%s}\n",indentation);
    }

    private void exportTaggedValue(PrintWriter pw, String indentation, ITaggedValueDefinition tv) {
        pw.printf("%smAttribute {\n", indentation);
        PropertiesExporter.exportProperties(pw,indentation+"  ", tv);
        pw.printf("%s}\n",indentation);
    }
}
