![cgv19-logo.png](images%2Fcgv19-logo.png)
# Create your own cartridge

The previous tutorials used a _Cartridge_ for generating simple _PoJos_  from a class. The 
classes were marked with a _Stereotype_. In this tutorial I want to go deeper in what a 
_Cartridge_ is, what a stereotype means in MDD and how to build your own cartridge. 

## What is a cartridge?
![cartridge-retro.png](..%2Fcartridges%2Fdoc%2Fimages%2Fcartridge-retro.png)

A cartridge in cgv19 is a jar file, that contains an extension to cgv19. This
extension is responsible for receiving a _Model_ and generating the code 
from it. In analogy to the old game cartridges you plug the cartridge into
cgv19, so you can generate the artifacts you need on your project.

But in opposit to the old game cartridges, cgv19 is able to handle more than
one cartridge at a time.

In a real MDD project you need to build your own cartridge, so you can provide
a powerfull abstraction in the model for your specific task. Cartridge 
development is therefor an essential part of every MDD project. 

But how can you build your own cartridge?

## Build your first cartridge

Building your first cartridge is quite simple. Of course cgv19 provides a model
driven way to build your own cartridge. To start development go to your project
folder and create a new cartridge frame with:

```bash
cgv19 -m my-cartridge.yml -c cgv19Cartridge
Please give value for 'cartridgeName':
MyCartridge
Please give value for 'cgv19-Version':
23.1.0
Please give value for 'projectName':
my-cartridge
Please give value for 'rootPackage':
de.spraener.my.cartridge
```

Answer the questions, and you will have a new gradle project, that contains
a very base cartridge. Go into that directory and start a gradle jar.
```bash
cd my-cartridge
gradle jar
```
When compilation is finished, you will have a new jar file in `build/libs`. 
This jar file is your new cartridge. Copy the file to your cgv19-cli installation
under the folder `cartridges`. If you followed the _Getting-Started_ tutorial,
this installation directory is `${HOME}/tools/cgv19-cli` so this command will
install the new cartridge:
```bash
cp build/libs/my-cartridge.jar ~/tools/cgv19-cli/cartridges
```
And you can verify the successful installation by calling cgv19 with the _-l_
option:
```bash
cgv19 -l
The current cgv19 installation contains the following cartridges:

    * 'cgv19Cartridge' (ModelLoader)
    * 'cgv19Gradle' (ModelLoader)
    * .....
    * 'MyCartridge'
    * '...

You can choose one of these with the -d <CartridgeName> option.
```
The new cartridge should be listed now.

### The two ways of cartridge development
Your first cartridge is ready. It is installed in cgv19 and will run if
you call it. But it won't do very much. The work in a cartridge is done by
_Transformations_ and _CodeGenerators_. These are the work horses of your 
cartridge and will implement the generator logic.

In cgv19 Version 23.1.0 and above you have two choices for implementing a 
cartridge. You can do it _MDD_ like with the use of Visual Paradigm or 
annotation based with the use of cgv19 provided annotations.

In fact, you can mix up both ways, but I want to you know the two ways. In this
tutorial we will choose the _MDD_ approach. It needs less knowledge of the 
internals of cgv19. And because we are new to cgv19 it's the easier way. 

## Model Driven cartridge development
The _MDD_ method needs at first a _Model_. As shown in the _Visual Paradigm_
tutorial copy the cgv19-cartridges.vpp model into a folder `model` in your
cartridge project folder like this:

_Inside the cartridge project folder_
```bash
mkdir model
# Assuming your cartridge project lies next to the cgV19-Project
cp ../cgV19/cartridges/cgv19-cartridge-stereotypes.vpp model/my-cartridge.vpp
```
Now start _Visual Paradigm_ with the plugin installed and open the 
_my-cartridge.vpp_ model. In _Visual Paradigm_ create a new package with the
name of the root package of your cartridge and inside that package a new 
class with name of your cartridge like `MyCartridge`. Give this class the
stereotype _cgv19Cartridge_.

After that, your model should look like this:
![ct-firstModel.png](images%2Fct-firstModel.png)

Starting cgv19 on this model will generate the same cartridge as before.
```bash
cgv19 -m http://localhost:7001/de.spraener.my.cartridge
```

Open the cartridge project in your IDE and you will see what happend behind
the scenes.

![ct-openIDE.png](images%2Fct-openIDE.png)

