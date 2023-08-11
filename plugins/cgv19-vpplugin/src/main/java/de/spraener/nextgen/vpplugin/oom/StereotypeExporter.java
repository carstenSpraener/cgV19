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
        String defaultValue = tv.getDefaultValue();
        if( defaultValue != null ) {
            pw.printf("%s  defaultValue \"\"\"%s\"\"\"%n", indentation, defaultValue);
        }
        pw.printf("%s  typeID %d%n", indentation, tv.getType());
        pw.printf("%s  type '%s'%n", indentation, toTaggedValueType(tv.getType()));
        if( tv.getEnumerationValues() != null ) {
            pw.printf("%s  enumValues \"\"\"%n", indentation);
            for( String v : tv.getEnumerationValues() ) {
                pw.printf("%s    %s%n", indentation, v);
            }
            pw.printf("\"\"\"%n");
        }
        if( tv.getTaggedValueEnumeration() != null ) {
            pw.printf("%s  enumValues \"\"\"%n", indentation, tv.getTaggedValueEnumeration().getName());
            for( String v : tv.getTaggedValueEnumeration().getEnumerationValues() ) {
                pw.printf("%s    %s%n", indentation, v);
            }
            pw.printf("\"\"\"%n");

        }
        PropertiesExporter.exportProperties(pw,indentation+"  ", tv);
        pw.printf("%s}\n",indentation);
    }

    // See https://www.visual-paradigm.com/support/documents/pluginjavadoc/ chapter ITaggedValue
    private static final String[] TAGGED_VALUE_TYPE = new String[]{
            "String",       // 0
            "modelElement", // 1
            "adhoc-enum",   // 2
            "htmlText",     // 3
            "string",       // 4
            "integer",      // 5
            "float",        // 6
            "boolean",      // 7
            "date",         // 8
            "time",         // 9
            "currency"      // 10
    };
    private static String toTaggedValueType( int i) {
        if( i<0 ||i>= TAGGED_VALUE_TYPE.length) {
            return "unknown";
        } else {
            return TAGGED_VALUE_TYPE[i];
        }
    }
}
