///THIS FILE IS GENERATED AS LONG AS THIS LINE EXISTS
package de.spraener.nxtgen.cartridge.meta;

import de.spraener.nxtgen.model.ModelElement;
import de.spraener.nxtgen.oom.model.*;
import de.spraener.nxtgen.oom.StereotypeHelper;
import de.spraener.nxtgen.oom.model.*;
import de.spraener.nxtgen.annotations.*;

@CGV19Transformation(
        requiredStereotype = "ModelRoot",
        operatesOn = MPackage.class
)
public abstract class RemoveModelRootPackageBase implements de.spraener.nxtgen.Transformation {

    @Override
    public void doTransformation(ModelElement element) {
        if( !(element instanceof MPackage) ) {
            return;
        }
        if( !StereotypeHelper.hasStereotype(element, "ModelRoot")) {
            return;
        }
        doTransformationIntern((MPackage)element);
    }

    public abstract void doTransformationIntern(MPackage modelElement);
}
