# Getting Started with cgV19

## Using cgv19 from maven repository

The easiest way of getting started with cgV19 is by installing the provided
application.

### Requirements

You need to have:

* A bash-like shell. Windows users could install _git bash_ or similar.
* git
* Java 17 or higher. Check with ```java -version```
* Gradle 8.0 or higher. Check with ```gradle --version```
* A text editor of your choice. vi, emacs, VSC, IntelliJ or whatever you want.


## Installing cgv19-cli

Clone the project into a folder of your choice with:

```bash
git clone https://github.com/carstenSpraener/cgV19.git
```
This will create a copy of cgv19 in the subdirectory cgv19. Now you have to change into the
core-directory and build the cli project by:

```bash
cd cgV19/core
gradle :cgv19-cli:installDist
```

After the (hopefully) successfull build you shall copy the created cgv19-tool to a 
location of your choice and add the `bin` subfolder to your PATH:

```bash
cp -r cgv19-cli/build/install/ ~/tools/
export PATH=${PATH}:${HOME}/tools/cgv19-cli/bin
```

To make the installation permanent, add the last export statement to your login script
like `.bashrc`, `.zshrc` or what not.

You should now be able to call cgv19 like:
```bash
❯ cgv19 -l
Aug. 16, 2023 1:40:06 PM de.spraener.nxtgen.cli.CGV19 main
INFORMATION: Running in /Users/casi/tmp/cgV19/core/.
Aug. 16, 2023 1:40:06 PM de.spraener.nxtgen.NextGen loadCartridges
INFORMATION: found 9 cartridges [cgv19Cartridge, cgv19Gradle, JavaLinCartridge, PoJo-Cartridge, REST-Cartridge, Cgv19Angular, ObjectOrientedMetamodel-Cartridge, CloudCartridge, MetaCartridgeBase].
The current cgv19 installation contains the following cartridges:

    * 'cgv19Cartridge' (ModelLoader)
    * 'cgv19Gradle' (ModelLoader)
    * 'JavaLinCartridge'
    * 'PoJo-Cartridge'
    * 'REST-Cartridge'
    * 'Cgv19Angular'
    * 'ObjectOrientedMetamodel-Cartridge'
    * 'CloudCartridge'
    * 'MetaCartridgeBase'

You can choose one of these with the -d <CartridgeName> option.
Aug. 16, 2023 1:40:06 PM de.spraener.nxtgen.cli.CGV19 main
INFORMATION: No model was defined. Nothing to be done.
```

## Build your first cgv19 project

### Initialization with cgv19 

Now, that you have cgv19 up and running let's make use of it. You can use cgv19 to 
setup your first cgv19 project! CGV19 has a __blueprint__ for this. 

Go into your project directory and enter the following command:

```bash
cgv19 -m my-app.yml -c cgv19Gradle
```

The _-m_ options tells cgv19 to use the model _my-app.yml_. The second _-c_ option tells 
cgv19 to start the cartridge __cgv19Project__. This cartridge can
generate a new gradle project upon the model given.

When you hit the enter key, the __cgv19Project__ cartridge tries to open the model
_my-app.yml_. At this moment the model does not exsist and so the cartridge will ask
you to enter some data like here:

```bash
❯ cgv19 -m my-app.yml -c cgv19Gradle


Please give value for 'cgv19-Version':
23.1.0
Please give value for 'projectName':
my-app
Please give value for 'rootPackage':
de.spraener.my.app
```

Cgv19 will create a new project directory `my-app` and inside you will find the following 
content:

```bash
❯ cd my-app
❯ tree .
.
├── build.gradle
├── de.spraener.my.app.oom
└── settings.gradle
```

You can start developing with gradle and your first generator run:

```bash
gradle jar
```
Even if there is no src, gradle will start cgv19 which wil generate your first PoJo and 
build a jar file.

__Congratulations! You set up your first cgv19 Project.__

# Behind the scenes

## What is this oom-File?
The Project created contains a `.oom` file. __OOM__ stands for __O__ bject __O__ riented __M__ odel
and is in fact a _groovy_ script. It defines a package and a class. You can edit this script
with any groovy editor. But there is another option to build such script with Visual Paradigm.
The cgv19-plugin for Visual Paradigm will open a Port on 7001 and serve the oom-File upon 
get requests to the projects root package.

## How distinguish between generated and non-generated source?
In the default behaviour all files with the String 
_THIS FILE IS GENERATED AS LONG AS THIS LINE EXISTS_ somewhere at the top of the file
are considered to be generated and can be overwritten. This may sound strange, but I have made
good experiences with this approach. So, give it a try. You can change this behaviour by
implementing your own protection strategy.

The sources generated are all marked as _Generated_. When you call cgv19 with the _-d_ option
it deletes all generated files.

## How to use more cartridges?
Cartridges are the generator blocks for certain areas like JPA, REST or Cloud. Cgv19 comes 
with som predefined cartridges to support some common tasks. You can create your
own cartridge (of course with the use of cgv19 and the predefined meta-cartridge) and place
the jar file in the catridge-folder of your cgv19 installation. 

# Some notices for real projects

This is a very, very tiny usage of a model driven development strategie,
and it is definitive not worth the overhead. But in real projects
when the complexity is growing you will benefit from the level of
abstraction that MDD can reach. Especially when you make exhaustive use
of transformations.

## Should i checkin genrated sources?
Nearly always: __NO__! For the same reason why you don't checkin .class files. Think about it.
Generated sources are a result of a compilation from the model to source code and only an 
intermediate step. 

There is (for me) only on exception. The _meta-cartridge_! This 
cartridge is generated with the use of cgv19. But when you first don't have cgv19 available,
you would not be able to build cgv19. So in such a situation, you need to checkin the 
generated sources.

## Where to go next?

To get deeper into model driven development, try to follow the use [VisualParadigm to
model your project](building-VisualParadigm-Plugin.md) giude.

After that you may start using more advanced cartridges like the
provided REST- or Cloud-Catridge or even
[start developing your own cartridge](CartridgeDevelopment.md) adapting
the model driven development to your project.
