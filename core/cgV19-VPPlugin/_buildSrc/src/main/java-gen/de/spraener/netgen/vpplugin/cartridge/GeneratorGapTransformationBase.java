///THIS FILE IS GENERATED AS LONG AS THIS LINE EXISTS
package de.spraener.nextgen.vpplugin.cartridge;

import de.spraener.nxtgen.model.ModelElement;
import de.spraener.nxtgen.oom.model.*;
import de.spraener.nxtgen.oom.StereotypeHelper;

public abstract class GeneratorGapTransformationBase implements de.spraener.nxtgen.Transformation {

    @Override
    public void doTransformation(ModelElement element) {
        if( !(element instanceof MClass) ) {
            return;
        }
        doTransformationIntern((MClass)element);
    }

    public abstract void doTransformationIntern(MClass modelElement);
}
