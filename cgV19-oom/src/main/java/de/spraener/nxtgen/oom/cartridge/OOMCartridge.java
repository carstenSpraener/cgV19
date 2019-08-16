package de.spraener.nxtgen.oom.cartridge;

import de.spraener.nxtgen.Cartridge;
import de.spraener.nxtgen.CodeGeneratorMapping;
import de.spraener.nxtgen.Transformation;
import de.spraener.nxtgen.model.Model;
import de.spraener.nxtgen.model.ModelElement;
import de.spraener.nxtgen.model.Stereotype;

import java.util.ArrayList;
import java.util.List;

public class OOMCartridge implements Cartridge {

    @Override
    public String getName() {
        return "ObjectOrientedMetamodel-Cartridge";
    }

    @Override
    public List<Transformation> getTransformations() {
        return null;
    }

    @Override
    public List<CodeGeneratorMapping> mapGenerators(Model m) {
        List<CodeGeneratorMapping> result = new ArrayList<>();
        for (ModelElement me : m.getModelElements()) {
            if (isMetaClass(me)) {
                result.add(CodeGeneratorMapping.create(me, new OOMGenerator()));
            }
        }
        return result;
    }

    private boolean isMetaClass(ModelElement me) {
        for (Stereotype sType : me.getStereotypes()) {
            if (sType.getName().equals("OOMMetaClass")) {
                return true;
            }
        }
        return false;
    }
}
