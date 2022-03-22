# Version history

Versions are build with the year of release, a number
for the releases per year and a third number 
identifiying the fix/hotfix.

Release candidates are postfixed with RCN where 
N is a number.
Snapshots are postfixed with -SNAPSHOT

# 19.0.0
The very first Release of cgV19. This was the
first try to get a library up to maven central
Unfornatly this release is only a POM cause of
a problem in the pom.xml. 

Users should __NEVER__ use this release. Instead
use the 19.0.0-RC1

# 19.0.0-RC1
Release candidate 1. It has all required jars
in it. The cgV19-gradle plugin had a build problem.
Still struggling with mavenCentral. Themse to
me like a Hen Egg dilema.

# 19.0.0-RC2
This is the actual release of cgV19. It contains the core, gradle plugin, a ObjectOrientedMetamodel and a 
smal pojo cartridge to demonstrate how things are working togther. The Readme.md is updated to this version.

cgV19 is no official on mavenCentral and so you can use the gradle plugin in your gradle project.

The build an upload to mavenCentral process is now established. 

# 19.0.0-FINAL
Coming soon. There are several things to do:

* fixing some typos
* make the plugin less chatty abound the __catridge__ class path

# 21.0.0
I got time to improve cgV19. Features are:
* Build restructured to reflect dependencies between the projects
* new MetaCartridge to generate generators
* a new RESTCartridge, that can build a complete Spring Boot application from a model
* 
