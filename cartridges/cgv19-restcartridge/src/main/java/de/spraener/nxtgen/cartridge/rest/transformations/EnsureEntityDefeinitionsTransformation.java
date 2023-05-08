package de.spraener.nxtgen.cartridge.rest.transformations;

import de.spraener.nxtgen.oom.model.*;
import de.spraener.nxtgen.oom.StereotypeHelper;

public class EnsureEntityDefeinitionsTransformation extends EnsureEntityDefeinitionsTransformationBase {

    @Override
    public void doTransformationIntern(MClass me) {
        ResourceToEntity.ensureEntityDefinition((MClass) me);
    }
}
