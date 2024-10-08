package de.spraener.nxtgen.mustache;

import de.spraener.nxtgen.annotations.CGV19Component;
import de.spraener.nxtgen.annotations.CGV19MustacheGenerator;
import de.spraener.nxtgen.model.ModelElement;
import de.spraener.nxtgen.oom.model.MClass;
import de.spraener.nxtgen.oom.model.MPackage;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@CGV19Component
public class MustacheGenerator {

    @CGV19MustacheGenerator(
            value="build.gradle",
            requiredStereotype="SpringBootApp",
            operatesOn= MClass.class,
            templateResource="/mustache/build.gradle.mustache"
    )
    public void fillBuildScriptMap(ModelElement modelElement, Map<String, Object> mustacheScope) {
        MClass app = (MClass) modelElement;
        mustacheScope.put("springBootVersion", "3.1.0");
        mustacheScope.put("springDependencyManagementVersion", "1.1.0");
        mustacheScope.put("app.group", ((MPackage)app.getPackage()).getFQName());
        mustacheScope.put("app.version", "0.0.1-SNAPSHOT");
        mustacheScope.put("javaSourceVersion", "17");
        mustacheScope.put("applicationMainClass", app.getFQName());
        List<Map<String, String>> implementationDependencies = new ArrayList<>();
        mustacheScope.put("implementationDependencies", implementationDependencies);
        implementationDependencies.add( Map.of("coordinates", "org.springframework.boot:spring-boot-starter-oauth2-resource-server"));
        implementationDependencies.add( Map.of("coordinates", "org.springframework.boot:spring-boot-starter-oauth2-client"));
    }

}
