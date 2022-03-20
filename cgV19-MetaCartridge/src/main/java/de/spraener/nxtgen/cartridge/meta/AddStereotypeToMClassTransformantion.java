package de.spraener.nxtgen.cartridge.meta;

import de.spraener.nxtgen.Transformation;
import de.spraener.nxtgen.model.Model;
import de.spraener.nxtgen.model.ModelElement;
import de.spraener.nxtgen.model.impl.StereotypeImpl;
import de.spraener.nxtgen.oom.StereotypeHelper;
import de.spraener.nxtgen.oom.model.MClass;
import de.spraener.nxtgen.oom.model.OOModel;

public class AddStereotypeToMClassTransformantion implements Transformation {
    @Override
    public void doTransformation(ModelElement element) {
        if( element instanceof MClass ) {
            MClass mC = (MClass)element;
            if(!StereotypeHelper.hasStereotye(mC, MetaCartridge.STEREOTYPE_NAME ) ) {
                StereotypeImpl sType = new StereotypeImpl(MetaCartridge.STEREOTYPE_NAME);
                mC.addStereotypes(sType);
            }
        }
    }
}
