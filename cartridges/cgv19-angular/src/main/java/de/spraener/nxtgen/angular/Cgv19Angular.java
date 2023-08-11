package de.spraener.nxtgen.angular;

import de.spraener.nxtgen.CodeGenerator;
import de.spraener.nxtgen.annotations.CGV19Blueprint;
import de.spraener.nxtgen.annotations.CGV19Cartridge;
import de.spraener.nxtgen.annotations.CGV19Component;
import de.spraener.nxtgen.annotations.CGV19MustacheGenerator;
import de.spraener.nxtgen.model.ModelElement;
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
        return new HashMap<>();
    }

    @CGV19MustacheGenerator(
            value="Dockerfile",
            operatesOn = MPackage.class,
            templateResource = "/mustache/Dockerfile.mustache",
            requiredStereotype = "Cgv19AngularApp"
    )
    public void fillDockerfileScope(ModelElement me, Map<String, Object> scope ) {
        String prjName = me.getName();
        scope.put("projectName", prjName);
        scope.put("port", "80");
    }
}
