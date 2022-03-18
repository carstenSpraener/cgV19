# Tutorial to cgV19
_Last Update on: March the 17th 2022._

This is the core of the NextGen V19

## A brief history
In 2002 i started to work on a project for an insurance company in germany. The goal was to build 
an application for insurance estimations. The problem was it has to run in multiple channels
on the web or as a pure swing application. And in 2002 this was a real task.

We decided to work with a self programmed code generator called __CodeGen__. This generator
has gone several realeases and is still in work for the project.

In 2019 i started to recreate the whole generator from scratch. This is the
begining of __cgV19__

In __cgV19__ i implemented several features i missed in CodeGen. It has a new
concept of so called __cartridges__. Yes think of it as the old hardware
modules to plug into your game console. It has a self generated
meta model and the loading of a model can be plugged in from 
classpath.

Dependencies are very low. The core module only depends on Groovy.
Groovy is a language on top of java that is predestined for generation
and defining DSLs. 

While the old CodeGen had a very special model type (xml), __cgV19__ 
has a java meta model, that can be extended and created from nearly
every type of data.

__cgV19__ comes with a plugin for gradle. You can add it to your build.gradle and
the generator will run before the compile task starts. 

## What are all these modules?

### cgV19-core

This is the very hard of cgV19. If you want to use cgV19, this is the module you 
really allways have to use. All other modules dependending on this.

### cgV19-oom

This modules adds some basic object oriented mode features to cgV19. It impelements
a model that containes packages, classes, attribute and relations between the
classes. If you want to generate some object oriented language like, let's say java,
you can use this module to read your model into the generator.

### cgV19-pojo

This is an example of a very basic __cartridge__. It can take a OOM-Model and will
generate PoJos on classes marked as PoJos in the model.

### cgV19-gradle

This module implements a gradle plugin to enable gradle projects to use cgV19.

### cgV19-helloWorld

A very very basic project to demonstrate how you can use cgV19 in your projects.
The truth of how to use cgV19 is here!

### cgV19-MDPlugin

A plugin for MagicDraw to provide the model in MagicDraw to 
cgV19 via port 7000. Only useable if you have MagicDraw installed.

### cgV19-RESTCartridge

A (under construction) cartridge that generates a full runnable Spring Boot
application from the a model. It also provides a PHP-Symfony Backend to
demonstrate multiple language generation.

### restDemo

A demonstration project of how to use the cgV19-RESTCartridge.

## Want to start?

If you want to have a brief "how does it feel" follow the steps in this chapter. but
be warned: Model driven development is powerfull and fun. But you should know that 
writing templates and transformations for a real productive application is as
complex as writing a compiler. And also as powerfull. The real use of MDD comes
with powerfull transformations and generators that implementing a high level of 
abstraction. For example with a simple class marked as __Ressource__ you can
generate a whole Spring Boot 2 Resource (Entity, Repository, Controller) with
Angular frontend (TypeScript: Model, Service, Form). Where everything fits,
changes will synchronize frontend and backend and manual driven code is not 
affected. But from scratch to that is a long way to go.

__Stil want to start?__ OK! You are the right kind of developer. 

This example requires gradle in version 5.1 or higher.

### Set up the base environment
If you want to compile cgV19 on your own and using the most recent version, you can 
pull it directly from here. There for: 

Make a directory where you want to start, go into that directory an type:
```
git clone https://github.com/carstenSpraener/cgV19.git
```

This will create a copy of the project in your workspace.

Next step is to build all these modules. For that change into the oom, pojo and
gradle Module and call `gradle jar`.

```
cd cgV19
gradle publish
```

Now everything you need is set up. The required artifacts are now
in a local maven repository directors _rep_ under the cgV19 projec
directory.

__Attention__
This compiles all the modules in cgV19 except the cgV19-helloWorld and the restDemo project. This projects
are not included in the settings.gradle of the root project. This is because they have a chicken egg problem. 
They need the cgV19-gradle plugin but that is what need to be published first. So after you publihed all other
cgV19 modules you can now uncomment the two projects and do a second turn:

```
gradle jar
```

### Building a helloWorld-Application

_This chapter describes how to create a simple PoJo with cgC19 . The result is
in the cgV19-helloWorld module. But of course you want to build it on your own._

#### Setup the basic gradle build

Make a new directory, lets say "helloWorldNxtGen" and copy the following build script into a new build.gradle
 ```groovy
// Make the cgV19 Gradle plugin available to the build.
buildscript {
    repositories {
        maven {
            url "file:/<INSERT_PATH_TO_YOUR_CGV19-DIR_HERE!>/repo"
        }
        mavenCentral()
    }
    dependencies {
        classpath 'de.spraener.nxtgen:cgV19-core:21.0.0-SNAPSHOT'
        classpath 'de.spraener.nxtgen:cgV19-gradle:21.0.0-SNAPSHOT'
    }
}

plugins {
    // Apply the java-library plugin for API and implementation separation.
    id 'java'
}
// apply the plugin to the build script
apply plugin: 'de.spraener.nxtgen.cgV19'

repositories {
    // Use JCenter for resolving dependencies.
    mavenCentral()
    maven {
        url 'file:/<INSERT_PATH_TO_YOUR_CGV19-DIR_HERE!>/repo'
    }
}

dependencies {
}

```
__Attention__ Please replace the string ```<INSERT_PATH_TO_YOUR_CGV19-DIR_HERE!>``` with the real directory
where you build your cgV19 instance (occurs two times). There should be the _repo_ directory.

Also create a new file settings.gradle and insert the following text:

