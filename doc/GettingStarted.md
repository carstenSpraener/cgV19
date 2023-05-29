# Getting Started with cgV19

## Using cgv19 from maven repository

The easiest way of getting started with cg19 is by seting up a small
gradle project, that uses cgV19 as a component from maven central.

### Requirements
You need to have:
* A bash-like shell. Windows users should install git bash or similar.
* Java 17 or higher. Check with ```java -version```
* Gradle 8.0 or higher. Check with ```gradle --version```
* A text editor of your choice. vi, emacs, VSC, IntelliJ or whatever you want.

### Create a new directory
Go to your favorite project directory and create a new sub directory
lets say __my-cartridge__

```bash
mkdir my-cartridge
cd my-cartridge
```

### Greate a build.gradle file to use cgV19 from maven central

Open your favorite editor and create a new file __build.gradle__ and
copy this content into that build.gradle file:

```groovy
buildscript {
    repositories {
        mavenCentral();
    }
    // add cgv19 to the build scirpts classpath
    dependencies {
        classpath "de.spraener.nxtgen:cgv19-gradle:23.0.0"
        classpath "de.spraener.nxtgen:cgv19-core:23.0.0"
    }
}

plugins {
    id 'java'
    id 'application'
}

// Apply cgV19 plugin to the build
apply plugin: 'de.spraener.nxtgen.cgV19'

// Configure cgV19 to read this model
cgV19 {
    model = "model.oom"
}

repositories {
    mavenCentral()
}

// cgV19 will generate java sources into src/main/java
// AND! src/main/java-gen
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

dependencies {
    // apply this cartridges to cgV19 so we can generate something
    cartridge 'de.spraener.nxtgen:cgv19-core:23.0.0'
    // oom-loader is responsible to loade Object Oriented Models
    cartridge 'de.spraener.nxtgen:cgv19-oom:23.0.0'
    // to create your own cartridge, the meta-cartridge can help
    cartridge 'de.spraener.nxtgen:cgv19-metacartridge:23.0.0'

    // You also need this jars during implementing your cartridge
    implementation 'de.spraener.nxtgen:cgv19-core:23.0.0'
    implementation 'de.spraener.nxtgen:cgv19-oom:23.0.0'
    implementation 'de.spraener.nxtgen:cgv19-metacartridge:23.0.0'

}
```

### Create a little model.oom
OOM stands for __O__ bject __O__ riented __M__ odel and means that you
can specify Packages, Classes, Operations, Attributes, Inheritance... 

But for the first Model we just want one class with a _stereotype_ 
"cgv19Cartridge". 

The language to specify such a model is a Groovy-DSL. Here is the example:

```groovy
import de.spraener.nxtgen.groovy.ModelDSL

ModelDSL.make {
    mPackage {
        name 'my.cartridge'
        mClass {
            name 'MyCartridge'
            stereotype 'cgV19Cartridge'
        }
    }
}
```
Store this model in a file model.oom in the project directory.

Your directory should now look like this:

```text
my-cartridge
??? build.gradle
??? model.oom
```
### Run the generator 
You can run the generator on it's own by calling 

```bash
gradle cgV19
```

but the cgV19-Plugin defines a dependency so that the compile task requires
a cgV19 run. So you can do a simple:

```bash
gradle jar
```

After That your directory should now look like:

```text
my-cartridge
??? build
?   ??? classes
?   ?   ??? java
?   ?       ??? main
?   ?           ??? my
?   ?               ??? cartridge
?   ?                   ??? MyCartridge.class
?   ?                   ??? MyCartridgeBase.class
?   ??? libs
?   ?   ??? my-cartridge.jar
??? build.gradle
??? model.oom
??? src
    ??? main
        ??? java
        ?   ??? my
        ?       ??? cartridge
        ?           ??? MyCartridge.java
        ??? java-gen
        ?   ??? my
        ?       ??? cartridge
        ?           ??? MyCartridgeBase.java
        ??? resources
            ??? META-INF
                ??? services
                    ??? de.spraener.nxtgen.Cartridge

```
Well done! You just created your first cartridge.

