package de.spraener.nxtgen.symfony;

import de.spraener.nxtgen.oom.cartridge.GeneratorGapTransformation;
import de.spraener.nxtgen.oom.model.*;
import de.spraener.nxtgen.oom.StereotypeHelper;

public class BaseControllerForRESTController extends BaseControllerForRESTControllerBase {

    @Override
    public void doTransformationIntern(MClass me) {
        new GeneratorGapTransformation().doTransformation(me);
    }
}
