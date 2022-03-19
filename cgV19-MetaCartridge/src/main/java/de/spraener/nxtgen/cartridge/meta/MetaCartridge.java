package de.spraener.nxtgen.cartridge.meta;

import de.spraener.nxtgen.Cartridge;
import de.spraener.nxtgen.CodeGeneratorMapping;
import de.spraener.nxtgen.NextGen;
import de.spraener.nxtgen.Transformation;
import de.spraener.nxtgen.model.Model;
import de.spraener.nxtgen.model.ModelElement;
import de.spraener.nxtgen.oom.model.MClass;

import java.util.ArrayList;
import java.util.List;

public class MetaCartridge implements Cartridge {
    public static final String STEREOTYPE_NAME="Stereotype";

    @Override
    public String getName() {
        return "Meta-Catridge for OOM";
    }

    @Override
    public List<Transformation> getTransformations() {
        NextGen.LOGGER.info("Cartridge "+getName()+" is running and listing its transformations.");
        List<Transformation> result = new ArrayList<>();
        result.add(new AddStereotypeToMClassTransformantion());
        return result;
    }

    @Override
    public List<CodeGeneratorMapping> mapGenerators(Model m) {
        List<CodeGeneratorMapping> result = new ArrayList<>();
        for( ModelElement me : m.getModelElements() ) {
            if( me instanceof MClass && ((MClass) me).hasStereotype(STEREOTYPE_NAME) ) {
                result.add(CodeGeneratorMapping.create(me, new StereotypeDocGenerator()));
            }
        }
        NextGen.LOGGER.info("Adding "+result.size()+" CodeBlockGenerators to the generator.");
        return result;
    }
}
