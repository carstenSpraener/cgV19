package de.spraener.nxtgen.oom.cartridge;

import de.spraener.nxtgen.Transformation;
import de.spraener.nxtgen.annotations.CGV19Cartridge;

import java.util.List;

@CGV19Cartridge("MDOomCartridge")
public class OOMMetaCartridge extends MDOomCartridgeBase{
    public static final String NAME = "MDOomCartridge";

    public OOMMetaCartridge() {
        super();
    }

    @Override
    public String getName() {
        return NAME;
    }

    @Override
    public List<Transformation> getTransformations() {
        List<Transformation> tList = super.getTransformations();
        tList.add(new GeneratorGapTransformation());
        return tList;
    }

}
