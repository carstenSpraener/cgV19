package de.spraener.nextgen.vpplugin.oom;

import com.vp.plugin.model.IModelElement;
import com.vp.plugin.model.IStereotype;
import com.vp.plugin.model.ITaggedValue;

import java.io.PrintWriter;

public class PropertiesExporter {

    private static void exportStereotypes(PrintWriter pw, String indentation, IModelElement mElement) {
        IStereotype[] stereotypes = mElement.toStereotypeModelArray();
        if (stereotypes == null || stereotypes.length == 0) {
            return;
        }
        for (IStereotype sType : stereotypes) {
            exportStereotype(pw, indentation, sType);
        }
    }

    private static void exportStereotype(PrintWriter pw, String indentation, IStereotype sType) {
        pw.printf("%sstereotype '%s'", indentation, sType.getName());
        if (sType.getTaggedValues() != null) {
            pw.printf(" {\n");
            ITaggedValue[] taggedValues = sType.getTaggedValues().toTaggedValueArray();
            if (taggedValues != null && taggedValues.length > 0) {
                pw.printf("%s  taggedValues {\n", indentation);
                for (ITaggedValue tv : taggedValues) {
                    pw.printf("%s    taggedValue {\n", indentation);
                    pw.printf("%s      name '%s'\n", indentation, tv.getName());
                    pw.printf("%s      value '%s'\n", indentation, tv.getValue());
                    pw.printf("%s    }\n", indentation);
                }
                pw.printf("%s  }\n", indentation);
            }
            pw.printf("%s}\n", indentation);
        } else {
            pw.println();
        }
    }

    public static void exportProperties(PrintWriter pw, String indentation, IModelElement mElement) {
        pw.printf("%sname '%s'\n", indentation, mElement.getName());
        pw.printf("%sdocumentation '%s'\n", indentation, toStringValue(mElement.getDocumentation()));
        pw.printf("%saddress '%s'\n", indentation, mElement.getAddress());
        pw.printf("%sxmiID '%s'\n", indentation, mElement.getId());
        pw.printf("%smetaType '%s'\n", indentation, mElement.getModelType());

        exportStereotypes(pw, indentation, mElement);
    }

    private static String toStringValue(String str) {
        return str
                .replaceAll("\'", "\\\'")
                .replaceAll("\n", "\\\\n")
                .replaceAll("\"", "\\\"");
    }
}
