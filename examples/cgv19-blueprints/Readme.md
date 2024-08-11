# Blueprints
Blueprints are perfect for setting up new projects
with a given structure. A blueprint based generator
fills a map with key-value-pairs and starts with a 
directory containing the __blueprint__ of a projects 
structure.

The whole thing is very simular to mustaches and the
evaluation of single files is done via mustache. But
in extension to mustache generators a blueprint can
also contain mustache-markers in a directories name.

## Example: docker-compose application frame

The blueprint in this example is stored in the resource
directory under ```src/main/resources/blueprints```. The
subdirectory ```springCloudApp``` contains all files 
needed for a little docker-compose application with
frontend, backend and database. The structure is shown
here:

```log
└── springCloudApp
    ├── docker-compose.yml.mustache
    ├── {{appname}}-backend
    │   └── build.gradle
    └── {{appname}}-frontend
        ├── Dockerfile.mustache
        └── etc
            └── nginx
                ├── app.conf
                └── nginx.conf
```

The generator to create a docker application with
this blueprint is implemented in the class 
```BlueprintGenerator``` as follows:

```java
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
```

The generator method is annotated as a ```@CGV19Blueprint```
and glues the root of the blueprint to a map with key-value
pairs. The keys are referenced in the blueprint files
them self or in the directory names.

With the model from the tests the keys are set to
```bash
appname=demoapp
appClassName=demoapp.DemoApp
```
After starting the generator the resulting files are:

```log
├── demoapp-backend
│   └── build.gradle
├── demoapp-frontend
│   ├── Dockerfile
│   └── etc
│       └── nginx
│           ├── app.conf
│           └── nginx.conf
└── docker-compose.yml
```
