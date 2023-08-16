package de.spraener.nxtgen.javalin;


import de.spraener.nxtgen.CodeGeneratorMapping;
import de.spraener.nxtgen.MustacheGenerator;
import de.spraener.nxtgen.Transformation;
import de.spraener.nxtgen.model.Model;
import de.spraener.nxtgen.model.ModelElement;
import de.spraener.nxtgen.model.Stereotype;
import de.spraener.nxtgen.oom.StereotypeHelper;
import de.spraener.nxtgen.oom.cartridge.GeneratorGapTransformation;
import de.spraener.nxtgen.oom.model.MClass;
import de.spraener.nxtgen.oom.model.MPackage;

import java.util.List;
import java.util.Map;

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

    @Override
    public String evaluate(Model m, ModelElement me, Stereotype sType, String aspect) {
        if( me instanceof MPackage && sType.getName().equals("CloudModule") && "docker-compose".equals(aspect)) {
            return CodeGeneratorMapping.create(me, new MustacheGenerator(
                    "/mustache/javalinApp/docker-compose-serviceblock.mustache",
                    "docker-compose-service-block",
                    this::dockerComposeServiceBlock
            )).getCodeGen().resolve(me, "").toCode();
        }
        return super.evaluate(m, me, sType, aspect);
    }

    private void dockerComposeServiceBlock(ModelElement me, Map<String, Object> scope) {
        MPackage module = (MPackage) me;
        String containerName = module.getTaggedValue("CloudModule", "dockerImage");
        String moduleName = module.getName().toLowerCase();
        if( containerName==null ) {
            containerName = moduleName;
        }
        String applPort = "7070";
        String modulePort = module.getTaggedValue("CloudModule", "port");
        if( modulePort == null ) {
            modulePort = applPort;
        }
        scope.put("containerName",containerName);
        scope.put("moduleName",moduleName);
        scope.put("modulePort",modulePort);
        scope.put("applPort",applPort);
    }
}
