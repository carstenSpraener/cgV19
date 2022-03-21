package de.spraener.nxtgen.cartridge.meta;

import de.spraener.nxtgen.Transformation;
import de.spraener.nxtgen.model.ModelElement;
import de.spraener.nxtgen.model.Stereotype;
import de.spraener.nxtgen.model.impl.StereotypeImpl;
import de.spraener.nxtgen.oom.model.MClass;
import de.spraener.nxtgen.oom.model.MClassRef;

public class CartridgeBaseForCartridgeTransformation extends CartridgeBaseForCartridgeTransformationBase {
    @Override
    public void doTransformationIntern(MClass mClass) {
        createBaseClass(mClass);
    }

    private void createBaseClass(MClass mClass) {
        MClass baseClass = mClass.getPackage().createMClass(mClass.getName()+"Base");
        Stereotype st = new StereotypeImpl(MetaCartridge.STYPE_CGV19CARTRIDGE_BASE);
        baseClass.getStereotypes().add(st);
        mClass.setInheritsFrom(new MClassRef(baseClass.getFQName()));
        baseClass.setModel(mClass.getModel());
    }
}
