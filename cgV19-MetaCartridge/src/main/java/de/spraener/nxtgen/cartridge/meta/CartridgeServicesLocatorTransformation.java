package de.spraener.nxtgen.cartridge.meta;

import de.spraener.nxtgen.model.impl.StereotypeImpl;
import de.spraener.nxtgen.oom.model.MClass;

public class CartridgeServicesLocatorTransformation extends CartridgeServicesLocatorTransformationBase {

    @Override
    public void doTransformationIntern(MClass mClass) {
        MClass service = mClass.getPackage().createMClass("CartridgeServiceLocatior");
        service.setModel(mClass.getModel());
        StereotypeImpl sType = new StereotypeImpl(MetaCartridge.STYPE_CGV19CARTRIDGE_SERVICE_DEFINITION);
        service.getStereotypes().add(sType);
        sType.setTaggedValue(MetaCartridge.TV_CARTRIDGE_CLASS, mClass.getFQName());
    }
}
