# Getting Started with cgV19

## Using cgv19 from maven repository

The easiest way of getting started with cgV19 is by setting up a small
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

### Create a build.gradle file to use cgV19 from maven central

Open your favorite editor and create a new file __build.gradle__ and
copy this content into that build.gradle file:

```groovy
buildscript {
    repositories {
        mavenCentral();
    }
    // add cgv19 to the build scripts classpath
    dependencies {
        classpath "de.spraener.nxtgen:cgv19-gradle:23.1.0"
        classpath "de.spraener.nxtgen:cgv19-core:23.1.0"
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
    main {
        java {
            srcDir('src/main/java-gen')
        }
    }
}

dependencies {
    // apply this cartridges to cgV19 so we can generate something
    cartridge 'de.spraener.nxtgen:cgv19-core:23.1.0'
    // oom-loader is responsible to load Object Oriented Models
    cartridge 'de.spraener.nxtgen:cgv19-oom:23.1.0'
    // to create your own cartridge, the meta-cartridge can help
    cartridge 'de.spraener.nxtgen:cgv19-metacartridge:23.1.0'

    // You also need this jars during implementing your cartridge
    implementation 'de.spraener.nxtgen:cgv19-core:23.1.0'
    implementation 'de.spraener.nxtgen:cgv19-oom:23.1.0'
    implementation 'de.spraener.nxtgen:cgv19-metacartridge:23.1.0'

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

![dirTree-beforeGeneration.png](images%2FdirTree-beforeGeneration.png)

### Run the generator

You can run the generator on its own by calling

```bash
gradle cgV19
```

but the cgV19-Plugin defines a dependency so that the compile task requires
a cgV19 run. So you can do a simple:

```bash
gradle jar
```

After That your directory should now look like:

![dirTree-afterGeneration.png](images%2FdirTree-afterGeneration.png)

Well done! You just created your first cartridge.

But maybe you want to dive into the next step...

* [Clone and build by yourself](CloneAndBuildYourself.md)
* [building-VisualParadigm-Plugin.md](building-VisualParadigm-Plugin.md)

### Some notices for real projects

This is a very, very tiny usage of a model driven development strategie,
and it is definitive not worth the overhead. But in real projects
when the complexity is growing you will benefit from the level of
abstraction that MDD can reach. Especially when you make exhausted use
of transformations.

