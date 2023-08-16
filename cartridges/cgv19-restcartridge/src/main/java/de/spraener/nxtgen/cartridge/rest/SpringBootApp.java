package de.spraener.nxtgen.cartridge.rest;

import de.spraener.nxtgen.annotations.CGV19Blueprint;
import de.spraener.nxtgen.annotations.CGV19Component;
import de.spraener.nxtgen.annotations.CGV19Generator;
import de.spraener.nxtgen.annotations.CGV19MustacheGenerator;
import de.spraener.nxtgen.model.ModelElement;
import de.spraener.nxtgen.model.Stereotype;
import de.spraener.nxtgen.model.TaggedValue;
import de.spraener.nxtgen.oom.StereotypeHelper;
import de.spraener.nxtgen.oom.model.MClass;
import de.spraener.nxtgen.oom.model.MPackage;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@CGV19Component
public class SpringBootApp {

    @CGV19MustacheGenerator(
            value="build.gradle",
            requiredStereotype="SpringBootApp",
            operatesOn= MClass.class,
            templateResource="/mustache/springBootApp/build.gradle.mustache"
    )
    public static void fillBuildScriptMap(ModelElement modelElement, Map<String, Object> mustacheScope) {
        MClass app = (MClass) modelElement;
        mustacheScope.put("springBootVersion", "3.1.0");
        mustacheScope.put("springDependencyManagementVersion", "1.1.0");
        mustacheScope.put("app.group", ((MPackage)app.getPackage().getParent()).getFQName());
        mustacheScope.put("app.version", "0.0.1-SNAPSHOT");
        mustacheScope.put("javaSourceVersion", "17");
        mustacheScope.put("applicationMainClass", app.getFQName());
        List<Map<String, String>>  implementationDependencies = new ArrayList<>();
        mustacheScope.put("implementationDependencies", implementationDependencies);
        implementationDependencies.add( Map.of("coordinates", "org.springframework.boot:spring-boot-starter-oauth2-resource-server"));
        implementationDependencies.add( Map.of("coordinates", "org.springframework.boot:spring-boot-starter-oauth2-client"));
    }

    public static void fillDockerfileMap(ModelElement modelElement, Map<String, Object> mustacheScope) {
        MClass app = (MClass) modelElement;
        mustacheScope.put("dockerGradleBuildImage", "gradle:jdk17-focal");
        mustacheScope.put("projectName", app.getPackage().getName());
        mustacheScope.put("dockerProductionRunImage", "eclipse-temurin:17.0.6_10-jdk-focal");
        mustacheScope.put("timeZone", "Europe/Berlin");
    }

    @CGV19Blueprint(
            value="/blueprints/springCloud",
            requiredStereotype="SpringCloudApp",
            operatesOn= MClass.class
    )
    public Map<String, String> springCloudBlueprintScope(ModelElement me) {
        Map<String, String> scope = new HashMap<>();
        Stereotype sType = StereotypeHelper.getStereotype(me, RESTStereotypes.SPRINGBOOTAPP.getName());
        for( TaggedValue tv : sType.getTaggedValues() ) {
            scope.put(tv.getName(), tv.getValue() );
        }
        return scope;
    }

    @CGV19MustacheGenerator(
            value="docker-compose-service-block",
            requiredStereotype="CloudModule",
            operatesOn= MPackage.class,
            templateResource="/mustache/springBootApp/docker-compose-serviceblock.mustache"
    )
    public static void dockerComposeServiceBlock(ModelElement me,  Map<String, Object> scope) {
        MPackage module = (MPackage) me;
        String containerName = module.getTaggedValue("CloudModule", "dockerImage");
        String moduleName = module.getName().toLowerCase();
        if( containerName==null ) {
            containerName = moduleName;
        }
        String applPort = "8080";
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
