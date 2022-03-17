package de.csp.cgv19.mdplugin.oom;

import com.nomagic.uml2.ext.magicdraw.classes.mdkernel.*;
import com.nomagic.uml2.ext.magicdraw.classes.mdkernel.impl.LiteralIntegerImpl;

import java.util.List;

public class OOMExportSupport {

    static String getStringValue(List<ValueSpecification> valueList) {
        if( valueList==null || valueList.isEmpty() ) {
            return "null";
        }
        StringBuffer sb = new StringBuffer();
        for( ValueSpecification tv : valueList ) {
            sb.append(readTaggedValueAsString(tv));
        }
        return sb.toString();
    }

    public static String getSlotName(Slot taggedValue ) {
        StructuralFeature vSpec = taggedValue.getDefiningFeature();
        return vSpec.getName();
    }

    static String readTaggedValueAsString(Object tv) {
        if (tv instanceof LiteralString) {
            return ((LiteralString) tv).getValue();
        } else if (tv instanceof LiteralBoolean) {
            return ((LiteralBoolean) tv).isValue() ? "true" : "false";
        } else if (tv instanceof InstanceValue) {
            InstanceValue tvValue = (InstanceValue) tv;
            return tvValue.getInstance().getName();
        } else if (tv instanceof LiteralInteger) {
            LiteralInteger tvValue = (LiteralInteger) tv;
            return ""+tvValue.getValue();
        } else if (tv instanceof ElementValue) {
            ElementValue ev = (ElementValue) tv;
            Element e = (Element) ev.getElement();
            if (e instanceof NamedElement) {
                return ((NamedElement) e).getQualifiedName();
            } else {
                return e.getHumanName();
            }
        } else {
            return tv.toString();
        }
    }

    static String toTypeName( String mdTypeName) {
        String typeName = mdTypeName.replace("UML Standard Profile::UML2 Metamodel::PrimitiveTypes::", "");
        typeName = typeName.replaceAll("::", ".");
        return typeName;
    }
}
