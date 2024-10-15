package de.spraener.nextgen.vpplugin.oom;

import com.vp.plugin.model.IAttribute;
import com.vp.plugin.model.IModelElement;

import java.io.PrintWriter;
import java.util.Set;

public class AttributeExporter extends AttributeExporterBase {
    private static Set<String> STANDARD_TYPES = Set.of(
            "string", "integer", "boolean", "decimal"
    );
    @Override
    protected void exportBody(OOMExporter exporter, PrintWriter pw, String indentation, IModelElement element) {
        super.exportBody(exporter, pw, indentation, element);
        IAttribute attr = (IAttribute)element;
        pw.printf("%stype '%s'\n", indentation, formatType(attr.getTypeAsString()) );
        pw.printf("%svisibility '%s'\n", indentation, attr.getVisibility());
        pw.printf("%smultiplicity '%s'\n", indentation, attr.getMultiplicity());
        pw.printf("%stypeModifier '%s'\n", indentation, attr.getTypeModifier());
        if( "1".equals(attr.getMultiplicity()) ) {
            pw.printf("%srequired 'true'\n", indentation);
        }
    }

    public static String formatType(String typeAsString) {
        if( typeAsString == null ) {
            return "null";
        }
        if( typeAsString.equalsIgnoreCase("date")) {
            // FIXME: No Java-Knowledge inside the plugin
            return "java.util.Date";
        }
        if( typeAsString.equalsIgnoreCase("int")) {
            // FIXME: No Java-Knowledge inside the plugin
            return "Integer";
        }
        if( STANDARD_TYPES.contains(typeAsString) ) {
            return typeAsString.substring(0,1).toUpperCase()+typeAsString.substring(1);
        }
        return typeAsString;
    }
}
