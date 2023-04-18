//THIS FILE IS GENERATED AS LONG AS THIS LINE EXISTS
package de.spraener.nextgen.vpplugin.cartridge;

import de.spraener.nxtgen.Cartridge;
import de.spraener.nxtgen.CodeGeneratorMapping;
import de.spraener.nxtgen.Transformation;
import de.spraener.nxtgen.model.Model;
import de.spraener.nxtgen.model.ModelElement;
import de.spraener.nxtgen.oom.StereotypeHelper;

import java.util.List;
import java.util.ArrayList;

public class VPPluginCartridgeBase implements Cartridge {

    @Override
    public String getName() {
        return "VPPluginCartridgeBase";
    }
    
    @Override
    public List<Transformation> getTransformations() {
        List<Transformation> result = new ArrayList<>();
        result.add( new de.spraener.nextgen.vpplugin.cartridge.GeneratorGapTransformation() );

        return result;
    }

    @Override
    public List<CodeGeneratorMapping> mapGenerators(Model m) {
        List<CodeGeneratorMapping> result = new ArrayList<>();
        for( ModelElement me : m.getModelElements() ) {
            if( StereotypeHelper.hasStereotye(me, "ExporterBase") ) {
                    result.add(CodeGeneratorMapping.create(me, new de.spraener.nextgen.vpplugin.cartridge.ExporterBaseGenerator()));
            }
            if( StereotypeHelper.hasStereotye(me, "Exporter") ) {
                    result.add(CodeGeneratorMapping.create(me, new de.spraener.nextgen.vpplugin.cartridge.ExporterGenerator()));
            }

        }
        return result;
    }
}

