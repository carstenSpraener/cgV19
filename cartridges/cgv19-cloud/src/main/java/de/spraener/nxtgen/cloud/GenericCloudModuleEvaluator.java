package de.spraener.nxtgen.cloud;

import de.spraener.nxtgen.CodeGeneratorMapping;
import de.spraener.nxtgen.MustacheGenerator;
import de.spraener.nxtgen.NextGen;
import de.spraener.nxtgen.incubator.Blueprint;
import de.spraener.nxtgen.model.ModelElement;
import de.spraener.nxtgen.oom.model.MPackage;

import java.util.HashMap;
import java.util.Map;

/**
 * <p>
 * This GenericCloudModuleEvaluator generates a service-definition from a CloudModule. The root
 * of the blueprint is taken directly from the CloudModules dockerImage - tagged value. It this
 * dockerImage is something like &lt;imagename&gt:version it will take the content of the blueprint
 * under blueprint/imagename and generates the output from that blueprint
 * </p>
 * <p>
 * It defines the following variables, that can be referenced in the blueprint:
 * <ul>
 *     <li/>dockerImage
 *     <li/>moduleName
 *     <li/>modulePort
 *     <li/>password
 *     <li/>globalHostURL
 * </ul>
 * </p>
 * <p>
 *     As an example you can look at src/main/resources/blueprint/cloudmodules/mariadb
 * </p>
 */
public class GenericCloudModuleEvaluator {
    private MPackage pkg;

    public GenericCloudModuleEvaluator(MPackage pkg) {
        this.pkg = pkg;
    }

    public String evaluate() {
        String dockerImage = pkg.getTaggedValue("CloudModule", "dockerImage");
        String blueprintSelector = toBlueprintSelector(dockerImage);

        Blueprint.copyTo(NextGen.getWorkingDir(), "/blueprint/cloudmodules/" + blueprintSelector, buildScope(pkg));

        return CodeGeneratorMapping.create(pkg,
                new MustacheGenerator(
                        "/blueprint/cloudmodules/" + blueprintSelector + "/docker-compose-serviceblock.mustache",
                        "docker-compose-serviceblock",
                        this::dockerComposeServiceBlock
                )
        ).getCodeGen().resolve(pkg, "").toCode();

    }

    private String toBlueprintSelector(String dockerImage) {
        String selector =  dockerImage.substring(0, dockerImage.indexOf(':'));
        int idx = selector.lastIndexOf('/');
        if( idx != -1 ) {
            selector = selector.substring(idx+1);
        }
        return selector;
    }

    private void dockerComposeServiceBlock(ModelElement me, Map<String, Object> scope) {
        scope.putAll(buildScope((MPackage) me));
    }

    private static Map<String, String> buildScope(MPackage me) {
        Map<String, String> scope = new HashMap<>();
        MPackage pkg = me;
        String dockerImage = pkg.getTaggedValue("CloudModule", "dockerImage");
        String moduleName = pkg.getName().toLowerCase();
        if (moduleName == null) {
            dockerImage = moduleName;
        }
        String applPort = "80";
        String modulePort = pkg.getTaggedValue("CloudModule", "port");
        if (modulePort == null) {
            modulePort = applPort;
        }
        scope.put("dockerImage", dockerImage);
        scope.put("moduleName", moduleName);
        scope.put("modulePort", modulePort);
        scope.put("applPort", applPort);
        scope.put("password", moduleName + "pwd");
        scope.put("globalHostURL", "http://localhost:"+applPort);
        return scope;
    }

}
