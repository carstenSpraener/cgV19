# Groovy as template language

Groovy has some nice features that makes it perfect for a 
template language in a generator. This exmaple demonstrates 
the usage of a groovy template to generate the code for a
simple PoJo.

## The Generator-Class

When using groovy templates the generator (method) is just the glue
between cgv19 and the template. All it does is instantiating a 
GroovyCodeBlock, wrap it into a JavaCodeBlock and return it to
cgv19.

Here is the example code of the PoJo-Generator with a groovy template:

```java
    @CGV19Generator(
            requiredStereotype = "PoJo",
            operatesOn = MClass.class,
            outputType = OutputType.JAVA,
            outputTo = OutputTo.SRC_GEN
    )
    public CodeBlock generateWithGroovyTemplate(ModelElement me, String templateName) {
        MClass mc = (MClass)me;
        GroovyCodeBlockImpl gcb = new GroovyCodeBlockImpl("PoJoGenerator", me, "/PoJoTemplate.groovy");
        JavaCodeBlock jcb = new JavaCodeBlock("src/main/java-gen", mc.getPackage().getFQName(), me.getName());
        jcb.addCodeBlock(gcb);
        return jcb;
    }
```

The constructor of the GroovyCodeBlockImpl take three parameter. The name
of the Block, the ModelElement to operate on and the URL to the template
itself. It is good practice to organize the templates in the resource
folder of your project. 

It then wraps the GroovyCodeBlock into a JavaCodeBlock and returns that
to cgv19.

## The groovy template

The template is stored under src/main/resources. When it is called it 
gets a property "modelElement" from cgv19. This is exact the model element
given to the GroovyCodeBlockImpl as second parameter.

The template then generates the code as a string.

```groovy
import de.spraener.nxtgen.ProtectionStrategie
import de.spraener.nxtgen.oom.model.MAttribute
import de.spraener.nxtgen.oom.model.MClass

import static de.spraener.nxtgen.oom.cartridge.JavaHelper.*

MClass mClass = this.getProperty("modelElement");

def String attribureDeclaration(MClass mc) {
    StringBuilder sb = new StringBuilder();
    for(MAttribute a : mc.getAttributes() ) {
        sb.append("    private ${a.type} ${a.name};\n")
    }
    return sb.toString();
}

def String accessors(MClass mc) {
    StringBuilder sb = new StringBuilder();
    for(MAttribute a : mc.getAttributes() ) {
        String mName = firstToUpperCase(a.getName());
        sb.append(
"""    public ${mc.getName()} set${mName}( ${a.getType()} value ) {
        this.${a.getName()} = value;
        return this;
    }
    
    public ${a.type} get${mName}() {
        return this.${a.name};
    }
"""
        );
    }
    return sb.toString();
}

"""// ${ProtectionStrategie.GENERATED_LINE}

public class ${mClass.getName()} {
${attribureDeclaration(mClass)}
    public ${mClass.getName()} {
    }
    
${accessors(mClass)}
}
"""
```

The __in string__ evaluation of groovy together with its multi line 
strings makes it easy readable. Also the handy property access like
```mc.name``` instead of ```mc.getName()``` is an advantage.

It is also very easy to call java methods from within the template.

In the first groovy line after the imports you can see how to 
retrieve the ModelElement given from cgv19:
```groovy
MClass mClass = this.getProperty("modelElement");
```
