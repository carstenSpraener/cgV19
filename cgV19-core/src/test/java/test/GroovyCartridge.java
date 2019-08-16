package test;

import de.spraener.nxtgen.Cartridge;
import de.spraener.nxtgen.CodeGeneratorMapping;
import de.spraener.nxtgen.Transformation;
import de.spraener.nxtgen.model.Model;
import de.spraener.nxtgen.model.ModelElement;
import transformations.EntityToTableTransformation;

import java.util.ArrayList;
import java.util.List;

public class GroovyCartridge implements Cartridge {

    @Override
    public String getName() {
        return "GroovyCartridge";
    }

    @Override
    public List<Transformation> getTransformations() {
        List<Transformation> result = new ArrayList<>();
        result.add(new EntityToTableTransformation());
        return result;
    }

    @Override
    public List<CodeGeneratorMapping> mapGenerators(Model m) {
        for(ModelElement me : m.getModelElements() ) {

        }
        return null;
    }

}
