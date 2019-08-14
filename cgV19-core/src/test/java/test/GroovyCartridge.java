package test;

import de.csp.nxtgen.Cartridge;
import de.csp.nxtgen.CodeGeneratorMapping;
import de.csp.nxtgen.Transformation;
import de.csp.nxtgen.model.Model;
import de.csp.nxtgen.model.ModelElement;
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
