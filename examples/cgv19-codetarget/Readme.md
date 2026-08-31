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
from a model class. An entity is a normal PoJo enhanced with some
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

## Two Approaches: Java API vs Groovy DSL

You can work with CodeTargets in two ways:
1. **Java API** - write Java classes that manipulate CodeTargets programmatically
2. **Groovy DSL** - write Groovy scripts with a fluent, chainable API

Both approaches support the same core concept: **reuse existing CodeTargets** (e.g., from PoJo cartridge) and enhance them with orthogonal aspects.

### Approach 1: The Java API

The generator is implemented as an annotated method:

```java
@CGV19Generator(
    requiredStereotype = "PoJo",
    operatesOn = MClass.class,
    outputTo = OutputTo.SRC_GEN,
    outputType = OutputType.JAVA
)
public CodeBlock generateViaCodeTarget(ModelElement me, String templateName) {
    MClass mc = (MClass) me;
    
    // Create a starting point from PoJo cartridge
    CodeTarget clazzTarget = new PoJoCodeTargetCreator(mc).createPoJoTarget();

    // Apply enhancements
    JavaLoggerAdding.addJavaLogging(clazzTarget, mc);
    EntityEnhancement.addEntityEnhancement(clazzTarget, mc);

    // Wrap into a JavaCodeBlock
    JavaCodeBlock jCB = new JavaCodeBlock("src/main/java-gen", mc.getPackage().getFQName(), mc.getName());
    jCB.addCodeBlock(new CodeTargetCodeBlockAdapter(clazzTarget));
    return jCB;
}
```

The `JavaLoggerAdding` class is reusable because it only requires a CodeTarget created with `JavaSections.createJavaCodeTarget()`:

```java
public static CodeTarget addJavaLogging(CodeTarget clazzTarget, MClass mc) {
    clazzTarget.forAspect("logging", mc,
        ct -> ct.getSection(JavaSections.IMPORTS)
            .add(new SingleLineSnippet("logging", "import java.util.logging.Logger;")),
        ct -> ct.getSection(JavaSections.CLASS_BLOCK_BEGIN)
            .add(new SingleLineSnippet("logging", "    private static final Logger LOGGER = Logger.getLogger(" + mc.getName() + ".class.getName());"))
    );
    return clazzTarget;
}
```

### Approach 2: The Groovy DSL (Recommended)

The Groovy DSL provides a **fluent, chainable API** that is more readable and easier to maintain. It uses `GroovyCodeBlockImpl` which loads a Groovy script from the classpath and executes it.

#### The Generator

```java
@CGV19Generator(
    requiredStereotype = "PoJo",
    operatesOn = MClass.class,
    outputTo = OutputTo.SRC_GEN,
    outputType = OutputType.JAVA
)
public CodeBlock generateViaCodeTargetScript(ModelElement me, String templateName) {
    MClass mc = (MClass) me;
    JavaCodeBlock jCB = new JavaCodeBlock("src/main/java-gen", mc.getPackage().getFQName(), mc.getName() + "Script");
    jCB.addCodeBlock(new GroovyCodeBlockImpl("dsl-script", mc, "/DemoApp.groovy"));
    return jCB;
}
```

#### The Groovy Template (`DemoApp.groovy`)

The script has access to two variables:
- `modelElement` - the ModelElement being processed
- `mClass` - an alias for modelElement (convenience)

