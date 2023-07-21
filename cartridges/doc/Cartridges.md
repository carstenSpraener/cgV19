# A cartridge example

A cartridge combines a set of transformations and generators to
implement a block of functionality. In this example we want to 
build a cartridge for PoJos.

## The jar file

To build a cartridge you should create a new project for a java
library. The library needs to have a class implementing the Cartridge
interface of cgv19. 

In the META-INF/services directory of the jar has to be a file with the
full qualified name of this interface and a single line in it containing
the full qualified name of the implementation.

This is the standard mechanism for the java service loader.

Let's implement the Cartridge in a class called `example.pojo.Cartridge`. 

The structure of your (gradle) project should look like this:

```
src/main/java/example/pojo/Cartridge.java
src/main/resources/META-INF/service/de.spraener.nxtgen.Cartridge
build.gradle
```

The content of the `de.spraener.nxtgen.Cartridge` is just one line:
```
example.pojo.Cartridge
```

That's the base structure of your cartridge.

## Implementing the cartidge

The interface requires three methods to be implemented.

```java
String getName();
List<Transformation> getTransformations();
List<CodeGeneratorMapping> mapGenerators(Model m);
```

#### getName()
This is just a name for logging and can return any string value. When you use 
cgv19 from the command line you can activate a cartridge by its name with the
`-c <NAME>` option.

#### List&lt;Transformation&gt; getTransformations()
List all Transformations, that this Cartridge provides and needs
to be executed.
#### List&lt;CodeGeneratorMapping&gt; mapGenerators(Model m)
List all GeneratorMappings which maps a ModelElement to a Generator.

### Annotation based implementation
Since Release 23.1 of cgV19 it is not needed to implement this 
method yourself, but you can use the AnnotatedGeneratorImpl 
super class to make thinks much easier.

## The PoJoGenerator

