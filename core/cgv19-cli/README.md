# CGV19-CLI

Running cgv19 in a gradle build is now only one option to
start with cgv19. With this cli module there is now a second
way to start using cgv19 without an impact to your current
project.

## Installation and running

To install cgv19 you first need to build the installation 
directory by running:

```bash
gradle :cgv19-cli:installDist
```
inside the core directory of cgv19. This will create a 
installation folder under `cgv19-cli/build/install/cgv19-cli`.

You can copy this folder to any target you want. But you hava
to add the `bin` subfolder to your PATH.

```bash
export PATH=$PATH:/[YOUR_CHOOSEN_DIRECTORY]/cgv19-cli/bin
```

After that you can start cgv19 by simple command:

```bash
❯ cgv19
Juli 20, 2023 3:01:57 PM de.spraener.nxtgen.cli.CGV19 main
INFORMATION: Running in /Users/casi/Projekte/GitHub/cgV19/core/cgv19-cli/.
Missing required option: m
usage: utility-name
 -b,--blueprints-dir <arg>   the directory to search for blueprints.
                             Default ist installdir/cartridges/blueprints
 -c,--cartridge <arg>        the names of the cartridge to execute
                             seperated by a colon. If not specified cgv19
                             will run all cartridges.
 -m,--model <arg>            the model to operate on. Could be a file, a
                             directory or a URL
 -w,--work-directory <arg>   the directory in which cgv19 should run.
                             Default is current directory
```
As you can see cgv19 requires at least a model to operate on.

__Well Done! cgv19-cli is ready to go.__

## Adding cartridges to the cgv19-cli

In order to do some more than just reading fancy error messages you can add
cartridges to the cgv19-cli installation. In the installation directory is
a folder "cartridges" in which any jar can be placed. If it contains a cartridge
and/or a model loader cgv19 will use them.

In the default installation there are the following cartridges already
included:

```bash
❯ ls -l build/install/cgv19-cli/cartridges
total 208
-rw-r--r--  1 casi  staff   3918 Jul 20 15:05 cgv19-javapoet-23.1.0.jar
-rw-r--r--  1 casi  staff  39053 Jul 20 15:05 cgv19-metacartridge-23.1.0.jar
-rw-r--r--  1 casi  staff  39703 Jul 20 15:05 cgv19-oom-23.1.0.jar
-rw-r--r--  1 casi  staff  18358 Jul 20 15:05 cgv19-pojo-23.1.0.jar
```

_javapoet is not a cartridge but is needed if you want to make use of 
javapoet in our cartridge_

With this tiny setup you are able to build your own cartridge in a model
driven way with the included metacartridge. When you finished a first 
release of your cartridge add it to the directory and you can use it.

## Mustache-Blueprints (incubator state)
__Attention: This feature is in incubator state__
Mustache is a template framework which is very handy if you have logic free
generation tasks. Good examples are standard files like Angular Components
or docker files.

When you place a directory under `cartridges/blueprints` each of this directories
will become a cartridge on its own. This cartridge can contain mustache templates
and will be processed by the cgv19-cli.

A Blueprint is not only one file but a complete directory-tree with files
and subdirs. Variables are referenced with the `{{ }}`Syntax (hence the name
mustache). In a Blueprint you can also name the subdirectories with mustaches
which are than renamed to a real value.

An example blueprint is included which setups a gradle cgv19 project.

The name of the cartridge will be the name of its directory.

To generate a new cgv19-Project with the cgv19Gradle blueprint
just go into a new directory and execute:

```bash 
cgv19 -m test.props -c cgv19Gradle
```

You will be asked for the necessary parameters, the 
parameter will be stored into the `test.props` file and
the new project will be genareted. 
