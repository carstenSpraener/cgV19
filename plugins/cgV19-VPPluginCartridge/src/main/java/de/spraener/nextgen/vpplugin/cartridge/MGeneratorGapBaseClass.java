package de.spraener.nextgen.vpplugin.cartridge;

import de.spraener.nxtgen.oom.model.MClass;

public class MGeneratorGapBaseClass extends MClass {

    private final MClass originalClass;

    public MGeneratorGapBaseClass(MClass originalClass ) {
        this.originalClass = originalClass;
    }

    public MClass getOriginalClass() {
        return originalClass;
    }

}
