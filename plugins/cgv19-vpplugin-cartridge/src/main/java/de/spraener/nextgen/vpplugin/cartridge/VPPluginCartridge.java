package de.spraener.nextgen.vpplugin.cartridge;


import de.spraener.nxtgen.Transformation;
import de.spraener.nxtgen.annotations.CGV19Component;
import de.spraener.nxtgen.annotations.CGV19Transformation;
import de.spraener.nxtgen.model.ModelElement;
import de.spraener.nxtgen.oom.cartridge.GeneratorGapTransformation;
import de.spraener.nxtgen.oom.model.MClass;

import java.util.List;

@CGV19Component
public class VPPluginCartridge extends VPPluginCartridgeBase{

    public VPPluginCartridge() {
        super();
    }

    @Override
    public String getName() {
        return "VPPluginCartridge";
    }

    @CGV19Transformation(
            requiredStereotype = "Exporter",
            operatesOn = MClass.class
    )
    public void pojoGeneratorGapTransformation(ModelElement me) {
        MClass mc = (MClass) me;
        new GeneratorGapTransformation().doTransformation(mc);
    }
}