## Starting with clone and compile yourself

![GettingStarted.png width="70%"](model/GettingStarted.png)

__Setup the Environment__ Clone the cgV9 Project to your workspace, compile and publish all generator 
components to a local repository directory.

__Setup the project__ Build a new gradle project, modify the build.gradle to make use of tha cgV19
gradle plugin and tell cgV19 where to find the model description and what it should generate.

__Describe a simple model and do Model driven development__ The last step is to describe a model
with a simple PoJo in a DSL, start the genrate process and look that happens 

## Set up the base environment

You need to have the following software installed on your computer:
* Java-SDK 11 or higher
* Gradle 8 (Gradle 6 and 7 may run but untested)
* Optional: [VisualParadigm Community Edition Version 17 or higher](https://www.visual-paradigm.com/download/community.jsp).

If you want to compile cgV19 on your own and using the most recent version, you can
pull it directly from here. Therefore:

Make a directory where you want to start, go into that directory and type:
```
git clone https://github.com/carstenSpraener/cgV19.git
```

This will create a copy of the project in your workspace.


Next step is to build all these projects. To do that change into the directory
core and publish all modules.

```bash
cd cgV19
cd core
gradle publish
```
That will build all core modules and publish them to a local repository in the directory
cgV19/repo. Now that the core is published you can build the plugins and the cartridges.
They depend on the core, so you had to build the core first. Do the following:

```bash
cd ../cartridges
gradle publish
```

You now have the cgV19-RESTCartridge installed in your local repository, and now you can run the final 
build of the demo projects.

```bash
cd ../demoProjects
gradle :restDemo:run
```

That should generate the restDemo application and start it as a Spring Boot application on port 8080. You can
test it in you Browser under the URL [http://localhost:8080/users/ping](http://localhost:8080/users/ping)
If that answers with a __Pong__ you are up and running.

Now everything you need is set up. The required artifacts are now
in a local maven repository directory _repo_ under the cgV19 project
directory.

## Running cgv19 as a standalone application

You can use cgv19 as a command line tool without need of gradle. If you go to the
`cgv19-core` directory and build cgv19 as a installDist,

```bash
gradle :cgv19-core:installDist
```
there will be a command
line tool at `cgv19-core/build/install/cgv19`. Copy this folder into an appropriate place
and add the `bin` subfolder to your classpath.

```bash
export PATH=$PATH:<<CGV19_DIR>>/bin
```

To make use of several cartridges just copy them into the `cartridges` subfolder of
your cgv19 installation.

For example to make use of the provided cartridges, your target installation should 
look like
```text
cgv19
??? bin
?   ??? cgV19
?   ??? cgV19.bat
??? cartridges
?   ??? README.MD
?   ??? cgv19-metacartridge-23.0.0.jar
?   ??? cgv19-oom-23.0.0.jar
?   ??? cgv19-restcartridge-23.0.0.jar
??? lib
    ??? cgv19-core-23.0.0.jar
    ??? groovy-4.0.12.jar
```


## Optional: Building and installing the VisualParadigm plugin

cgV19 provides a plugin for VisualParadigm. With VP installed and setup correctly you
will be able to generate your code directly while  modeling in VP. [VP has a community
edition](https://www.visual-paradigm.com/download/community.jsp) which is completely 
sufficient for your first steps. (And far beyond)

If you want to give it a try install the community edition on your system. Then follow
this steps to build and install the cgV19-VPPlugin.

* go to the `cgV19/core` folder an run `gradle publish`
* Go to the folder `cgV19/plugins/cgV19-VPPlugin`
* run the bash script buildPlugin.sh inside the cgV19-VPPlugin folder (tested under macOS and Linux)
* restart VisualParadigm and check the plugin. The link http://localhost:7001/ping should
response with a "pong!"

YES! You successfully installed the cgV19-VPPlugin into your VisualParadigm.

## Setup a basic gradle project

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

## Configure cgV19 for your project

Next you need to tell cgV19 where it can find the model (oom-File) to generate from. MagicDraw-Users can use a
URL like http://localhost:7000/<root-package-name> to diretcly build from a MagicDraw-Model while modeling.
But for now we will use an ordinary file. So insert the following line to the ```build.gradle```:

```groovy
// Tell cgV19 where it can find the oom-Model. 
// Note: If you have MagicDraw up and running and installed the cgV19-MDPlugin in MagicDraw than you 
// can use a url like http://localhost:7000/<FQ_NAME_OF_YOUR_MODEL_ROOT_PACKAGE>
// for example http://localhost:7000/de.spreaner.nxtgen.hello.world
cgV19 {
    model = './src/main/helloWorld.oom'
    // If you installed VisualParadigm Plugin:
    // model='http://localhost:7001/de.spraener.nxtgen.hello'
}
// Tell gradle that a compile task needs a generation first
tasks.withType(JavaCompile) {
    compileTask -> compileTask.dependsOn generate
}
```

This tells the cgV19-Plugin that your model is described in the given file. The second block is to
tell gradle, that all compile task depending on the generate task. So the generate will be executed
before compilation starts.

Now you have to tell what cartridges you want to use. This is done via the dependencies block of the
build.gradle file. The cgV19-Plugin defines a new dependency-group __cartridge__. Add all required
cartridges, the cgV19-oom module and the cgV19-core module to this dependency group like follows:

```groovy
dependencies {
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

## Defining a model

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

## Running the generator
To transform you model in a java class,  start `gradle generate`. This
will create a new source directory `src/main/java-gen` and inside
that directory a package `de.spraener.nxtgen.hello` wich contains...
I think you can emagine.

Now you can use the generated Person-Class inside your code.

# Some notices for real projects

This is a very very tiny usage of a model driven development stragegie
and it is definitivle not worth the overhead. But in real projects
when the complexity is growing you will benefit from the level of 
abstraction that MDD can reach. Especialy when you make exhausted use
of transformations.

## Defining OOM-Files
The creation of the oom-File by hand is also not very handy. If your
IDE supports groovy-script you will have some kind of syntax highlighting.

But i prefere the use of a UML-Modeling tool like MagicDraw and 
install the MDPlugin there. That will give you the power of describing
your model in UML. 

![Example of an UML-Model for a simple data model](../demoProjects/restDemo/doc/img/class__model__DataModel.png)

And that model will (no MUST) be synchron with your
code. On the long run this will give you a high quality and trustable
documentation.

## What to model and what not to model

Well... that's a good question and a kind of taste. I see nothing
bad at reading an __if__ statement. But 20 if statements can be
hard to understand. If you have such complex situations it's maybe
better to define an activity diagram and abstract to sub activities.

Also the model is and abstraction and that is an absolut MUST! It 
does not make sense to model all decisions and let the code be 
100% generated. That will lead to a model that is hard to understand
and not debugable.

To find the right way is a question of experience. 

## The Generator Gap Pattern

With cgV19 you could definitiv write cartridges that mix hand 
writte (manifested) code and generated code in one file. But I 
prefer a strict separation of files that are generated and files
that are manifested.

This can easily be achieved with the use of the 
__Generator Gap Pattern__. This means you divide a class into two
classes. An abstract 100% generated _*Base_ Class and a manifested
_*Impl_ class that extends the base class. In that way you can put
the Base-classes into the src-gen directory and generate the 
templates for the *Impl-Classes into the src directory. 

But what could the generator do to not override classes that are 
in the java directory, and you enhanced them by hand?

## Protecting from regeneration
Sometimes your generated code is just a template, that the developer
namely you, have to fill in with real code. This code must be protected
from overwriting somehow.

__cgV19__ has a very simple __ProtectionStrategy__. This strategie
looks in the file to be overwritten for a line containing the
string

__THIS FILE IS GENERATED AS LONG AS THIS LINE EXISTS__

If it finds this text in the first 5 lines of the file or if
the file is empty, it will generate the code.

So: If you edited a generated file just remove this line and __cgV9__
will never touch it again. You will surely forget this several times. But I hope
you have a good IDE with a nice UNDO/HISTORY function.
