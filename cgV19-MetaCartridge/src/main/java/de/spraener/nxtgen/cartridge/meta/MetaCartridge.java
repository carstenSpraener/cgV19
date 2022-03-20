package de.spraener.nxtgen.cartridge.meta;

import de.spraener.nxtgen.*;
import de.spraener.nxtgen.model.Model;
import de.spraener.nxtgen.model.ModelElement;
import de.spraener.nxtgen.oom.model.MClass;

import java.util.ArrayList;
import java.util.List;

public class MetaCartridge implements Cartridge {
    public static final String STEREOTYPE_NAME="Stereotype";
    public static final String STEREOTYPE_MODEL_ROOT="ModelRoot";
    public static final String STEREOTYPE_ENUM="StereotypeEnum";

    @Override
    public String getName() {
        return "Meta-Catridge for OOM";
    }

    @Override
    public List<Transformation> getTransformations() {
        List<Transformation> result = new ArrayList<>();
        result.add(new AddStereotypeToMClassTransformantion());
        result.add(new RemoveModelRootPackage());
        return result;
    }

    @Override
    public List<CodeGeneratorMapping> mapGenerators(Model m) {
        List<CodeGeneratorMapping> result = new ArrayList<>();
        for( ModelElement me : m.getModelElements() ) {
            if( me instanceof MClass && ((MClass) me).hasStereotype(STEREOTYPE_NAME) ) {
                result.add(CodeGeneratorMapping.create(me, new StereotypeDocGenerator()));
            }
            if( me instanceof MClass && ((MClass) me).hasStereotype(STEREOTYPE_ENUM) ) {
                result.add(CodeGeneratorMapping.create(me, new StereotypeEnumGenerator()));
            }
        }
        return result;
    }
}
