# The goal of this module

The goal of this module is to ensure that cgV19
is useable and the vgV19-RESTCardtridge can generate real running code [^model]. 


The example application "restDemo" has (in the moment) the following layers.

## Backend and Data-Model Layer

![data model](doc/img/datamodel.png?raw=true Data Model)

It uses the following definitions and relations:
* Resources are marked as << Resources >>
* Entities are marked as << Entity >>
* The model uses One_to_Many relation from User to Adresse
* The model uses Many_to_Many relations form User to Group
* The model uses Inheritance vom Customer to User

## Application Layer

![Application Layer](doc/img/application.png?raw=true)

## Frontend
The Frontend is a generic applicatin in Angular

# What is generated
From this model the cgV19-RESTCartridge generates a fully useable Spring Boot application backend
and a PHP Symfony backend application. In the moment the PHP Backend is not completed.
It does not support associations and inheritance as the Spring Boot application does.

## Generated output

Generaly the output is splitted it two directories. In ```src/main/java-gen``` all
code is generated to 100% and should never need to be touched. This includes the complete 
data model and base classes for controller logic.

The other code is under ```src/main/java``` and contains classes that are intended
to be enhanced by developers. This code can be changed manually and if you do so,
remove the top line in the code saying: 
```java 
// THIS FILE IS GENERATED AS LONG AS THIS LINE EXISTS
````
If you remove this marker line, the generator won't touch this file again. 

## Layers and Cmponents generated (Java)

From the model above the restDemo get's the following layers and Components:

* For each << Entity >> a JPA-Annotated java class in RootPackage/model
* For each << Resource >> a entity derived from this resource under java-gen in RootPackage/model
* For each << Resource >> a base controller under java-gen in RootPackage
* For each << Resource >> a controller impl under java RootPackage
* For each << Resource >> a logic base under java-gen in RootPackage/logic
* For each << Resource >> a logic impl derived from logic base under java in RootPackage/logic
* For each << Resource >> a Repository under java in PackageRoot/model
* From the << SpringBootApp >> a SpringBootApp class with main method under java in RootPackage


[^model]: The model restDemo.oomwas created with MagicDraw 18.5 and 
    the cgV19-MDPlugin installed. If you do not have MagicDraw available, 
    you can edit the restDemo.oom with any text editor. It is basically 
    a Groovy-DSL for defining OOM-Models.