```groovy
import de.spraener.nxtgen.oom.model.MClass
import de.spraener.nxtgen.pojo.PoJoCodeTargetCreator
import de.spraener.nxtgen.target.java.JavaSections

// 1. Cast modelElement to MClass for type-safe access
def MClass mClass = (MClass) modelElement

// 2. Get CodeTarget from PoJo cartridge (reusable base with all sections)
def ct = new PoJoCodeTargetCreator(mClass).createPoJoTarget()

// 3. Set default model element for chainable DSL
ct.setDefaultModelElement(mClass)

// 4. Apply custom aspects using the chainable DSL
ct.forAspect('logging') {
    // Add Logger import (deduplicated by UNIQUE_LINES section)
    to JavaSections.IMPORTS, "import java.util.logging.Logger;"
    
    // Add Logger field to class body (CLASS_BLOCK_BEGIN is before attributes)
    to JavaSections.CLASS_BLOCK_BEGIN, 
        "    private static final Logger LOGGER = Logger.getLogger(${mClass.name}.class.getName());"
    
    // Add logging call before constructor close using beforeSnippet
    beforeSnippet JavaSections.CONSTRUCTORS, [aspect: 'clazz-default-constructor.close'], 
        "        LOGGER.trace(\"new Instance of ${mClass.name}.\");"
}

ct.forAspect('entity') {
    // Add JPA imports
    to JavaSections.IMPORTS, "import javax.persistence.*;"
    
    // Add @Entity and @Table annotations before class declaration
    def tableName = mClass.name.toLowerCase()
    beforeSnippet JavaSections.CLASS_DECLARATION, [aspect: 'clazz-frame'], 
        "@Entity \n@Table(\"${tableName}\")\n"
}

// 5. Evaluate external scripts for additional aspects
ct.evaluate('Logging.groovy')   // Loads and executes Logging.groovy from classpath
ct.evaluate('Entity.groovy')    // Loads and executes Entity.groovy from classpath

// 6. Return the CodeTarget (will be rendered by GroovyCodeBlockImpl)
return ct
```

#### Key DSL Features

| Method | Description |
|---|---|
| `ct.setDefaultModelElement(mClass)` | Sets the default model element for 2-arg `forAspect`. Chainable. |
| `ct.forAspect('name') { }` | 2-arg version using defaultModelElement. Chainable. |
| `to Section, "code"` | Adds a snippet at the end of the section. Accepts enum (JavaSections) or String. |
| `first Section, "code"` | Adds a snippet at the beginning of the section. |
| `beforeSnippet Section, [aspect: '...'], "code"` | Inserts before the first snippet matching the criteria. |
| `afterSnippet Section, [aspect: '...'], "code"` | Inserts after the first snippet matching the criteria. |
| `ct.evaluate('Script.groovy')` | Loads and executes an external Groovy script. Chainable. |

#### External Scripts (`Logging.groovy`, `Entity.groovy`)

External scripts loaded via `evaluate()` have access to:
- `ct` - the CodeTarget
- `mClass` - the default model element
- `modelElement` - alias for mClass

Example (`Logging.groovy`):
```groovy
import de.spraener.nxtgen.target.java.JavaSections

ct.forAspect('external-logging') {
    to JavaSections.IMPORTS, "import java.util.logging.Level;"
    
    to JavaSections.METHODS, """
        public void logDebug(String message) {
            LOGGER.log(Level.FINE, "DEBUG: " + message);
        }
"""
}
```

## Why CodeTargets?

As you can see dealing with __CodeTargets__ is a little more 
complex as writing plain templates. But it creates really 
reusable generator logic because:

1. **Orthogonal aspects** - each aspect (logging, entity, etc.) can be added independently
2. **Reusability** - PoJo cartridge provides a base that any other generator can enhance
3. **Precise insertion** - `beforeSnippet`/`afterSnippet` allow inserting code at exact positions
4. **Deduplication** - `UniqueLineSection` automatically deduplicates imports
5. **Two APIs** - choose Java for complex logic, Groovy DSL for readability

## Available Section IDs (JavaSections)

When using a CodeTarget created with `JavaSections.createJavaCodeTarget()`, these sections are available:

| Section ID | Purpose |
|---|---|
| `JavaSections.HEADER` | Package declaration and preamble |
| `JavaSections.IMPORTS` | Import statements (deduplicated) |
| `JavaSections.CLASS_DECLARATION` | Class declaration with annotations |
| `JavaSections.EXTENDS` | Extends clause |
| `JavaSections.IMPLEMENTS` | Implements clause (comma-separated) |
| `JavaSections.CLASS_BLOCK_BEGIN` | Opening brace and static fields |
| `JavaSections.ATTRIBUTE_DECLARATIONS` | Instance attributes |
| `JavaSections.CONSTRUCTORS` | Constructor methods |
| `JavaSections.METHODS` | Regular methods |
| `JavaSections.CLASS_BLOCK_ENDS` | Closing brace |

