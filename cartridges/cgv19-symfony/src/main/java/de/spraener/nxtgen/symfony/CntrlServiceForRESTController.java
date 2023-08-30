package de.spraener.nxtgen.symfony;

import de.spraener.nxtgen.model.impl.StereotypeImpl;
import de.spraener.nxtgen.oom.OOModelBuilder;
import de.spraener.nxtgen.oom.cartridge.GeneratorGapTransformation;
import de.spraener.nxtgen.oom.model.*;
import de.spraener.nxtgen.oom.StereotypeHelper;

public class CntrlServiceForRESTController extends CntrlServiceForRESTControllerBase {

    @Override
    public void doTransformationIntern(MClass me) {
        MPackage srvPkg = me.getPackage().findOrCreatePackage("Service");
        String serviceName = me.getName().replace("Controller", "CntrlService" );
        OOModelBuilder.createMClass(srvPkg, serviceName,
            c -> c.getStereotypes().add(new StereotypeImpl(SymfonyStereotypes.PHPCNTRLSERVICE.getName())),
            c -> GeneratorGapTransformation.setOriginalClass(c, me)
        );
    }
}
