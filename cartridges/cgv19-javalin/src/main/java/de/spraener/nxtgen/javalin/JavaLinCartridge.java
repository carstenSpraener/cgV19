package de.spraener.nxtgen.javalin;


import de.spraener.nxtgen.CodeGeneratorMapping;
import de.spraener.nxtgen.MustacheGenerator;
import de.spraener.nxtgen.Transformation;
import de.spraener.nxtgen.model.Model;
import de.spraener.nxtgen.model.ModelElement;
import de.spraener.nxtgen.oom.StereotypeHelper;
import de.spraener.nxtgen.oom.cartridge.GeneratorGapTransformation;
import de.spraener.nxtgen.oom.model.MClass;

import java.util.List;

public class JavaLinCartridge extends JavaLinCartridgeBase {

    public JavaLinCartridge() {
        super();
    }

    @Override
    public String getName() {
        return "JavaLinCartridge";
    }

    @Override
    public List<Transformation> getTransformations() {
        List<Transformation> result = super.getTransformations();
        result.add(new GeneratorGapTransformation());
        return result;
    }

    @Override
    public List<CodeGeneratorMapping> mapGenerators(Model m) {
        List<CodeGeneratorMapping> result = super.mapGenerators(m);
        for( ModelElement me : m.getModelElements() ) {
            if (StereotypeHelper.hasStereotype(me,JavaLinStereotypes.JAVALINAPP.getName()) && me instanceof MClass mc) {
                result.add( CodeGeneratorMapping.create(mc,
                        new MustacheGenerator(
                                "mustache/javalinApp/build.gradle.mustache",
                                "build.gradle",
                                JavaLinAppComponent::fillBuildScriptMap
                        )
                ));
                result.add( CodeGeneratorMapping.create(mc,
                        new MustacheGenerator(
                                "mustache/javalinApp/Dockerfile.mustache",
                                "Dockerfile",
                                JavaLinAppComponent::fillDockerfileMap
                        )
                ));
            }
        }
        return result;
    }
}
