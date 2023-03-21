package de.spraener.nxtgen.pojo;

import de.spraener.nxtgen.Cartridge;
import de.spraener.nxtgen.CodeGeneratorMapping;
import de.spraener.nxtgen.Transformation;
import de.spraener.nxtgen.model.Model;
import de.spraener.nxtgen.model.ModelElement;
import de.spraener.nxtgen.oom.StereotypeHelper;
import de.spraener.nxtgen.oom.model.MClass;
import de.spraener.nxtgen.pojo.annotations.Payload;

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
        Logger.getAnonymousLogger().info("Initiating cartridge " + getName());
        List<CodeGeneratorMapping> mappings = new ArrayList<>();
        for (ModelElement me : m.getModelElements()) {
            if (StereotypeHelper.hasStereotye(me, ST_POJO)) {
                Logger.getAnonymousLogger().info("Mapping class " + me.getName());
                mappings.add(CodeGeneratorMapping.create(me, new PoJoGenerator()));
            }
            if (StereotypeHelper.hasStereotye(me, "Payload")) {
                Logger.getAnonymousLogger().info("Mapping PayLoad " + me.getName());
                mappings.add(CodeGeneratorMapping.create(me, new PayLoadGenerator()));
                mappings.add(CodeGeneratorMapping.create(me, new PayLoadBuilderGenerator()));
            }
        }
        return mappings;
    }

    @Override
    public List<String> getAnnotationTypes() {
        return List.of(
                Payload.class.getName()
        );
    }
}
