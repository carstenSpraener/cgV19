package de.spraener.nxtgen.ice2;

import de.spraener.nxtgen.CodeGeneratorMapping;
import de.spraener.nxtgen.MustacheGenerator;
import de.spraener.nxtgen.annotations.CGV19Cartridge;
import de.spraener.nxtgen.ice.CloudStereoTypes;
import de.spraener.nxtgen.model.Model;
import de.spraener.nxtgen.model.ModelElement;
import de.spraener.nxtgen.model.Stereotype;
import de.spraener.nxtgen.oom.model.MClass;
import de.spraener.nxtgen.oom.model.MPackage;

import java.util.Map;

@CGV19Cartridge("ICECalledCartridge")
public class ICECalledCartridge extends ICECalledCartridgeBase{
    public static final String NAME = "ICECalledCartridge";

    public ICECalledCartridge() {
        super();
    }

    @Override
    public String getName() {
        return NAME;
    }

    @Override
    public String evaluate(Model m, ModelElement me, Stereotype sType, String aspect) {
        CodeGeneratorMapping mapping = this.createMapping(me, sType.getName(), aspect);
        if (mapping == null) {
            return "Unsupported evaluation request for ModelElement '" + me.getName() + " with aspect: '" + aspect + "'";
        }
        return mapping.getCodeGen().resolve(me, "").toCode();
    }

    protected CodeGeneratorMapping createMapping(ModelElement me, String stereotypeName, String aspect) {
        if (me instanceof MPackage &&
                CloudStereoTypes.DOCKERSERVICE.getName().equals(stereotypeName) &&
                aspect.equals("docker-compose")
        ) {
            return CodeGeneratorMapping.create(me,
                    new MustacheGenerator(
                            "/mustache/docker-service/docker-compose-serviceblock.mustache",
                            "docker-compose-service-block",
                            this::dockerComposeServiceBlock
                    )
            );
        }
        return super.createMapping(me, stereotypeName);
    }

    private void dockerComposeServiceBlock(ModelElement me, Map<String, Object> scope) {
        // Fill the scope with data from the ModelElement me. In this context the ModelElement 'me' is
        // the <<DockerService>>
        scope.put("serviceName", me.getName());
        scope.put("modulePort", "8080");
        scope.put("applPort", "8090");
        scope.put("moduleName", me.getName().toLowerCase());
        scope.put("containerName", me.getName().toLowerCase());
    }

}
