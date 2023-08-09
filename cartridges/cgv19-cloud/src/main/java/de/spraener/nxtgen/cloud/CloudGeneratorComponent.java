package de.spraener.nxtgen.cloud;

import de.spraener.nxtgen.annotations.CGV19Component;
import de.spraener.nxtgen.annotations.CGV19MustacheGenerator;
import de.spraener.nxtgen.cloud.model.MComponent;
import de.spraener.nxtgen.model.ModelElement;
import de.spraener.nxtgen.oom.model.MClass;
import de.spraener.nxtgen.oom.model.MPackage;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@CGV19Component
public class CloudGeneratorComponent {

    @CGV19MustacheGenerator(
            value=".gitignore",
            requiredStereotype="Deployment",
            operatesOn= MPackage.class,
            templateResource= "/mustache/.gitignore.mustache"
    )
    public static void fillGitIgnoreMap(ModelElement modelElement, Map<String, Object> mustacheScope) {
    }


    @CGV19MustacheGenerator(
            value="buildDocker.sh",
            requiredStereotype="Deployment",
            operatesOn= MPackage.class,
            templateResource= "/mustache/buildDocker.sh.mustache"
    )
    public static void fillBuildDockerMap(ModelElement modelElement, Map<String, Object> mustacheScope) {
        String deploymentName = CloudCartridge.getDeploymentName(modelElement);
        String deploymentRegistry = CloudCartridge.getDeploymentRegistry(modelElement);

        String stCloudModule = CloudStereotypes.CLOUDMODULE.getName();
        List<Map<String, String>> dockerProjectList = new ArrayList<>();
        mustacheScope.put("dockerprojects", dockerProjectList);
        // fill: buildDockerImage {{prjName}} {{registry}} {{deploymentName}}-{{prjName}}:latest
        for(MPackage pkg : CloudCartridge.findCloudModules(modelElement.getModel()) ) {
            Map<String, String> dockerProject = new HashMap<>();
            dockerProject.put("prjName", pkg.getName().toLowerCase());
            dockerProject.put("registry", deploymentRegistry);
            if( deploymentRegistry==null ) {
                dockerProject.put("registry", pkg.getTaggedValue(stCloudModule, "dockerRegistry"));
            }
            dockerProject.put("deploymentName", deploymentName);
            dockerProjectList.add(dockerProject);
        }
    }
}
