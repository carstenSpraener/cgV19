package de.spraener.nxtgen.cartridge.rest.transformations;

import de.spraener.nxtgen.Transformation;
import de.spraener.nxtgen.cartridge.rest.RESTStereotypes;
import de.spraener.nxtgen.model.ModelElement;
import de.spraener.nxtgen.model.Stereotype;
import de.spraener.nxtgen.model.impl.StereotypeImpl;
import de.spraener.nxtgen.oom.StereotypeHelper;
import de.spraener.nxtgen.oom.model.MAttribute;
import de.spraener.nxtgen.oom.model.MClass;
import de.spraener.nxtgen.oom.model.MPackage;

public class ResourceToTSType implements Transformation {

    @Override
    public void doTransformation(ModelElement element) {
        if( !(element instanceof MClass) ) {
            return;
        }
        if( !((MClass) element).hasStereotype(RESTStereotypes.RESSOURCE.getName()) ) {
            return;
        }
        create((MClass)element);
    }

    public MClass create(MClass mClass) {
        MPackage tsTypePkg = mClass.getPackage().findOrCreatePackage("tstype");
        final MClass tsType = tsTypePkg.createMClass(mClass.getName());

        Stereotype sType = new StereotypeImpl(RESTStereotypes.TSTYPE.getName());
        tsType.getStereotypes().add(sType);

        mClass.getAttributes().forEach( attr -> {
            MAttribute tsAttr = tsType.createAttribute(attr.getName(), toTSType(attr));
        });

        return tsType;
    }

    private String toTSType(MAttribute attr) {
        String t = attr.getType().toLowerCase();
        t = t.replaceAll("java.lang.", "");
        if( "long".equals(t)) {
            return "number";
        } else if( "integer".equals(t) || "int".equals(t)) {
            return "number";
        } else if( "double".equals(t) || "float".equals(t)) {
            return "number";
        } else if( "boolean".equals(t)) {
            return "boolean";
        } else if( "string".equals(t)) {
            return "string";
        } else if( "date".equals(t)) {
            return "Date";
        } else if( isLinkReference(attr)){
            return "ResourceLink";
        } else {
            return "any";
        }
    }

    private boolean isLinkReference(MAttribute attr) {
        return StereotypeHelper.hasStereotype(attr, RESTStereotypes.LINK.getName());
    }
}