cgv19 created the classes and service locator files for the cartridge.
It splitt the cartridge class into two parts. The 100% generated
Base-Class and a derived Cartridge-Class. This is again the _GeneratorGap_
pattern explained in the _Getting-Started_ tutorial.


### Annotation based cartridge development

When you take a close look to the class declaration of the
cartridge, you see an annotation `@CGV19Cartridge`. This is a
annotation, that marks the class as a cartridge and when you follow
the inheritance hirarchy you will see, that the class is a extension
of an `AnnotatedCartridgeImpl`. This two things
(__@CGV19Cartridge__ and __extends AnnotatedCartridgeImpl__) together 
is the second way to build a cartridge. You also need to add the service 
locator file in the `resources/META-INF` directory. But this is all done
with the first generation of your cartridge.

# Adding a Generator
To keep things easy, lets say we want to create _PayLoads_. Let's say
in our project we need some classes for transporting structured data from
a server to a frontend. The server is running a java backend while the
frontend running type script code. The _PayLoad_ is needed in _Java_ and 
in _TypeScript_. The model should act as a single source of truth.

## Modeling the Stereotype and the Generators

In the MDD way add a two new class `PayLoadJavaGenerator`  and `PayLoadTypeScriptGenerator`
with stereotype _CodeGenerator_ to the model.
The Stereotype needs some _Tagged Values_ that you specify in the following
way: After assigning the stereotype _<< CodeGenerator >>_ to a class, open the
specification and go to the _Tagged Values_ tab:

![ct-taggedValuesDefinition.png](images%2Fct-taggedValuesDefinition.png)

For the Java-Generator set the following tagged values:

* requiredStereotype = PayLoad
* outputType = Java
* generatesOn = MClass
* outputTo = src-gen

For the TypeScript Generator set the following tagged values:

* requiresStereotype = PayLoad
* outputType = TypeScript
* generatedOn = MClass
* outputTo = other

After that the model should look like this:
![ct-PayldGeneratorModel.png](images%2Fct-PayldGeneratorModel.png)

> __Note:__ If the model does not show the tagged values, right-click on the
diagramm background and select _Presentation Options/Show Tagged Values/Show Non-Empty_

![ct-showTaggedValues.png](images%2Fct-showTaggedValues.png)

Start cgv19 to generate the frame:

```bash
cgv19 -m http://localhost:7001/de.spraener.my.cartridge
```
Your cartridge project should now contain the following content:
![ct-generatedArtifacts.png](images%2Fct-generatedArtifacts.png)

We now have:
* A CodeGenerator-Class under 'src-gen' for each __<< CodeGenerator >>__ class 
  in the model.
* A Groovy-Script as a template to generate the output under 'resources'
* A method to create the generator-mapping for each generator in the
  MyCartridgeBase-Class.

## Implementing the Generators

Everything is framed so let's get started with implementing the first generator. Let's 
start with the easy one. The TypeScript generator. TypeScript is very eays because you
don't need to generate the accessor methods. Just declare an interface with the required
attributes.

### Test driven development with cgv19
> __Important note for version 23.1.0:__
> 
> _In version 23.1.0 as available in mvnrepository
the class OOModelBuidler is missing. So please copy the following code into your
cartridge projects' `src/test/java` in the package `de.spraener.nxtgen.oom` if
you are using version 23.1.0:  [OOMModelBuilder.java](assets%2FOOMModelBuilder.java)_

To implement the first template let us choose a test driven approach because 
for template writing and maintaining it is very useful to have test cases at hand.

The cartridge is ready to generate the code so the test case can be defined and will
go red because the template is not doing very much. But that's ok for now. Here 
is the test case to check the generation of a simple attribute in TypeScript:

```java
@Test
void testPayLoadTypeScriptGeneratorSimpleAttribute() {
    OOModel m = OOModelBuilder.createModel();
    OOModelBuilder.createPackage(m, "my.test.pkg",
            pkg -> OOModelBuilder.createMClass(pkg, "APayLoad",
                    c -> c.getStereotypes().add(new StereotypeImpl("PayLoad"))
                    c -> c.createAttribute("name", "String"),
            )
    );
    MClass mc = m.findClassByName("my.test.pkg.APayLoad");

    PayLoadTypeScriptGenerator uut = new PayLoadTypeScriptGenerator();
    CodeBlock cb = uut.resolve(mc, "");
    String code = cb.toCode();
    Assertions.assertThat(code)
            .contains("export interface APayLoad {")
            .contains("name: string,")
    ;
}
```
The test case creates a simple model with a package 'my.test.pkg' and inside that package
a class `APayLoad` with stereotype _<< PayLoad >>_ and a attribute `name` of type `String`.

