package de.spraener.nxtgen.examples.generatorgap;

import de.spraener.nxtgen.annotations.CGV19Cartridge;

@CGV19Cartridge("GeneratorGap")
public class GeneratorGap extends GeneratorGapBase{
    public static final String NAME = "GeneratorGap";

    public GeneratorGap() {
        super();
    }

    @Override
    public String getName() {
        return NAME;
    }
}
