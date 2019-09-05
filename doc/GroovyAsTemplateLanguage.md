# Using Groovy as your template engine

Groovy is a language on top of java and is very handy for code generatrion.
That's because it has a nice feature for strings to embed expressions 
to be evaluated later. For example you can write things like:
```groovy
"  public class ${mc.getName()} {"
```

in your template. The expression inside of the `${...}` is evaluated and
inserted into the string. This makes the template much more readable.

## How to use it in your generator

As you read in the [cartridge for a PoJo](../Cartridges.md) a generator
has a method _resolve_. In this method it creates a _JavaCodeBlock_ 
and operates some println-Methods on it. 

To use Groovy as a template language you can add a __GroovyCodeBlockImpl__
to this _JavaCodeBlock_ and give the groovy code block the script that it
should run.

The script hat to return a string. The _JavaCodeBlock_ will add
this string from the _GroovyCodeBlockImpl_ to the output.

Here is a concrete example:

### Adding the GroovyCodeBlock

In your reolveMethod do the following:

```java
    public CodeBlock resolve(ModelElement element, String templateName) {
        MClass mc = (MClass) element;
        JavaCodeBlock jCB = new JavaCodeBlock("src/main/java-gen", RESTJavaHelper.toPkgName(mc.getPackage()), mc.getName());
        
        GroovyCodeBlockImpl gcb = new GroovyCodeBlockImpl("helloWorld", element, "/HelloWorldTemplate.groovy");
        jCB.addCodeBlock(gcb);
        
        return jCB;
    }
```
You still want to generate java so the top _CodeBlock_ should be a _JavaCodeBlock_. But all
the generation work is done in groovy so we just add a _GrooveCodeBlockImpl_ to the
_JavaCodeBlock_. In the constructor you tell the _GroovyCodeBlock_ what's it's name,
what element to generate for and where the groovy script is to find.

The _CodeBlockGeneator_ itself does not have any further code. Just this one method.

### Genrating with groovy

The generation happens inside the groovy script here under `/HelloWorkdTemplate.groovy`
somewhere in your classpath. 

The script will get a variables named _modelElement_ pointing to the
actual modelElement for generation.

To generate the PoJo class body with groovy you could write:


```groovy
import de.spraener.nxtgen.ProtectionStrategie
import de.spraener.nxtgen.oom.model.JavaHelper
import de.spraener.nxtgen.oom.model.MClass
import de.spraener.nxtgen.oom.model.MPackage;


MClass mClass = (MClass)modelElement;
MPackage mPkg = mClass.getPackage();
def pkgName = mPkg.name;
def cName = mClass.name;

String accessMethods(MClass mc) {
    StringBuilder sb = new StringBuilder();
    mc.attributes.forEach( {
        methodName = JavaHelper.toPorpertyName(it.name);
        sb.append(
"""
  public ${it.type} get${methodName}() {
    return ${it.name};
  }

  public void set${methodName}(${it.type} value) {
    this.${it.name} = value;
  }
"""
        );
    })
    return sb.toString()
}

String attributeDefinitions(MClass mc) {
    StringBuilder sb = new StringBuilder();
    mc.attributes.forEach( {
        sb.append(
                
"""
  private ${it.type} ${it.name};
"""
        );
    })
    return sb.toString();
}

return
"""// ${ProtectionStrategie.GENERATED_LINE}
package ${pkgName};

public class ${cName} {
  // generate Attribute definitions
  
  ${attributeDefinitions(mClass)}

  public ${cName}() {
  }

  ${accessMethods(mClass)}

}
"""
```  

I thing this examples shows how smooth groovy can be used to generate
code. It clearly separates template from logic wit it's ${} operator.
The tripple-" makes the writing of output string much more readable
than a list of _println_ statements.

> The disadvantage: In the moment there is no functionality to debug
> your templates. That's a bummer.  
