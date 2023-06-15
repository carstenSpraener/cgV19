package de.spraener.nxtgen.jp.cartridge;

import com.squareup.javapoet.TypeSpec;
import de.spraener.nxtgen.*;
import de.spraener.nxtgen.jp.JavaPoetGenerator;
import de.spraener.nxtgen.model.Model;
import de.spraener.nxtgen.model.ModelElement;
import de.spraener.nxtgen.oom.StereotypeHelper;
import de.spraener.nxtgen.oom.model.MClass;

import java.util.ArrayList;
import java.util.List;

public class JavaPoetTestCartridge implements Cartridge {
    @Override
    public String getName() {
        return "JavaPoetTestCartridge";
    }

    @Override
    public List<Transformation> getTransformations() {
        return new ArrayList<>();
    }

    @Override
    public List<CodeGeneratorMapping> mapGenerators(Model m) {
        List<CodeGeneratorMapping> mappingList = new ArrayList<>();
        for (ModelElement me : m.getModelElements()) {
            if (me instanceof MClass mc && StereotypeHelper.hasStereotype(mc, "PoJo")) {
                mappingList.add(
                        CodeGeneratorMapping.create(mc, new JavaPoetGenerator(
                                mClazz -> TypeSpec.classBuilder(mClazz.getName()),
                                PoJoGenerator::build)
                                .withCodeBlockBefore(new SimpleStringCodeBlock("//" + ProtectionStrategie.GENERATED_LINE))
                        )
                );
            }
            if (me instanceof MClass mc && StereotypeHelper.hasStereotype(mc, "Entity")) {
                mappingList.add(
                        CodeGeneratorMapping.create(mc, new JavaPoetGenerator(
                                mClazz -> TypeSpec.classBuilder(mClazz.getName()),
                                PoJoGenerator::build,
                                EntityGenerator::build)
                                .withCodeBlockBefore(new SimpleStringCodeBlock("//" + ProtectionStrategie.GENERATED_LINE))
                        )
                );
            }
        }
        return mappingList;
    }
}