You can use these as enum values (`JavaSections.IMPORTS`) or their string representation in the DSL.

## Creating a Custom Table of Contents

When you need more control over the structure than `JavaSections` provides, you can create your own CodeTarget with a custom table of contents (ToC). There are two ways to do this:

### Method 1: Using `CodeTargetDSL.build()` (Recommended for new CodeTargets)

Use this when you want to create a completely custom CodeTarget from scratch:

```groovy
import de.spraener.nxtgen.target.dsl.CodeTargetDSL as DSL
import de.spraener.nxtgen.target.SectionType

def ct = DSL.build {
    tableOfContents {
        section 'header'                              // SIMPLE type (default)
        section 'imports', SectionType.UNIQUE_LINES  // deduplicates lines
        section 'classDeclaration'
        section 'extends'
        section 'implements', SectionType.PREFIXED_LIST, 
            [prefix: 'implements ', sep: ', ']       // comma-separated list with prefix
        section 'classBlockBegin'
        section 'attributes'
        section 'constructors'
        section 'methods'
        section 'classBlockEnd'
    }
}

// Now apply aspects to your custom CodeTarget
ct.setDefaultModelElement(mClass)
    .forAspect('java-frame') {
        to 'header', "package ${mClass.package.fqName};"
        to 'classDeclaration', "public class ${mClass.name} {"
    }
```

### Method 2: Using `addSection()` within `forAspect` (For extending existing CodeTargets)

Use this when you have an existing CodeTarget (e.g., from PoJo cartridge) and want to add new sections:

```groovy
import de.spraener.nxtgen.pojo.PoJoCodeTargetCreator
import de.spraener.nxtgen.target.SectionType

// Start with PoJo CodeTarget
def ct = new PoJoCodeTargetCreator(mClass).createPoJoTarget()
ct.setDefaultModelElement(mClass)

// Add a new section after an existing one
ct.forAspect('custom-extension') {
    // Add a new SIMPLE section after METHODS
    addSection 'validation-methods', JavaSections.METHODS
    
    // Add a new UNIQUE_LINES section at the end
    addSection 'custom-imports', SectionType.UNIQUE_LINES
    
    // Now use the new sections
    to 'validation-methods', """
        public boolean isValid() {
            return name != null && !name.isEmpty();
        }
    """
    
    to 'custom-imports', "import org.springframework.validation.*;"
}
```

### Section Types Explained

| Type | Behavior | Use Case |
|---|---|---|
| `SectionType.SIMPLE` | Appends all snippets in order | Class declarations, method bodies |
| `SectionType.UNIQUE_LINES` | Deduplicates identical lines automatically | Import statements, annotations |
| `SectionType.PREFIXED_LIST` | Joins snippets with separator and optional prefix | `implements A, B, C`, `extends X` |

### Configuring PREFIXED_LIST

```groovy
section 'implements', SectionType.PREFIXED_LIST, [
    prefix: 'implements ',   // prepended to the joined output
    sep: ', '                // separator between snippets
]

// If you add: "Serializable" and "Runnable"
// Output: implements Serializable, Runnable
```

### Combining Both Approaches

You can use both methods together - create a custom ToC and then extend it:

```groovy
// 1. Create custom base
def ct = DSL.build {
    tableOfContents {
        section 'header'
        section 'imports', SectionType.UNIQUE_LINES
        section 'classDeclaration'
        section 'body'
    }
}

// 2. Extend with additional sections via forAspect
ct.setDefaultModelElement(mClass)
    .forAspect('base') {
        to 'header', "package ${mClass.package.fqName};"
    }
    .forAspect('extension') {
        addSection 'footer', JavaSections.body  // after body section
        to 'footer', "// End of ${mClass.name}"
    }
```

This flexibility allows you to create highly reusable generator components that can be composed and extended in any way you need.
