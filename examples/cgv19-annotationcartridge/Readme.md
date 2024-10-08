# Annotation based cartridges
This is a demo of howto implement a cgv19 cartridge with the
use of annotations.

__Attention!__ The implemented generator writes directly into 
a codeblock. Don't do that in a real project. But as the purpose
of this example is to show the catridge basics and not how to
generate, we leaf it for now.

## Create the project frame
The frame of the project (gradle script, base source, tests) is
generated by executing the following command in the parent
directory:

```bash
cgv19 -m cgv19-annotationcartridge.yml -c cgv19Cartridge
```

## Implement the cartridge
cgv19 will generate a base cartridge setup and the main cartridge
class can be found in ```src/main/java/de/spraener/nxtgen/annotationcartridge```

```java
//THIS FILE IS GENERATED AS LONG AS THIS LINE EXISTS
package de.spraener.nxtgen.annotationcartridge;

import de.spraener.nxtgen.annotations.CGV19Cartridge;

@CGV19Cartridge("AnnontationCartridgeDemo")
public class AnnontationCartridgeDemo extends AnnontationCartridgeDemoBase{
    public static final String NAME = "AnnontationCartridgeDemo";



    public AnnontationCartridgeDemo() {
        super();
    }

    @Override
    public String getName() {
        return NAME;
    }

}
```

## Remove the "generated" marker and start implementing
The first thing to change on that pre generated class is to 
remove the __GENERATED__ marker in the first line. By removing
this line you tell cgv19 to not touch this file anymore.

## Implement a generator
A Generator can be implemented as a method within a class annotated 
as @CGV19Component. The method has to be anotated with @CGV19Generator.

Since there is already a class "AnnotationCartridgeDemo" why not
annotating it as a @CGV19Component and implement the generator 
directly in that class like this:

```java
package de.spraener.nxtgen.annotationcartridge;

import de.spraener.nxtgen.CodeBlock;
import de.spraener.nxtgen.ProtectionStrategie;
import de.spraener.nxtgen.annotations.*;
import de.spraener.nxtgen.java.JavaCodeBlock;
import de.spraener.nxtgen.model.ModelElement;
import de.spraener.nxtgen.oom.model.JavaHelper;
import de.spraener.nxtgen.oom.model.MAttribute;
import de.spraener.nxtgen.oom.model.MClass;

@CGV19Component
@CGV19Cartridge("AnnontationCartridgeDemo")
public class AnnontationCartridgeDemo extends AnnontationCartridgeDemoBase{
    public static final String NAME = "AnnontationCartridgeDemo";

    public AnnontationCartridgeDemo() {
        super();
    }

    @Override
    public String getName() {
        return NAME;
    }

    @CGV19Generator(
            operatesOn = MClass.class,
            outputTo = OutputTo.SRC_GEN,
            outputType = OutputType.JAVA,
            requiredStereotype = "PoJo"
    )
    public CodeBlock generatePoJo(ModelElement element, String templateName) {
        MClass mc = (MClass)element;
        JavaCodeBlock jCB = new JavaCodeBlock("src/main/java-gen", mc.getPackage().getName(), mc.getName() );
        generatePoJo(jCB,mc);
        return jCB;
    }

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

    private void generateAttributeDefinitions(CodeBlock cb, MClass mc) {
        for(MAttribute a : mc.getAttributes() ) {
            String aType = a.getType();
            String aName = a.getName();
            cb.println( "    private "+aType+" "+aName+";");
        }
    }

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
}
```

## Testing the generator
The cgv19 generated project template also contains a test class
in ```src/test/java/TestRun```. This class starts cgv19 with the
current Cartridge on the model in ```src/test/resources/demoapp.oom```

Because the implemented PoJo-Generator reacts on MClasses with
steretype "PoJo" we have to edit this model as shown below:
```groovy
import de.spraener.nxtgen.groovy.ModelDSL

ModelDSL.make {
    mPackage {
        name 'demoapp'
        documentation ''
        metaType 'Package'
        stereotype 'AnnontationCartridgeDemoApp'
        mClass {
            name 'SimplePoJo'
            documentation ''
            metaType 'Class'
            stereotype 'PoJo'
        }
    }
}
```

With that modifications the TestRun can be started. It will
write its output to the ```build/demo-app``` directory.