It than locates the `MClass`, create a PayLoadTypeScriptGenerator and let it run 
on that `MClass`. The result of the `resolve` method is a `CodeBlock` which can be
converted to a String with the `toCode()` method.

The generator should generate a TypeScript interface with the name `APayLoad` and 
a field inside that interface with name `name` and type `string`. This is checked
with the help of AssertJs `Assertions.assertThat()` method.

Running the test will create a fail of course.
```log
java.lang.AssertionError: 
Expecting actual:
  "//THIS FILE IS GENERATED AS LONG AS THIS LINE EXISTS
package my.test.pkg;

public class APayLoad {
}
"
to contain:
  "export interface APayLoad {" 
```

So open the `PayLoadTypeScriptTemplate.groovy` and add the following code:

> __IMPORTANT!__ Don't forget to remove the first line
> ```java
>  // THIS FILE IS GENERATED AS LONG AS THIS LINE EXISTS
>  ```

```Groovy
import de.spraener.my.cartridge.TypeScriptHelper
import de.spraener.nxtgen.ProtectionStrategieDefaultImpl
import de.spraener.nxtgen.oom.model.MAttribute
import de.spraener.nxtgen.oom.model.MClass
import de.spraener.nxtgen.oom.model.OOModel

MClass mClass = this.getProperty("modelElement");
OOModel model = mClass.getModel();

String generateAttributes( MClass mc) {
    StringBuilder sb = new StringBuilder();
    for(MAttribute a : mc.getAttributes() ) {
        String type = TypeScriptHelper.toTsType(a.type)
        sb.append( "  ${a.getName()}: ${type},\n")
    }
    return sb.toString();
}


"""//${ProtectionStrategieDefaultImpl.GENERATED_LINE}
export interface ${mClass.getName()} {
${generateAttributes(mClass)}
}
"""
```

The script first gets the MClass in question and the OOModel from the groovy environment.
It than defines a method to generate the attribute declarations.

The template itself begins at the end with a trippel `""""`. This is a groovy multiline
string. Groovy has the very handy opportunity to embedd code in such strings which
are evaluated and the result is placed at the position. This makes groovy ideal for
template writing.

The very first statement in the template is a line, that marks this code as 
generated. So the code will be overwritten with every generator run.

In the next line the definition of the `interface` begins. Here you can see how you
can access properties of a `ModelElement` inside a template. The name of the class
is placed after the `export interface` statement.

In the interface block the method `generateAttribute(mClass)` is called.

#### The generateAttributes() method
The method iterates over all attributes in the `MClass` and prints out each 
attribute name with a type. The types in Angular are a little different from the
types in java. So a converter method is used. 

The converter is implemented as a static method in a `TypeScriptHelper` class. Copy
the following code into the java class `` in the `src/main/java/de/spraener/my/cartridge` directory:

```java
package de.spraener.my.cartridge;

public class TypeScriptHelper {

    /**
     * Converts a Java-Base-Type to a corresponding TypeScript type.
     *
     * @param type
     * @return
     */
    public static String toTsType(String type) {
        if (type.contains(".")) {
            return type.substring(type.lastIndexOf('.') + 1);
        }
        switch (type.toLowerCase()) {
            case "int":
            case "integer":
            case "long":
            case "double":
            case "float":
                return "number";
            case "boolean":
                return "boolean";
            default:
                return "string";
        }
    }
}
```
The name and resulting type is than placed in a String `"${a.getName()}: ${type}"`
and this String is appended to a StringBuilder.

After iterating over all attributes, the method return the StringBuilders String.

If you now run the test, it should be green.

### Adding support for associations

#### Writing a test case

To test associations we need at least two classes in the model and a association of
the one class to another. The association can be a _toOne_ or _toMany_ association which
also should be handled. The test case must create a small model with two classes and 
two associations from class `A` to class `B`. One _toOne_ association and one _toMany_
association. 

It than calls the generator as in the previous test case and checks the result. There
should be one simpe attribute and one array attribute with the references class type.

Here is the code for a new test case to check the generation of associations:

```java
@Test
void testPayLoadTypeScriptGeneratorReference() {
    OOModel m = OOModelBuilder.createModel();
    OOModelBuilder.createPackage(m, "my.test.pkg",
            pkg -> OOModelBuilder.createMClass(pkg, "APayLoad",
                    c -> c.createAttribute("name", "String"),
                    c -> c.getStereotypes().add(new StereotypeImpl("PayLoad"))
            ),
            pkg -> OOModelBuilder.createMClass(pkg, "OtherPayLoad",
                    c -> c.getStereotypes().add(new StereotypeImpl("PayLoad"))
                    )
    );
    MClass mc = m.findClassByName("my.test.pkg.APayLoad");
    MClass other = m.findClassByName("my.test.pkg.OtherPayLoad");
    OOModelBuilder.createAssociation(mc, other, "myOthers", "1..*");
    OOModelBuilder.createAssociation(mc, other, "theOther", "0..1");

    PayLoadTypeScriptGenerator uut = new PayLoadTypeScriptGenerator();
    CodeBlock cb = uut.resolve(mc, "");
    String code = cb.toCode();
    Assertions.assertThat(code)
            .contains("export interface APayLoad {")
            .contains("name: string,")
            .contains("myOthers: OtherPayLoad[],")
            .contains("theOther: OtherPayLoad,")
    ;
}
```

The test expects something like:

```typescript
export interface APayLoad {
  name: string,
  myOthers: OtherPayload[],
  theOther: OtherPayload,
}
```

#### Enhancing the template

To insert the references into the template we should first define a call
to a `generateReferences()`-Method in the template part and implement the
`generateReferences()` method at the top of the script.

To insert the `generateReferences(mClass)` in the template, add the method call into it:
```groovy
"""//${ProtectionStrategieDefaultImpl.GENERATED_LINE}
export interface ${mClass.getName()} {
${generateAttributes(mClass)}
${generateReferences(mClass)}
}
"""
```

The `generateReferences()`-Method implementation should iterate over all associations,
check if it is a _toOne_ or _toMany_ association and add the appropriate code to a 
StringBuilder. It than returns the StringBuilder result.

The check wether we have a _toOne_ or _toMany_ associations is simply done by analyzing
the `multiplicity` property of an association:
```java
boolean isMultipleReference( MAssociation a ) {
    return a.getMultiplicity().endsWith("*") ||a.getMultiplicity().endsWith("n");
}
```
With this done, the `generateReferences()`-Method looks like:
```java
String generateReferences( MClass mc) {
    StringBuilder sb = new StringBuilder();
    for(MAssociation a : mc.getAssociations() ) {
        String type = ModelHelper.findByFQName(mc.getModel(), a.getType(), ".").getName();
        if( isMultipleReference(a) ) {
            sb.append( "  ${a.getName()}: ${type}[],\n")
        } else {
            sb.append( "  ${a.getName()}: ${type},\n")
        }
    }
    return sb.toString();
}
```

### The resulting generated code
When you run the test again, it should now be green and generating some code like:

```typescript
//THIS FILE IS GENERATED AS LONG AS THIS LINE EXISTS
export interface APayLoad {
  name: string,

  myOthers: OtherPayLoad[],
  theOther: OtherPayLoad,

}
```

## Implementing the Java-PayLoad generator

The PayLoads on the java side are simple _<< PoJo >>_ classes and for this purpose there
is already a generator that could be reused. The PoJo-cartridge does not implement the code
generator with a template but with a _CodeTarget_. A _CodeTarget_ is a structured form
of describing code. It has _Sections_ and each _Section_ has a list of _Snippets_. 

You can reference each _Section_ and each _Snippet_ and insert your code before or after
a snippet. You can remove a _Snippet_ or replace it with your own. 

### Implementing a test case

The test case is only neede to check if the generator will produce some code. Since the 
generator only calls the pojo cartridge generator it should be sufficient to know that
it is called. The correctness of the pojo generator is expected.

Similar to the TypeScript test case we again create a small model and start the
generator on the model, reading the generated code and check it with AssertJ:

```java
@Test
void testPayLoadJavaGeneratorProducesCode() {
    OOModel model = OOModelBuilder.createModel();
    OOModelBuilder.createPackage(model, "my.test.pkg",
            p -> OOModelBuilder.createMClass(p, "APayLoad",
                    c -> c.getStereotypes().add(new StereotypeImpl("PayLoad")),
                    c -> c.createAttribute("name", "String")
            )
    );
    MClass mc = model.findClassByName("my.test.pkg.APayLoad");
    PayLoadJavaGenerator uut = new PayLoadJavaGenerator();
    CodeBlock cb = uut.resolve(mc, "");
    String code = cb.toCode();
    Assertions.assertThat(code)
            .contains("public class APayLoad {")
            .contains("private String name;")
            ;
}
```
###  Implementing the script template
But for now we are fine with simple use the _CodeTarget_ provided by the pojo cartridge
and give it back as a string. This all can be done in the `PayLoadJavaTemplate.groovy`
script. Here is the code:

