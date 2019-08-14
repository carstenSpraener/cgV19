# cgV19 Core
This is the core of the CodeGen V19 (nextGen)

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

With __cgV19__ comes a new plugin for MagicDraw. This plugin provides
the UML-Model from inside MagicDraw in a DSL form supported by the cgV19OOM
module. You can install the plugin inside of MagicDraw. It provides a REST-Service
on Port 7000 and serves the model. The ModelLoader from the cgV19OOM-Module
can read this model while MagicDraw is running.

__cgV19__ comes with a plugin for gradle. You can add it to your build.gradle and
the generator will run before the compile task starts. 

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
Make a new directory, lets say "helloWorldNxtGen" and run
 `gradle init --type application` to initialize a new Application-Project.

When finished open your build.gradle in your editor and add the following
plugin to the build-script:

```
plugin {
  ...
  add 'cgV19', version:'19.0'
}
```

This will add __cgV19__ to your build process. Also add a new dependency to your
project:

```
dependencies {
  ...
  cartridge group:'', name: '', version: ''
}
```
Now you added a example cartridge for generating simple java PoJos to __cgV19__.
The last step is to tell the generator where the model is located. This can be 
done by adding:
``` 
cgv19 {
   model='./helloWorld.oom'
}
```
to the build script.

That's it. Your project is ready for model driven development with __cgV19__.

### Defining a model

The generator of course needs some sort of model to tell him what to generate. As 
we using the pojo-cartridge, which comes with an OOM-ModelLoader we have to
describe the model in an OOM-File. OOM stands for ObjectOrientedModel and is 
a groovy implemented DSL.

So, create a file helloWorld.oom and open it in your editor:
```
import de.csp.ng.nxtgen.model.Model
import de.csp.nxtgen.groovy.ModelDSL

Model model = ModelDSL.make {
    mPackage {
        name 'example.nxtgen'
        mPackage {
            name = 'hello'
            mClass {
                name 'PersonBase'
                stereotype 'PoJo'
                mAttribute {
                    name 'name'
                    type 'String'
                }
                mAttribute {
                    name 'firstName'
                    type 'String'
                }
            }
        }
    }
}
```
As you can see the model defines a package `example.nxtgen` inside
the package is another package `hello` that contains a Class 
`Person` with two attributes `name` and `firstName`. All this
is straight forward.

The one thing, that makes it special is the _stereotype_ definition
on the Class. This marks the class as a PoJo.

Stereotypes telling __cgV19__ what kind a generator is to use. The
PoJo-Cartridge used in the build script maps this stereotyped classes
to the PoJo-Generator which will create a PoJo Java-Class.

### Running the generator
To transform you model in a java class,  start `gradle generate`. This
will create a new source directory `src/main/java-gen` and inside
that directory a package `example.nxtgen.hello` wich contains...
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

## Model, MetaModel, MetaMetaModel....
* Model describes your domain for example in UML
* UML is a meta model that has Classes, Packages, Relations, Attributes ...
* The model to describe UML is the meta meta model
* MetaMetalModel of cgV19 is
    * _ModelElement_ which contains other ModelElements
        * a Parent model Element
        * a name
        * e Key/Value store for properties
        * a list of assigned _Stereotypes_
    * Stereotypes have
        * a name unique in the list of stereotypes assigned to a 
          ModelElement
        * a set of key/value pairs as _TaggedValues_
                 
## CodeGen V19

### Base Engine

* Searches for Modelloader
* Locates Catridges
* For each Cartridge: 
    * LoadModel
    * run Transformations from Cartridge
    * map model Elements to generators
    * start Generators for each mapped model element

#### Model Loader
* implements interface
* located over ServiceLoader-Machanism of java
* canHandle(ModelFile)? 
* if yes, laod the model an give it to the generator

#### Cartridges

* typically responsible for the generation of a certain functionality. For example
    * Entity with Mappings and DDL-Statement
    * REST-Controllers 
    * Processes from a activity diagram
    
* Container for Transformations and CodeGenerator
* maps model elements to code generators
* starts the generation

#### Transformations

* Key to abstraction
* creating new model elements from the given model elements
* adding new tagged values

#### CodeGenerator

#### CodeBlock

### Groovy
