package de.spraener.nxtgen.blueprints;

import de.spraener.nxtgen.annotations.CGV19Blueprint;
import de.spraener.nxtgen.annotations.CGV19Component;
import de.spraener.nxtgen.model.ModelElement;
import de.spraener.nxtgen.model.Stereotype;
import de.spraener.nxtgen.model.TaggedValue;
import de.spraener.nxtgen.oom.StereotypeHelper;
import de.spraener.nxtgen.oom.model.MClass;

import java.util.HashMap;
import java.util.Map;

@CGV19Component
public class BlueprintGenerator {
    @CGV19Blueprint(
            value="/blueprints/springCloudApp" ,
            requiredStereotype="SpringCloudApp",
            operatesOn= MClass.class
    )
    public Map<String, String> springCloudBlueprintScope(ModelElement me) {
        Map<String, String> scope = new HashMap<>();
        scope.put("appname", me.getName().toLowerCase());
        scope.put("appClassName", ((MClass)me).getFQName());
        return scope;
    }
}
