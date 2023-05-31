package de.spraener.nextgen.vpplugin.oom;

import com.vp.plugin.model.IModelElement;
import com.vp.plugin.model.IStereotype;
import com.vp.plugin.model.ITaggedValue;
import org.apache.commons.lang3.StringUtils;

import java.io.PrintWriter;
import java.util.HashSet;
import java.util.Set;

public class PropertiesExporter {

    public static void exportStereotypes(PrintWriter pw, String indentation, IModelElement mElement) {
        IStereotype[] stereotypes = mElement.toStereotypeModelArray();
        if (stereotypes == null || stereotypes.length == 0) {
            return;
        }
        for (IStereotype sType : stereotypes) {
            exportStereotype(pw, indentation, sType, mElement);
        }
    }

    private static void exportStereotype(PrintWriter pw, String indentation, IStereotype sType, IModelElement mElement) {
        pw.printf("%sstereotype '%s'", indentation, sType.getName());
        if (mElement.getTaggedValues() != null) {
            pw.printf(", {\n");
            ITaggedValue[] taggedValues = mElement.getTaggedValues().toTaggedValueArray();
            if (taggedValues != null && taggedValues.length > 0) {
                for (ITaggedValue tv : taggedValues) {
                    if( tv.getValue()!=null && !"Unspecified".equalsIgnoreCase(tv.getValue().toString()) ) {
                        pw.printf("%s  taggedValue '%s', '%s'\n", indentation, tv.getName(), formatValue(tv.getValue()));
                    }
                }
            }
            pw.printf("%s}\n", indentation);
        } else {
            pw.println();
        }
    }

    private static String formatValue(Object value) {
        if( value == null ) {
            return "null";
        }
        if( value.toString().equalsIgnoreCase("true") ) {
            return "true";
        }
        if( value.toString().equalsIgnoreCase("false") ) {
            return "false";
        }
        return value.toString();
    }

    public static void exportProperties(PrintWriter pw, String indentation, IModelElement mElement, PropertyOverwriter... propOverwrite) {
        Set<String> overwrittenProps = new HashSet<>();
        if( propOverwrite!=null ) {
            for( PropertyOverwriter po : propOverwrite ) {
                pw.printf("%s%s '%s'\n", indentation, po.getKey(), po.getValue());
                overwrittenProps.add(po.getKey());
            }
        }
        exportProperty(pw, indentation, overwrittenProps, "name",  mElement.getName());
        exportProperty(pw, indentation, overwrittenProps, "documentation", toStringValue(mElement.getDocumentation()));
        exportProperty(pw, indentation, overwrittenProps, "id", mElement.getAddress());
        exportProperty(pw, indentation, overwrittenProps, "metaType", mElement.getModelType());
        exportStereotypes(pw, indentation, mElement);
    }

    private static void exportProperty(PrintWriter pw, String indentation, Set<String> overwrittenProps, String propName, String value) {
        if( !overwrittenProps.contains(propName) ) {
            pw.printf("%s%s '%s'\n", indentation, propName, value);
        }
    }

    private static String toStringValue(String str) {
        return str
                .replaceAll("\'", "\\\'")
                .replaceAll("\n", "\\\\n")
                .replaceAll("\"", "\\\"");
    }
}