> This example uses java itself as language for the templates.
> You have the option to write Templates in Groovy as 
> * [Groovy Templates](doc/GroovyAsTemplateLanguage.md)
> * [Java Poet](../../core/cgv19-javapoet/README.md)
> * [As Mustache Template](../../docs/Release-23.1.md#mustache)
> * [Via CodeTarget approach](../../docs/Release-23.1.md#codeTargets)

cgv19 offers several ways to implement CodeGenerators. For this first
example we will use a standard java _CodeBlock_ to do our generation.
But first we need the _Cartridge_ class itself.
```java
package example.pojo;

@CGV19Cartridge("PoJo-Cartridge")
public class PoJoCartridge extends AnnotatedCartridgeImpl {
}
```
This will define a new Cartridge with the name "PoJo-Cartridge"

### Defining a Generator
The PoJo-Cartrige wants to generate PoJos from a Model. Each MClass with 
stereotype "PoJo" should result in a java class with the declared
attributes and setter and getter methods for them.

So let's define a Generator that handles this like:

```java
@CGV19Component
public class PoJoGeneratorComponent {

    @CGV19Generator(
            requiredStereotype = "PoJo",
            outputTo = OutputTo.SRC_GEN,
            outputType = OutputType.JAVA,
            operatesOn = MClass.class
    )
    public CodeBlock generatePoJo(ModelElement element, String templateName) {
        MClass mc = (MClass)element;
        JavaCodeBlock jCB = new JavaCodeBlock("src/main/java-gen", mc.getPackage().getName(), mc.getName() );
        generatePoJo(jCB,mc);
        return jCB;
    }
}
```

This annotations will result in a Cartridge with one Generator that
is interested in ModelElement of type MClass.class and with the stereotype
"PoJo" applied to them. 

The AnnotatedCartridgeImpl will deal with the model and call the generator
on the requested model elements. Therefor the annotated methods needs to take 
two parameters. The model element and the templateName. The Templatename is not 
used in this example.

Notice that we can safely cast the ModelElement to a MClass. Next 
the generator uses a class _JavaCodeBlock_ from the cgv19-core package.
This _CodeBlock_ is a handy class to generate java output. It is created
with a output directory (here it is "src/main/java-gen"), a package name
and a class name. The _JavaCodeBlock_ will write the source to the correct
directory in the correct output file.
 
When all is set up, the method calls a generatePoJo-Method. And now it comes to 
output.

### generatePoJo-Method

It's always a good idea to break down functionality into sub methods. The generatePoJo-
Method genrates the base of the class an delegates some other generation tasks to 
submethods.

```java
    private void generatePoJo(CodeBlock cb, MClass mc) {
        cb.println("// "+ ProtectionStrategie.GENERATED_LINE);
        cb.println("package "+mc.getPackage().getName()+";");
        cb.println("");
        cb.println("public class "+mc.getName()+" {");
        cb.println("");
        
        generateAttributeDefinitions(cb, mc);
        
        cb.println("");
        cb.println("    public "+mc.getName()+"() {");
        cb.println("        super();");
        cb.println("    }");
        cb.println("");
        
        generateAttributeAccessMethods(cb,mc);
        
        cb.println("");
        cb.println("}");
    }
````
Generating java code is mainly the call of the _println_-Method on a CodeBlock. It is 
pretty forward but let's explain some details.

#### ProtectionLine
First the generator prints out a so called __ProtectionLine__. This is the standard
mechanism of cgv19 to distinguish between code that can be regenerated and code that
shoud not get touched. The standard strategy looks for a line with the content of
`THIS FILE IS GENERATED AS LONG AS THIS LINE EXISTS`. If it finds such a line it the
first 5 lines of a file it will regenerate the file.

To protect your code from overriding by cgv19 just remove this line. 

The protection strategy can be changed, but for now it is good enough. In fact i used
this strategy for years with no problem.

### Package-Statement

Every java class has to declare it's package in the very first line of code. The 
generation of this line shows you the first interaction of your generator with
the meta model of cgv19. As the model loader is the _OOMModelLoader_ our MetaModel
is an instance of an OOM-Model. In an OOMModel every MClass instance has a MPackage
and the MPackage has a name.

This makes the generation of the package-Statement straight forward. But that's only
the case because the meta model is handy for this kind of generation. Other types
of generation my need other meta models. That's why you can exchange a _ModelLoader_
and _Cartridges_ to fit your needs.

### Going further

With the information of the previous chapters you should be able to understand the
rest of the _generatePoJo_-Method. Let's have a look at the other methods. 

```java
    private void generateAttributeDefinitions(CodeBlock cb, MClass mc) {
        for(MAttribute a : mc.getAttributes() ) {
            String aType = a.getType();
            String aName = a.getName();
            cb.println( "    private "+aType+" "+aName+";");
        }
    }
```

The definition of attributes is very easy. Go through all attributes of a class
and print out the type and the name. This is again a good example of the combination
of _MetaModel_ and _Generator_. Since a MClass has MAttributes and a MAttribute has 
a Name and a Type the generation is very easy.

Another thing to mention is, that the generator does not make any use of a visibility
of an attribute. It just asumes that all attributes are __private__. This has the 
disadvantage, that you can't declare a public attribute in your model. But it also 
ensures that there are no public or protected attributes in your PoJo-Farm. That's 
very nice to know. It is a definition of the project and has to be common knowledge 
in your team.

The last method is the _generateAttributeAccessMethods_-Method. 

```java
    private void generateAttributeAccessMethods(CodeBlock cb, MClass mc) {
        for(MAttribute a : mc.getAttributes() ) {
            String aType = a.getType();
            String aName = a.getName();
            String methodName = JavaHelper.toPorpertyName(a);
            cb.println( "    public "+aType+" get"+methodName+"() {");
            cb.println( "        return this."+aName+";");
            cb.println( "    }");
            cb.println("");
            cb.println( "    public void set"+methodName+"( "+aType+" value) {");
            cb.println( "        this."+aName+" = value;");
            cb.println( "    }");
        }
    }
```

I thing you can understand the generator code. The methodName comes from a class
_JavaHelper_ that can translate attribute names to property names. Mainly
transforming _firstName_ to _FirstName_.

## The output

Bind all this classes and service-Files to a jar and add it to the classpath when you 
call cgv19. If you run this cartridge on a OOM-Model like this:

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
it will generate a class _Person_ into your project like this:

```java
// THIS FILE IS GENERATED AS LONG AS THIS LINE EXISTS
package de.spraener.nxtgen.hello.model;

public class Person {

    private String firstName;
    private String name;

    public Person() {
        super();
    }

    public String getFirstName() {
        return this.firstName;
    }

    public void setFirstName( String value) {
        this.firstName = value;
    }
    public String getName() {
        return this.name;
    }

    public void setName( String value) {
        this.name = value;
    }

}
```
