package de.spraener.nxtgen.cartridge.meta;

import de.spraener.nxtgen.model.impl.StereotypeImpl;
import de.spraener.nxtgen.oom.StereotypeHelper;
import de.spraener.nxtgen.oom.model.MClass;
import de.spraener.nxtgen.oom.model.MPackage;

public class AddStereotypeToMClassTransformantion extends AddStereotypeToMClassTransformantionBase {
    @Override
    public void doTransformationIntern(MClass  mC) {
        if (mC.getStereotypes().isEmpty() &&isRootMClass(mC)) {
            StereotypeImpl sType = new StereotypeImpl(MetaCartridge.STEREOTYPE_NAME);
            mC.addStereotypes(sType);
        }
    }

    private boolean isRootMClass(MClass mC) {
        MPackage mp = mC.getPackage();
        if( mp == null || StereotypeHelper.hasStereotye(mp, MetaCartridge.STEREOTYPE_MODEL_ROOT)) {
            return true;
        } else {
            return false;
        }
    }
}
