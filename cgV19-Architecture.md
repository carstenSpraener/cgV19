## CodeGen V19

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
