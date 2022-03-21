package de.spraener.nxtgen.cartridge.meta;

import de.spraener.nxtgen.Transformation;
import de.spraener.nxtgen.model.ModelElement;
import de.spraener.nxtgen.model.Stereotype;
import de.spraener.nxtgen.model.impl.StereotypeImpl;
import de.spraener.nxtgen.oom.model.MClass;
import de.spraener.nxtgen.oom.model.MClassRef;

public class CartridgeBaseForCartridgeTransformation implements Transformation {
    @Override
    public void doTransformation(ModelElement element) {
        if( !(element instanceof MClass) ) {
            return;
        }
        MClass mClass = (MClass)element;
        if( !mClass.hasStereotype(MetaCartridge.STYPE_CGV19CARTRIDGE)) {
            return;
        }
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
