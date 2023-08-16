package de.spraener.nxtgen.angular;

import de.spraener.nxtgen.CodeGeneratorMapping;
import de.spraener.nxtgen.MustacheGenerator;
import de.spraener.nxtgen.annotations.CGV19Blueprint;
import de.spraener.nxtgen.annotations.CGV19Cartridge;
import de.spraener.nxtgen.annotations.CGV19Component;
import de.spraener.nxtgen.annotations.CGV19MustacheGenerator;
import de.spraener.nxtgen.model.Model;
import de.spraener.nxtgen.model.ModelElement;
import de.spraener.nxtgen.model.Stereotype;
import de.spraener.nxtgen.oom.model.MPackage;

import java.util.HashMap;
import java.util.Map;

@CGV19Cartridge("Cgv19Angular")
@CGV19Component()
public class Cgv19Angular extends Cgv19AngularBase{
    public static final String NAME = "Cgv19Angular";

    public Cgv19Angular() {
        super();
    }

    @Override
    public String getName() {
        return NAME;
    }

    @CGV19Blueprint(
            value="/blueprints/angularFrame",
            operatesOn = MPackage.class,
            outputDir = ".",
            requiredStereotype = "Cgv19AngularApp"
    )
    public Map<String,Object> fillAngularSetup(MPackage pkg) {
        Map<String,Object> scope = new HashMap<>();
        scope.put("appName", pkg.getName().toLowerCase());
        return scope;
    }

    @CGV19MustacheGenerator(
            value="Dockerfile",
            operatesOn = MPackage.class,
            templateResource = "/mustache/Dockerfile.mustache",
            requiredStereotype = "Cgv19AngularApp"
    )
    public void fillDockerfileScope(ModelElement me, Map<String, Object> scope ) {
        String appName = me.getName().toLowerCase();
        scope.put("appName", appName);
        scope.put("port", "80");
    }

    @Override
    public String evaluate(Model m, ModelElement me, Stereotype sType, String aspect) {
        if( me instanceof MPackage && sType.getName().equals("CloudModule") && aspect.equals("docker-compose")) {
            return CodeGeneratorMapping.create(me, new MustacheGenerator(
                    "/mustache/docker-compose-angular-serviceblock.mustache",
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
        String applPort = "80";
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
