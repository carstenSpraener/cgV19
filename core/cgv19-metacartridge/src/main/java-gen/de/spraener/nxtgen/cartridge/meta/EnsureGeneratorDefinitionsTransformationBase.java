///THIS FILE IS GENERATED AS LONG AS THIS LINE EXISTS
package de.spraener.nxtgen.cartridge.meta;

import de.spraener.nxtgen.model.ModelElement;
import de.spraener.nxtgen.oom.model.*;
import de.spraener.nxtgen.oom.StereotypeHelper;
import de.spraener.nxtgen.oom.model.*;
import de.spraener.nxtgen.annotations.*;

@CGV19Transformation(
        requiredStereotype = "CodeGenerator",
        operatesOn = MClass.class
)
public abstract class EnsureGeneratorDefinitionsTransformationBase implements de.spraener.nxtgen.Transformation {

    @Override
    public void doTransformation(ModelElement element) {
        if( !(element instanceof MClass) ) {
            return;
        }
        if( !StereotypeHelper.hasStereotype(element, "CodeGenerator")) {
            return;
        }
        doTransformationIntern((MClass)element);
    }

    public abstract void doTransformationIntern(MClass modelElement);
}
