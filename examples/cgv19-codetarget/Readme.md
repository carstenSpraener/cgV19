# Using CodeTargets

The easiest way to generate code is by printing it out into
a file. This is often sufficient and easy to understand but
it has a huge disadvantage in bigger project. The reuse of
generator logic is realy complicated and can easy lead to
reinventing the wheel.

The _code target_ approach is a way to deal with this
requirement. It is inspired by __Java Poet__ a generator library
specialized in generating java code. 

With _code target_ you can write a class that creates a 
__CodeTarget__ class. Another class can use this __CodeTarget__
and enhance it for its needs. 

## Small use case
Let's assume you want to write a generator, that generates entities
from a  model class. An entity is a normal PoJo enhanced with some
annotations to describe the mapping to a database.

With _code target_ you can implement the generator exact in that
way. You can write an Entity-Creator that uses the PoJo-Creator 
and enhance the created PoJo-CodeTarget with the annotations
for the database mapping. 

The PoJo-CodeTarget contains all the code for imports, extends, 
implements, attributes, constructor, accessor methods and what
not. 

The Entity-Creator takes the PoJo-CodeTarget locates the 
required CodeSnippets inside the PoJo-CodeTarget and adds
its code to them.

The resulting Entity-CodeTarget is wrapped into a JavaCodeBlock
and given back to cgv19.

## This example

In this example we just add a logger to each PoJo. It is a simple
demonstration of how to create a starting point and enhancing it
with a reusable "JavaLoggerAdding" class. 

### The generator

The generator is implemented as an annotated method

```java
    @CGV19Generator(
            requiredStereotype = "PoJo",
            operatesOn = MClass.class,
            outputTo = OutputTo.SRC_GEN,
            outputType = OutputType.JAVA
    )
    public CodeBlock generateViaCodeTarget(ModelElement me, String templateName) {
        MClass mc = (MClass) me;
        // create a starting point for your extensions
        CodeTarget clazzTarget = new PoJoCodeTargetCreator(mc).createPoJoTarget();

        // apply your enhancements to the code target
        JavaLoggerAdding.addJavaLogging(clazzTarget, mc);

        // wrap the target into a CodeBlock and add it to a JavaCodeBlock to write
        // it as a java class.
        JavaCodeBlock jCB = new JavaCodeBlock("src/main/java-gen", mc.getPackage().getFQName(), mc.getName());
        jCB.addCodeBlock(
                new CodeTargetCodeBlockAdapter(clazzTarget)
        );
        return jCB;
    }
```
It first creates a ```clazzTarget``` with the ```PoJoCodeTargetCreator``` class.
This is the starting point. It than give the ```classTarget``` to
the ```JavaLoggerAdding``` class. After adding the logger logic
to the ```clazzTarget``` it wraps all into a JavaCodeBlock and
returns that block to cgv19.

### The JavaLoggerAdding class

The JavaLoggerAdding class does not know anything about PoJos 
but requires a __CodeTarget__ that holds a Java class and was
created with the ```JavaSections.createJavaCodeTarget()```. So
it can be reused for any such __CodeTarget__.  

Here is the implementation
```java
    public static CodeTarget addJavaLogging(CodeTarget clazzTarget , MClass mc) {
        clazzTarget.inContext("loggingContext", mc,
                // Add the import statement to the IMPORTS section of the java class
                ct -> ct.getSection(JavaSections.IMPORTS)
                        .add("logging", "import java.util.logging.Logger;\n"),

                // Add the static logger declaration to the begin of the class block
                ct -> ct.getSection(JavaSections.CLASS_BLOCK_BEGIN)
                        .add("logging", "    private static final Logger LOGGER = Logger.getLogger(" + mc.getName() + ".class.getName());\n")
        );
        return clazzTarget;
    }
```

As you can see dealing with __CodeTargets__ is a little more 
complex as writing plain templates. But it creates realy 
reusable generator logic.

The methods implementation first locates the IMPORT-section of
the CodeTarget and add an import statement to that section.

After that it locates the begin of the class inner block and
adds the declaration of the logger to it. 