```groovy
import de.spraener.nxtgen.oom.model.MClass
import de.spraener.nxtgen.oom.model.OOModel
import de.spraener.nxtgen.pojo.PoJoCodeTargetCreator
import de.spraener.nxtgen.target.CodeTarget
import de.spraener.nxtgen.target.CodeTargetCodeBlockAdapter

MClass mClass = this.getProperty("modelElement");
OOModel model = mClass.getModel();


CodeTarget ct = new PoJoCodeTargetCreator(mClass).createPoJoTarget();
return new CodeTargetCodeBlockAdapter(ct).toCode();
```
The script simply calls the `PoJoCodeTargetCreator` and converts the `CodeTarget` to
code with the use of the `CodeTargetCodeBlockAdapter`. 

That`s all you need to do to generate a PoJo for your own stereotype.

### Running the test

Running the test should result in a green test case and the following generated code.

```java
//THIS FILE IS GENERATED AS LONG AS THIS LINE EXISTS
package my.test.pkg;


public class APayLoad {

    private String name;

    public APayLoad() {
        super();
    }

    public String getName() {
        return name;
    }


    public void setName( String value) {
        this.name = value;
    }

}
```

# Summary

__Well done!__ you created your first cartridge and add two code generators to it. You used
_model driven development_ to create the cartridge frame, create _CodeGenerators_ and
bind them to a _ModelElement_ with given _Stereotype_. From here you should be able to
follow the more complex cartridges like cgv19-restcartridge, cgv19-cloud or other
provided cartridges and addapt or enhance them to your own needs.

What you've learned:

## The Metacartridge

The _Metacartridge_ provides a cgv19 cartridge to implement new cgv19 cartridges in 
a model driven way. It provides Stereotypes for _cgv19Cartridge_, which will result
in a new cartridge definition, and a _CodeGenerator_ stereotype which results in a new 
_CodeGenerator_ class.

## MetaModel and Stereotypes

Each _CodeGenerator_ is mapped to a _ModelElement_ like _MClass_ and a _Stereotype_. 
The Cartridge will do the mapping and the taks of the _CodeGenerator_ is to generate
the code from the _ModelElement_. The code generation itself is done in a groovy script.

## Groovy-Script and Template
A generator can use a groovy script to biuld a template. Groovy has the nice feature
of multi line string with code evaluation which makes it possible to write very 
readable template.

The logic needed to create some code, can be in the script and can be called inside
a groovy String.

## Reuse of PoJo Cartridge
The PoJo-Cartridge provide a PoJoCodeTargetCreator. This class creates a _CodeTarget_
that contains the code for a PoJo. It can be converted to a String with a 
_CodeTargetCodeBlockAdapter_. 

# More about CodeTargets

At a first look the code generation via template looks much easier than using 
`CodeTargets`. But `CodeTargets` are very handy when you want to enhance java code.
You can reference each `Snippet` in the `CodeTarget`and add annotations or other
enhancements to the generated class. Think about building a JPA generator. You 
can use the PoJo CodeTarget. It will define each attribute, access methods and
deal with the imports. It also handles the inheritance correctly. Your JPA
generator just needs to add the JPA related annotations. 

Or you want to make your classes _Serializable_. This would require two things. 

1) Import the java.io.Serializable interface
2) add the Serializable interface to the implements section.

In a template driven implementation you have to splitt these aspects into two parts.
First part where you list all import statements and the second part when you declare
the class. 

With CodeTarget driven development the whole logic needed is kept together in one 
place.

Here a small example:

```java
CodeTarget target =....
target.inContext(JavaAspects.SERIALIZABLE, null, t -> {
     t.getSection(JavaSections.IMPORTS)
        .add(new SingleLineSnippet("import java.io.Serializable;"));
     t.getSection(JavaSections.IMPLEMENTS)
        .add(new CodeBlockSnippet("Serializable"))
        .add(new CodeBlockSnippet("Runnable"));
});
```

