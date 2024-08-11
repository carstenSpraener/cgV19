# Generating with mustache

[Mustache](https://github.com/spullara/mustache.java) is 
a template engine for processing files with insertion
points marked by double mustache (like {{SOME_VALUE}} )

This is perfect for generator-logic free generation of files like
build scripts or configurations.

This example generates a build.gradle for a spring boot
application from a class with stereotype SpringBootApp.

## Using mustache as template
The template itself should be somewhere in the resource
directory of the cartridge. In this example it 
is located at 
```src/main/resources/mustache/build.gradle.mustache```

```groovy
// THIS FILE IS GENERATED AS LONG AS THIS LINE EXISTS
buildscript {
    repositories {
        gradlePluginPortal()
    }
    dependencies {
    }
}

plugins {
    id 'java'
    id 'org.springframework.boot' version '{{springBootVersion}}'
    id 'io.spring.dependency-management' version '{{springDependencyManagementVersion}}'
}

group = '{{app.group}}'
version = '{{app.version}}'

java {
    sourceCompatibility = '{{javaSourceVersion}}'
}

repositories {
    mavenCentral()
}

dependencies {
    developmentOnly 'org.springframework.boot:spring-boot-devtools'

    implementation 'org.springframework.boot:spring-boot-starter-web'
    implementation 'org.springframework.boot:spring-boot-starter-data-jpa'
    implementation "org.springframework.boot:spring-boot-starter-validation"
    {{#implementationDependencies}}
    implementation '{{coordinates}}'
    {{/implementationDependencies}}
    implementation 'com.google.code.gson:gson:2.8.5'
    implementation 'com.h2database:h2:2.1.210'

    testImplementation 'org.springframework.boot:spring-boot-starter-test'
}

sourceSets {
    main {
        java {
            srcDirs 'src/main/java-gen'
        }
    }
}

springBoot {
    mainClass = "{{applicationMainClass}}"
}

tasks.named('test') {
    useJUnitPlatform()
}
```

The template contains a build.gradle that has some
markers for values and iterations. (like the 
{{#implementationDependencies}}). 

That's all very straight forward and requires very low
logic to implement.

## Using it in a generator

To make use of the template and fill the markers with
values from the model you have to define a 
```@CGV19MustacheGenerator``` in your cartridge.

Here is the code of the generator in this example:

```java
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
```

The method of the generator takes a model element and 
a Map where it should fill in the values for the markers.

That's it. The generator will create a build.gradle
for each MClass with stereotype "SpringBootApp".
