///THIS FILE IS GENERATED AS LONG AS THIS LINE EXISTS
package de.spraener.nxtgen.cartridge.meta;

import de.spraener.nxtgen.model.ModelElement;
import de.spraener.nxtgen.oom.model.*;
import de.spraener.nxtgen.oom.StereotypeHelper;

public abstract class CartridgeBaseForCartridgeTransformationBase implements de.spraener.nxtgen.Transformation {

    @Override
    public void doTransformation(ModelElement element) {
        if( !(element instanceof MClass) ) {
            return;
        }
        if( !StereotypeHelper.hasStereotype(element, "cgV19Cartridge")) {
            return;
        }
        doTransformationIntern((MClass)element);
    }

    public abstract void doTransformationIntern(MClass modelElement);
}
