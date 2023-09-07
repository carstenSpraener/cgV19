
package de.spraener.nxtgen.symfony;

import de.spraener.nxtgen.model.ModelHelper;
import de.spraener.nxtgen.oom.OOModelBuilder;
import de.spraener.nxtgen.oom.model.*;

public class CreateServiceTrait extends CreateServiceTraitBase {

    @Override
    public void doTransformationIntern(MClass me) {
        MClass trait = null;
        try {
            trait = (MClass) ModelHelper.findByFQName(me.getModel(), me.getPackage().getFQName()+".Service.ControllerServiceTrait", ".");
        } catch( Exception e) {}
        if( trait == null ) {
            MPackage srvPkg = me.getPackage().findOrCreatePackage("Service");

            OOModelBuilder.createMClass(srvPkg, "ControllerServiceTrait",
                    c ->  OOModelBuilder.createStereotype(c, SymfonyStereotypes.PHPCNTRLSERVICETRAIT.getName())
                    );
        }
    }
}
