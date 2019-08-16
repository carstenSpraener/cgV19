package de.csp.nxtgen.pojo;

import de.csp.nxtgen.Cartridge;
import de.csp.nxtgen.CodeGeneratorMapping;
import de.csp.nxtgen.Transformation;
import de.csp.nxtgen.model.Model;
import de.csp.nxtgen.model.ModelElement;
import de.csp.nxtgen.oom.StereotypeHelper;
import de.csp.nxtgen.oom.model.MClass;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

public class PoJoCartridge implements Cartridge {
    public static final String ST_POJO = "PoJo";

    @Override
    public String getName() {
        return "PoJo-Cartridge";
    }

    @Override
    public List<Transformation> getTransformations() {
        return null;
    }

    @Override
    public List<CodeGeneratorMapping> mapGenerators(Model m) {
        Logger.getAnonymousLogger().info("Initiating cartridge "+getName());
        List<CodeGeneratorMapping> mappings = new ArrayList<>();
        for(ModelElement me : m.getModelElements() ) {
            if (me instanceof MClass) {
                Logger.getAnonymousLogger().info("Mapping class "+me.getName());
                if (StereotypeHelper.hasStereotye(me, ST_POJO)) {
                    mappings.add(CodeGeneratorMapping.create(me, new PoJoGenerator()));
                }
            }
        }
        return mappings;
    }
}
