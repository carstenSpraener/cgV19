package de.spraener.nxtgen.cartridge.meta;

import de.spraener.nxtgen.Transformation;
import de.spraener.nxtgen.model.ModelElement;
import de.spraener.nxtgen.model.Stereotype;
import de.spraener.nxtgen.model.impl.StereotypeImpl;
import de.spraener.nxtgen.oom.StereotypeHelper;
import de.spraener.nxtgen.oom.model.MClass;

public class CartridgeServicesLocatorTransformation implements Transformation {

    @Override
    public void doTransformation(ModelElement element) {
        if( !(element instanceof MClass) ) {
            return;
        }
        MClass mClass = (MClass)element;
        if( !mClass.hasStereotype(MetaCartridge.STYPE_CGV19CARTRIDGE) ) {
            return;
        }
        MClass service = mClass.getPackage().createMClass("CartridgeServiceLocatior");
        service.setModel(mClass.getModel());
        StereotypeImpl sType = new StereotypeImpl(MetaCartridge.STYPE_CGV19CARTRIDGE_SERVICE_DEFINITION);
        service.getStereotypes().add(sType);
        sType.setTaggedValue(MetaCartridge.TV_CARTRIDGE_CLASS, mClass.getFQName());
    }
}
