package de.spraener.nxtgen.cartridge.rest.transformations;

import de.spraener.nxtgen.oom.model.*;

public class EnsureEntityDefinitionsTransformation extends EnsureEntityDefinitionsTransformationBase {

    @Override
    public void doTransformationIntern(MClass me) {
        ResourceToEntity.ensureEntityDefinition((MClass) me);
    }
}