```groovy
rootProject.name = 'helloWorldNxtGen'
```

Now you have initialized a new Application-Project ready to use cgV19.

#### Configure cgV19 for your project

Next you need to tell cgV19 where it can find the model (oom-File) to generate from. MagicDraw-Users can use a
URL like http://localhost:7000/<root-package-name> to diretcly build from a MagicDraw-Model while modeling.
But for now we will use an ordinary file. So insert the following line to the ```build.gradle```:

```groovy
cgV19 {
    model = './src/main/helloWorld.oom'
}
```

This tells the cgV19-Plugin that your model is described in the given file.

Now you have to tell what cartridges you want to use. This is done via the dependencies block of the 
build.gradle file. The cgV19-Plugin defines a new dependency-group __cartridge__. Add all required 
cartridges, the cgV19-oom module and the cgV19-core module to this dependency group like follows:

```groovy
dependencies {
  ...
    cartridge 'de.spraener.nxtgen:cgV19-core:21.0.0-SNAPSHOT'
    cartridge 'de.spraener.nxtgen:cgV19-oom:21.0.0-SNAPSHOT'
    cartridge 'de.spraener.nxtgen:cgV19-pojo:21.0.0-SNAPSHOT'
}
```

The first two cartridge dependencies are always needed. (Core for shure and you will mostly use a oom-Model)

The generator will generate the java-code inside src/main/java-gen. To tell gradle
that it has another src directory add the following:

```groovy
sourceSets {
    src{
        main {
            java {
                srcDir('src/main/java')
                srcDir('src/main/java-gen')
            }
        }
    }
}
```

That's it. Your project is ready for model driven development with __cgV19__.

### Defining a model

The generator of course needs some sort of model to tell him what to generate. As 
we using the pojo-cartridge, which comes with an OOM-ModelLoader we have to
describe the model in an OOM-File. OOM stands for ObjectOrientedModel and is 
a groovy implemented DSL to describe thinks like Classes, Attributes, Extends, Operations and so on.

So, create a file helloWorld.oom in src/main and open it in your editor.
Copy this code into your editor and safe the file.
```groovy
import de.spraener.nxtgen.groovy.ModelDSL

ModelDSL.make {
    mPackage {
        name 'de.spraener.nxtgen.hello'
        mPackage {
            name = 'model'
            mClass {
                name 'Person'
                stereotype 'PoJo'
                mAttribute {
                    name 'firstName'
                    type 'String'
                }
                mAttribute {
                    name 'name'
                    type 'String'
                }
            }
        }
    }
}
```

This is the model described in a domain specific laguange (DSL) 
as it comes with the oom-Module.

As you can see the model defines a package `de.spraener.nxtgen.hello`
that contains a Class  
`Person` with two attributes `name` and `firstName`. All this
is straight forward.

The one thing, that makes it special is the _stereotype_ definition
on the Class. This marks the class as a PoJo.

Stereotypes telling __cgV19__ what kind of generator is to use. The
PoJo-Cartridge used in the build script maps this stereotyped classes
to the PoJo-Generator which will create a PoJo Java-Class.

### Running the generator
To transform you model in a java class,  start `gradle generate`. This
will create a new source directory `src/main/java-gen` and inside
that directory a package `de.spraener.nxtgen.hello` wich contains...
I think you can emagine.

Now you can use the generated Person-Class inside your code.

### Protecting from regeneration
Sometimes your generated code is just a template, that the developer
namely you, have to fill in with real code. This code must be protected
from overwriting some how. 
__cgV19__ has a very simple __ProtectionStrategy__. This strategie
looks in the file to be overwritten for a line containing the 
string

 __THIS FILE IS GENERATED AS LONG AS THIS LINE EXISTS__
  
If it finds this text in the first 5 lines of the file or if
the file is empty, it will generate the code.

So: If you edited a generated file just remove this line and __cgV9__ 
will never touch it again.

# How does it work
cgV19 uses the java service loader mechanism to find it's components. The first
component is an implementation of the ModelLoader interface. 

One implementation is located in the cgV19-oom jar and in can handle oom-Files
with the syntax as seen in the helloWorld.oom. 

## Cartridges

[You can find a detailed descritption of a cartidge here](./Cartridges.md)

Once a model is loaded the next step is to find one or more implementations of
the Cartridge interface. Cartridges building a set of Transformations and 
Generators that belongs together. For example a JPA-Cartridge can contain all
classes to generate a JPA backend. Another cartridge can generate logic, controllers
and JSON-Wrapper classes. Maybe a third cartrdige generates the TypeScript 
frontend to call this controllers. And whatever more...

The cgV19-pojo jar has a Cartridge implementation. A 
cartridge implements the following methods:

### getName

Just a simple name for logging output. No other requeirements to this method.

### List&lt;Transformation&gt; getTransformations()

Delivers a list of Transformations to run on the loaded model. A Transformation is
the key concept to high level abstraction. It enhances the model with more model
imformation. For example it adds interfaces to the model, adds subclasses or 
refines the definitions. 

With a set of Transformations you keep you model clean from boiler plate model 
information.

### List&lt;CodeGeneratorMapping&gt; mapGenerators(Model m)

This method walks to the whole model an tells cgV19 how to handle each model 
element. For example the PoJo cartridge looks for Classes in the oom model 
and maps them to its PoJoGenerator.

The mapping is done __after__ the transformations. So your mapping can
map classes not in the oom file but created in some transformation.

### More cartridges

If you have more cartridges on your classpath each cartridge is called
with an new loaded model. So side effects can not occure. Of course they
can share Transformations. But each cartridge has to apply them on it's
own.





