# You are here

![cartridge-you-are-here.png](doc%2Fimages%2Fcartridge-you-are-here.png)

# What is a cartridge
__No! Not this. But the idea is very similar.__

![cartridge-retro.png](doc%2Fimages%2Fcartridge-retro.png)

CGV19 at it's core reads models and operates cartridges on them. That's the whole
story in a nutshell. So the cartridges are the way to make CGV19 usable for your 
needs. 

You can plug in a cartridge into cgv19 and it will run it. But in opposite to
the old game catridges you can plug several cartridges into cgv19 to combine
them and use them together.

There are severa ways to implement your own cartridge. You can choose:
* classic java programming the interafaces.
* model driven with the Meta-Cartridge provided by cgV19
* annotation based cartridge implementation. 

In this document each of the base components building a cartridge are introduced. Each
component is shown in a MDD-Style or as an annotated equivalent.

Let's start.

## The Cartridge itself

![cartridge-model.png](doc%2Fimages%2Fcartridge-model.png)

```java
@CGV19Cartridge("PoJo-Cartridge")
public class PoJoCartridge extends AnnotatedCartridgeImpl {
}
```

cgv19 will call each cartridge with a copy of each loaded model. So
modifications to the model from one cartridge are not visible in the 
second cartridge. A cartridge runs independently of all other plugged 
cartridges. __Note:__ There can be more than one model.

Cartridges interprets a model with its model elements and handle them
according to some rules. Therefor they are providing:

## Transformations
![transformation-model.png](doc%2Fimages%2Ftransformation-model.png)

```java
@CGV19Transformation(
        requiredStereotype = PoJoCartridge.ST_POJO,
        operatesOn = MClass.class
)
public void createGeneratorGapBaseClass(ModelElement me){
}
```

Transformations take a model element and will modify them ore create new 
model elements. Transformations will run on the input model __M__ and all together
they will modify __M__ to a new enhanced model __M'__.

Transformations are the key factor to abstraction. They are the most powerfull
tool in a code generator.

The shown transformtion `createGeneratorGapBaseClass` for example will do the 
following transformation on every MClass with Stereotype &lt;&lt;PoJo&gt;&gt;:

![generatorGapTransformation.png](doc%2Fimages%2FgeneratorGapTransformation.png)

## CodeGenerators
![controller-model.png](doc%2Fimages%2Fcontroller-model.png)

```java
@CGV19Generator(
        requiredStereotype = "PoJo",
        outputTo = OutputTo.SRC,
        outputType = OutputType.JAVA,
        operatesOn = MClass.class
)
public CodeBlock generatePoJo(ModelElement element, String templateName) {
}
```
CodeGenerators do the real work. They receive the elements of the __M'__ model
and will generate the _artifacts_.

You will have many of this code generators in a cartridge. Each type of artifact
will need its own generator. There could be many generators operating on the
same model element. Which generator is mapped to a model element is a decision 
of the cartridge itself.

cgv19 provides some helper classes to generate java, php or type script code.
