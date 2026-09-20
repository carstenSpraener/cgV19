# cgV19 Quick Reference (for LLMs)

How to create **transformations** and **generators** on top of `cgv19-core`, `cgv19-oom` and
`cgv19-pojo`. Written for language models **with access to this repository**: every claim below
is backed by a source file you can open. When in doubt, read the cited file — it is ground truth.

- §1–§5: mental model, SPIs, registration, model API (read once)
- §6–§9: four complete recipes — copy, adapt, compile
- §10: where to look for deeper topics · §11: pitfalls that break generated code

---

## 1. What cgV19 does

Model-driven code generation:

- A **model** (Groovy `.oom` file, Visual Paradigm via `http://localhost:7001/<pkg>`, or
  `java-ap://<dir>` for code-as-model) is loaded by a pluggable **ModelLoader**.
- Pluggable **cartridges** interpret the model: first they run **transformations**
  (model M → enhanced model M′), then they map **generators** to model elements.
- Each generator produces a **CodeBlock**; CodeBlocks write the generated files into the working dir.

| Module | Role |
|---|---|
| `core/cgv19-core` | Engine + SPIs (`Cartridge`, `Transformation`, `CodeGenerator`, `CodeBlock`), Groovy template bridge, CodeTarget |
| `core/cgv19-oom` | Object-oriented metamodel (`OOModel`, `MClass`, …), `.oom` loader, `OOModelBuilder` |
| `core/cgv19-pojo` | Reference cartridge: Java POJOs from `<<PoJo>>` classes — annotation-based, CodeTarget-driven |

## 2. Pipeline (mental model)

```
modelURI ──ModelLoader.canHandle()──▶ Model M
                                        │  for each selected cartridge:
                                        ▼
                               Transformations (M → M′)   ← in list order, same model instance
                                        │
                                        ▼
                               mapGenerators(M′) → List<CodeGeneratorMapping>
                                        │  for each mapping:
                                        ▼
                               generator.resolve(element, "") → CodeBlock
                                        │
                                        ▼
                               codeBlock.writeOutput(workingDir)   ← file on disk
```

Orchestrated by `de.spraener.nxtgen.NextGen` (`core/cgv19-core/src/main/java/de/spraener/nxtgen/NextGen.java`):

```java
for (Cartridge c : loadCartridges()) {            // ServiceLoader + NextGen.addCartridge()
    if (cartridgeNames.isEmpty() || cartridgeNames.contains(c.getName())) {
        for (Model m : loadModels(this.modelURI)) {   // ModelLoader SPI, canHandle(uri)
            runTransformations(m, c);                    // each Transformation on every element
            for (CodeBlock cb : runCodeGenerators(c, m)) { // mapGenerators → resolve(el, "")
                cb.writeOutput(getWorkingDir());
            }
        }
    }
}
```

Programmatic entry (no CLI):

```java
NextGen cg = new NextGen("path/to/model.oom");   // or http://localhost:7001/<pkg>
cg.run();                                        // workingDir defaults to "."; NextGen.setWorkingDir(dir) requires an EXISTING dir
```

CLI equivalent: `cgv19 -m <model> [-c name[:name…]]` (`-c` selects cartridges, colon-separated).

## 3. The three SPIs (package `de.spraener.nxtgen`)

```java
public interface Cartridge {                                   // core/.../Cartridge.java
    String getName();                                          // used by -c and logging
    List<Transformation> getTransformations();                 // run in list order on M → M′
    List<CodeGeneratorMapping> mapGenerators(Model m);         // element ↔ generator pairs on M′
    default String  evaluate(EvaluationRequest request) {…}    // optional: Inter-Cartridge Evaluation (ICE)
    default CodeBlock subEvaluate(EvaluationRequest request);  // optional: ICE, returns a CodeBlock
    default boolean canHandle(EvaluationRequest request);      // optional: ICE capability check
}

public interface Transformation {                              // core/.../Transformation.java
    void doTransformation(ModelElement element);               // root via element.getModel()
}

public interface CodeGenerator {                               // core/.../CodeGenerator.java
    CodeBlock resolve(ModelElement element, String templateName); // returning null = no output for that element
}

public interface CodeBlock {                                   // core/.../CodeBlock.java
    String toCode();                                           // render self + nested blocks
    void println(String txt);                                  // append one line (SimpleStringCodeBlock)
    void addCodeBlock(CodeBlock subBlock);                     // nest at current position
    String getName();
    void writeOutput(String workingDir);                       // decides the file, applies protection check
    void setToFileStrategy(ToFileStrategy strategy);           // optional: custom file naming
}
```

Useful `CodeBlock` implementations (all in `de.spraener.nxtgen` unless noted):

| Class | What it does |
|---|---|
| `CodeBlockImpl` | Base class: ordered list of child blocks; `toCode()` concatenates children |
| `JavaCodeBlock(srcDir, pkg, className)` (`de.spraener.nxtgen.java`) | Writes `<workingDir>/<srcDir>/<pkg as path>/ClassName.java` |
| `GroovyCodeBlockImpl(name, element, templateURL)` | Runs a Groovy script; binding vars `modelElement`, `mClass` (alias), `codeBlock`; result = script's return value (`String` or `CodeTarget`) |
| `SimpleStringCodeBlock(text)` | Raw text block |
| `CodeTargetCodeBlockAdapter(codeTarget)` (`de.spraener.nxtgen.target`) | Renders a CodeTarget as a block; `.withMarkers()` adds debug comments (not the protection line) |

## 4. Registration & discovery

**Cartridges** are discovered via Java ServiceLoader — file
`src/main/resources/META-INF/services/de.spraener.nxtgen.Cartridge` containing the FQCN of your
cartridge class (one per line). ModelLoaders work the same way (`…services/de.spraener.nxtgen.ModelLoader`).

**Recommended style: annotation-based.** Extend
`de.spraener.nxtgen.cartridges.AnnotatedCartridgeImpl` — it scans **your cartridge's package**
(via `org.reflections`) at construction and wires everything up:

```java
@CGV19Cartridge("MyApp-Cartridge")                 // de.spraener.nxtgen.annotations
public class MyAppCartridge extends AnnotatedCartridgeImpl { }   // name = what -c expects
```

It picks up, without any manual wiring:

- `@CGV19Component` classes whose methods carry `@CGV19Generator(…)` or `@CGV19Transformation(…)`
  (also `@CGV19MustacheGenerator`, `@CGV19Blueprint`). Method-level: any public method with the
  right signature (`CodeBlock resolve(ModelElement, String)` / `void doTransformation(ModelElement)`).
- Standalone classes annotated directly with `@CGV19Generator` (must **implement**
  `CodeGenerator`) or `@CGV19Transformation` (must **implement** `Transformation`).

Component classes need a no-arg constructor (generators may also have one taking `Consumer[]`).

Minimal module skeleton for a new cartridge. A **pure cartridge library needs no cgV19 Gradle
plugin** — that plugin is for *app* projects that generate code at build time (it registers a
`cgV19` task; `compileJava` depends on it — see AGENTS.md). All existing examples apply the
plugin because they double as demo apps; if you do, ship a stub `<last-segment>.oom` at the
module root so the task's model URL can fall back to it (see AGENTS.md, Model loading):

```groovy
// build.gradle — engine dependencies (in examples/ the composite build substitutes 24.1.0 with local core)
dependencies {
    implementation "de.spraener.nxtgen:cgv19-core:24.1.0"
    implementation "de.spraener.nxtgen:cgv19-oom:24.1.0"
    implementation "de.spraener.nxtgen:cgv19-pojo:24.1.0"   // optional, for CodeTarget reuse
    testImplementation 'org.junit.jupiter:junit-jupiter-api:5.9.3'
    testRuntimeOnly 'org.junit.jupiter:junit-jupiter-engine:5.9.3'
    testImplementation 'org.assertj:assertj-core:3.24.2'
}
test { useJUnitPlatform() }
```

plus the `META-INF/services/de.spraener.nxtgen.Cartridge` file (above) and — when developing
inside `examples/` — an `include ':your-module'` line in `examples/settings.gradle`.
See `AGENTS.md` (build layout) for the five-build structure.

**Matching semantics — read carefully:**

- A **transformation** runs on an element when `operatesOn.isAssignableFrom(element.getClass())`
  **and** the element has a stereotype named `requiredStereotype` (empty string = all elements).
- A **generator** matches when `element.getClass().isAssignableFrom(operatesOn)` — the direction
  is inverted! In practice: **always set `operatesOn` to the exact element class**
  (e.g. `MClass.class`). The default `ModelElement.class` will *not* match `MClass` elements.

Annotation attributes (`de.spraener.nxtgen.annotations`):

```java
@CGV19Generator(
    requiredStereotype = "PoJo",          // REQUIRED — stereotype name on the element
    operatesOn = MClass.class,            // default ModelElement.class (see matching above)
    outputTo = OutputTo.SRC_GEN,          // SRC | SRC_GEN | SRC_GEN_DSL | DOC | OTHER (informational)
    outputType = OutputType.JAVA,         // JAVA | TYPESCRIPT | PHP | MARKDOWN | XML | OTHER
    implementationKind = ImplementationKind.GROOVY_TEMPLATE, // …| JAVA_POET | MUSTACHE | BLUEPRINT | CLASS_TARGET
    templateName = ""                     // passed to resolve(); convention for your own use
)

@CGV19Transformation(
    requiredStereotype = "PoJo",          // default "" = all elements of type operatesOn
    operatesOn = MClass.class             // default ModelElement.class
)
```

## 5. Model API cheat sheet (cgv19-oom)

Model classes live in `de.spraener.nxtgen.oom.model`. Every element implements
`ModelElement` (`de.spraener.nxtgen.model`): `getName()`, `getProperty(key)`,
`getStereotypes()`, `getChilds()`, `getParent()`, `getModel()`.

| Class | Key methods you will use |
|---|---|
| `OOModel` (root) | `getClassesByStereotype(String)`, `findClassByName("pkg.Class")`, `createPackage(name, modifiers…)` |
| `MPackage` | `getFQName()`, `createMClass(name)`, nested packages via `findOrCreatePackage` |
| `MClass` | `getAttributes()`, `getAssociations()`, `getOperations()`, `getReferences()`, `getActivities()`, `getDependencies()`, `getPackage()`, `getFQName()`, `hasStereotype(String)`, chainable `createAttribute/createOperation/createAssociation(…)`, `cloneTo(pkg, name)` |
| `MAttribute` | `getName()`, `getType()` (returns `List<T>` if the attribute carries a `*` multiplicity property) |
| `MOperation` / `MParameter` | `getName()`, `getType()`, `getParameters()` |
| `MAssociation` | `getName()`, `getType()` (FQCN of target), `getMultiplicity()` (`"1..1"`, `"1..n"`…) |

**Stereotypes & tagged values** — the primary way cartridges select and configure elements:

```java
import de.spraener.nxtgen.oom.StereotypeHelper;   // works on ANY ModelElement

Stereotype st = StereotypeHelper.getStereotype(me, "Entity");   // null if absent
boolean has  = StereotypeHelper.hasStereotype(me, "Entity");
String tableName = st.getTaggedValue("tableName");              // null if absent
st.setTaggedValue("key", "value");                              // upsert

// MClass convenience:
mc.hasStereotype("PoJo");
```

**Building models in tests** — `de.spraener.nxtgen.oom.OOModelBuilder` (main sources, static factories):

```java
OOModel model = OOModelBuilder.createModel(
    m -> OOModelBuilder.createPackage(m, "de",
        p -> {
            MClass person = OOModelBuilder.createMClass(p, "Person",
                c -> c.createAttribute("name", "String"),
                c -> OOModelBuilder.addStereotype(c, "Entity", "tableName=person"));
            MClass order = OOModelBuilder.createMClass(p, "Order",
                c -> c.createAttribute("total", "BigDecimal"),
                c -> OOModelBuilder.addStereotype(c, "Entity"));
            OOModelBuilder.createAssociation(person, order, "orders", "1..n");
        }));
```

`addStereotype(me, stName, "key=value"…)` accepts `"key=<null>"` for null values.
Shortcut: `OOModelBuilder.createSimpleModelForClass("Person")` → class in package `de.testapp`.

**Loading `.oom` models** — `de.spraener.nxtgen.oom.model.OOMModelLoader`:
`canHandle()` = URI ends with `.oom` or starts with `http`. Load chain: local file → classpath
resource → URL (saves a copy) → **on URL failure silently falls back to `<last-segment>.oom` in
the working dir** (see §11).

`.oom` files are Groovy DSL (`de.spraener.nxtgen.groovy.ModelDSL`). The rules: any `mXxx { … }`
call creates a child element of class `MXxx` (via the element factory); any other `key value`
call sets a property; `stereotype 'Name'[, { taggedValue 'k', 'v' … }]` adds stereotypes:

```groovy
import de.spraener.nxtgen.groovy.ModelDSL

ModelDSL.make {
    mPackage {
        name 'de.example'
        mClass {
            name 'Person'
            stereotype 'Entity', { taggedValue 'tableName', 'person' }
            mAttribute   { name 'name'; type 'String' }
            mAssociation { name 'orders'; type 'de.example.Order'; multiplicity '1..n' }
            mOperation {
                name 'greet'; type 'String'
                mParameter { name 'who'; type 'String' }
            }
        }
    }
}
```

(Working example: `examples/cgv19-mustache/src/test/resources/demoapp.oom`.)

## 6. Recipe A — a Transformation

Transformations enrich the model (M → M′) before generation: derive new classes, add stereotypes
or tagged values, fill defaults. Keep templates simple by moving logic here.

```java
package de.example.myapp;

import de.spraener.nxtgen.annotations.CGV19Component;
import de.spraener.nxtgen.annotations.CGV19Transformation;
import de.spraener.nxtgen.model.ModelElement;
import de.spraener.nxtgen.oom.OOModelBuilder;
import de.spraener.nxtgen.oom.model.MClass;

@CGV19Component
public class MyTransformations {

    @CGV19Transformation(
            requiredStereotype = "Entity",   // only <<Entity>> classes
            operatesOn = MClass.class        // exact element class (see §4)
    )
    public void doTransformation(ModelElement me) {
        MClass mc = (MClass) me;
        // Derive a DTO class next to every entity: M → M′
        OOModelBuilder.createMClass(mc.getPackage(), mc.getName() + "Dto",
                dto -> OOModelBuilder.addStereotype(dto, "Dto"));
    }
}
```

Notes:

- The wrapper instantiates your class (no-arg ctor) and invokes the annotated method per element.
- Order = declaration/discovery order; all transformations of a cartridge run on the **same** model
  instance, so later ones see earlier changes. Each *cartridge* starts from a fresh model copy.
- The canonical non-trivial example is the **Generator Gap** pattern (base class derived, derived
  class hand-written): `core/cgv19-oom/.../cartridge/GeneratorGapTransformation.java` and
  `examples/cgv19-generatorgap/Readme.md`.

## 7. Recipe B — a Generator with a Groovy template

Two files: the generator method + one `.groovy` file per output. The script's **last expression**
is the code (or a `CodeTarget`). Binding variables: `modelElement`, `mClass` (alias), `codeBlock` —
they are plain Groovy properties: use them directly (`modelElement.name`) or via
`this.getProperty("modelElement")`; both work, canonical templates use the latter.

Generator (`@CGV19Component` class, same package as the cartridge):

```java
package de.example.myapp;

import de.spraener.nxtgen.CodeBlock;
import de.spraener.nxtgen.GroovyCodeBlockImpl;
import de.spraener.nxtgen.annotations.*;
import de.spraener.nxtgen.java.JavaCodeBlock;
import de.spraener.nxtgen.model.ModelElement;
import de.spraener.nxtgen.oom.model.MClass;

@CGV19Component
public class MyGenerators {

    @CGV19Generator(
            requiredStereotype = "Dto",
            operatesOn = MClass.class,
            outputTo = OutputTo.SRC_GEN,     // informational; the real dir comes from JavaCodeBlock
            outputType = OutputType.JAVA     // implementationKind defaults to GROOVY_TEMPLATE
    )
    public CodeBlock generateDto(ModelElement element, String templateName) {
        MClass mc = (MClass) element;
        JavaCodeBlock jcb = new JavaCodeBlock(
                "src/main/java-gen", mc.getPackage().getFQName(), mc.getName());
        jcb.addCodeBlock(new GroovyCodeBlockImpl("dto-template", mc, "/MyDto.groovy"));
        return jcb;
    }
}
```

Template `src/main/resources/MyDto.groovy` (canonical pattern from
`examples/cgv19-groovytemplate/src/main/resources/PoJoTemplate.groovy`):

```groovy
import de.spraener.nxtgen.ProtectionStrategie
import de.spraener.nxtgen.oom.model.MAttribute
import de.spraener.nxtgen.oom.model.MClass

MClass mClass = this.getProperty("modelElement")   // binding variable (or just: modelElement)

def String fields(MClass mc) {
    StringBuilder sb = new StringBuilder()
    for (MAttribute a : mc.getAttributes()) {
        sb.append("    private ${a.type} ${a.name};\n")
    }
    return sb.toString()
}

"""// ${ProtectionStrategie.GENERATED_LINE}

public class ${mClass.getName()} {
${fields(mClass)}
}
"""
```

The triple-quoted string is the script's return value → becomes the file content. The marker line
**must be among the first 5 lines** of every generated file (see §11).

## 8. Recipe C — a Generator with CodeTarget (the PoJo way, no templates)

`CodeTarget` = ordered collection of named `CodeSection`s; snippets are appended to sections and
rendered in order. This is how `cgv19-pojo` builds POJOs — and it makes **reusing PoJo's output**
trivial (you start from its target and add your aspects).

```java
package de.example.myapp;

import de.spraener.nxtgen.CodeBlock;
import de.spraener.nxtgen.ProtectionStrategieDefaultImpl;
import de.spraener.nxtgen.annotations.*;
import de.spraener.nxtgen.java.JavaCodeBlock;
import de.spraener.nxtgen.model.ModelElement;
import de.spraener.nxtgen.oom.model.MClass;
import de.spraener.nxtgen.pojo.ClassFrameTargetCreator;  // aspect name of the class declaration
import de.spraener.nxtgen.pojo.PoJoCodeTargetCreator;   // reuse the PoJo cartridge!
import de.spraener.nxtgen.target.CodeTarget;
import de.spraener.nxtgen.target.CodeTargetCodeBlockAdapter;
import de.spraener.nxtgen.target.SingleLineSnippet;
import de.spraener.nxtgen.target.java.JavaSections;

@CGV19Component
public class MyGenerators {

    @CGV19Generator(
            requiredStereotype = "Entity",
            operatesOn = MClass.class,
            outputTo = OutputTo.SRC_GEN,
            outputType = OutputType.JAVA,
            implementationKind = ImplementationKind.CLASS_TARGET
    )
    public CodeBlock generateEntity(ModelElement element, String templateName) {
        MClass mc = (MClass) element;

        // Option 1 — start from the PoJo target (fields + getters/setters for free):
        CodeTarget ct = new PoJoCodeTargetCreator(mc).createPoJoTarget();

        // Option 2 — build from scratch (marker line as preamble!):
        // CodeTarget ct = JavaSections.createJavaCodeTarget(
        //         "// " + ProtectionStrategieDefaultImpl.GENERATED_LINE);

        ct.forAspect("jpa", mc, t -> {
            t.getSection(JavaSections.IMPORTS)
             .add(new SingleLineSnippet("import javax.persistence.Entity;"));
            // position the annotation right before the class declaration line:
            t.getSection(JavaSections.CLASS_DECLARATION)
             .getFirstSnippetForAspect(ClassFrameTargetCreator.CLAZZ_FRAME)
             .insertBefore("@Entity\n");
        });

        JavaCodeBlock jcb = new JavaCodeBlock(
                "src/main/java-gen", mc.getPackage().getFQName(), mc.getName());
        jcb.addCodeBlock(new CodeTargetCodeBlockAdapter(ct));   // or .withMarkers() for debug
        return jcb;
    }
}
```

**CodeTarget inside a Groovy template** (bridges Recipe B and C) — the script's last
expression is a `CodeTarget`, which `GroovyCodeBlockImpl` renders automatically:

```groovy
import de.spraener.nxtgen.ProtectionStrategie
import de.spraener.nxtgen.oom.model.MClass
import de.spraener.nxtgen.target.java.JavaSections

MClass mClass = this.getProperty("modelElement")
def ct = JavaSections.createJavaCodeTarget("// " + ProtectionStrategie.GENERATED_LINE)
ct.setDefaultModelElement(mClass)
   .forAspect('greeter') {
       // no braces here — CLASS_BLOCK_BEGIN/ENDS already render "{" and "}" (see below)
       to JavaSections.CLASS_DECLARATION, "public class ${mClass.getName()}Greeter"
       to JavaSections.METHODS, """    public String greet() { return "Hello"; }"""
   }
ct   // ← last expression = CodeTarget → rendered automatically
```

Key API facts (all in `de.spraener.nxtgen.target`):

- Sections of a standard Java target (`JavaSections`, `de.spraener.nxtgen.target.java`):
  `HEADER, IMPORTS, CLASS_DECLARATION, EXTENDS, IMPLEMENTS, CLASS_BLOCK_BEGIN,
  CONSTRUCTORS, ATTRIBUTE_DECLARATIONS, METHODS, CLASS_BLOCK_ENDS` — rendered in that order.
  `IMPORTS` is a `UniqueLineSection` (duplicates removed); `IMPLEMENTS` joins with `,`.
- `ct.getSection(key)` — also path syntax `"METHODS/IN_OPERATION"` for nested scopes;
  `ct.getOrCreate(path, supplier)` creates on demand.
- **`createJavaCodeTarget` pre-populates sections**: `CLASS_BLOCK_BEGIN` with `{`,
  `CLASS_BLOCK_ENDS` with `}`, plus empty placeholder snippets in `CLASS_DECLARATION`,
  `ATTRIBUTE_DECLARATIONS`, `CONSTRUCTORS`, `METHODS`. If you write your own class
  declaration, **omit the braces** (the defaults render them) — or swap a default via
  `section.replace(old, new)` (⚠ DANGER: the replacement inherits the old snippet's aspect).
  There is **no `clear()`** on sections.
- `ct.append(key, code)` / `getSection(key).add(snippet)` — snippets:
  `SingleLineSnippet(text)`, `CodeBlockSnippet(multiLineText)`; both take optional
  `(aspect, modelElement, text)` for context tracking.
- `ct.forAspect(aspect, me, consumer…)` — scopes snippets under an aspect name (used for
  positioning like `insertBefore`/`getLastSnippetForAspect`).
- Groovy DSL variant (from scripts): the 2-arg `forAspect` uses a *default model element*, so
  chain it after `setDefaultModelElement`:
  `ct.setDefaultModelElement(mClass).forAspect('x') { to JavaSections.IMPORTS, "…" }`.
  DSL verbs: `to section, code` (append), `first section, code`,
  `beforeSnippet/afterSnippet(section, [aspect: …, element: …], code)`, `addSection(name…)`.
  `ct.evaluate('/Script.groovy')` runs an external script (binding: `ct`, `mClass`,
  `modelElement`).
- Rendering: `new CodeTargetToCodeConverter(ct).toString()` → String; or wrap in
  `CodeTargetCodeBlockAdapter` (above). A Groovy template that **returns a CodeTarget** is
  rendered automatically by `GroovyCodeBlockImpl`.

Full worked example (Java API + Groovy DSL + external scripts):
`examples/cgv19-codetarget/Readme.md` and `…/src/main/java/de/spraener/nxtgen/codetarget/CodeTargetDemo.java`.

## 9. Recipe D — testing a cartridge

Pattern from `core/cgv19-pojo/src/test/java/de/spraener/nxtgen/pojo/TestCartridgeRun.java`:
build a small model, run the cartridge's steps manually, assert on the result.

```java
package de.example.myapp;

import de.spraener.nxtgen.CodeBlock;
import de.spraener.nxtgen.Transformation;
import de.spraener.nxtgen.model.ModelElement;
import de.spraener.nxtgen.oom.OOModelBuilder;
import de.spraener.nxtgen.oom.model.MClass;
import de.spraener.nxtgen.oom.model.OOModel;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

public class MyCartridgeTest {

    private OOModel model() {
        return OOModelBuilder.createModel(
                m -> OOModelBuilder.createPackage(m, "de",
                        p -> OOModelBuilder.createMClass(p, "Person",
                                c -> c.createAttribute("name", "String"),
                                c -> OOModelBuilder.addStereotype(c, "Entity"))));
    }

    @Test
    void transformationDerivesDto() {
        OOModel model = model();
        MyAppCartridge uut = new MyAppCartridge();

        for (Transformation t : uut.getTransformations()) {
            for (ModelElement e : model.getModelElements()) {
                t.doTransformation(e);
            }
        }
        assertThat(model.findClassByName("de.PersonDto")).isNotNull();
    }

    @Test
    void generatorProducesCode() {
        MClass person = model().findClassByName("de.Person");
        CodeBlock cb = new MyGenerators().generateDto(person, "");   // call the method directly
        assertThat(cb.toCode()).contains("public class Person");
    }
}
```

Notes: `model.getModelElements()` returns **all** elements (packages, classes, attributes…).
For generator tests you can call the annotated method directly (as above) — no full `NextGen`
run needed. For end-to-end tests, run the whole pipeline against a real `.oom` file in test
resources:

```java
Path tempDir = Files.createTempDirectory("codegen-test");  // must exist before setWorkingDir
NextGen.setWorkingDir(tempDir.toString());
NextGen.clearCartridgeNames();   // reset the static cartridge filter (test isolation)
String oomPath = getClass().getClassLoader().getResource("demo.oom").getFile();
new NextGen(oomPath).run();
// assert on files written under tempDir, e.g. <tempDir>/src/main/java-gen/…
```

Assert **strongly** (exact content, or at least structural checks like brace balance):
`contains`-based asserts cannot catch structural breakage such as duplicated braces from
pre-populated CodeTarget sections (see §8).

(Working example: `examples/cgv19-groovytemplate/src/test/java/TestRun.java` — note it uses
the static `NextGen.main(…)` entry point with a fixed relative dir; for tests that need their
own working dir, prefer `new NextGen(oomPath).run()` as above.)

## 10. Where to look (question → file)

| Question | Read this |
|---|---|
| CodeTarget sections, scopes, DSL in depth | `examples/cgv19-codetarget/Readme.md` (400+ lines, the deep dive) |
| Inter-Cartridge Evaluation (one cartridge calls another's code) | `examples/cgv19-ice/Readme.md` + `Cartridge.evaluate/subEvaluate/canHandle` |
| Groovy template syntax details | `docs/GroovyAsTemplateLanguage.md` + `examples/cgv19-groovytemplate/` |
| Cartridge authoring tutorial (human-oriented, 600+ lines) | `docs/CartridgeDevelopment.md` |
| Generator Gap pattern (base generated, derived hand-written) | `examples/cgv19-generatorgap/Readme.md` + `core/cgv19-oom/.../cartridge/GeneratorGapTransformation.java` |
| How the PoJo cartridge is built (reference implementation) | `core/cgv19-pojo/src/main/java/de/spraener/nxtgen/pojo/` + `cartridges/doc/Cartridges.md` |
| Core architecture (ModelLoader, composite model) | `core/cgv19-core/doc/CoreArchitecture.md` |
| Metacartridge stereotypes (MDD-style cartridge development) | `core/cgv19-metacartridge/doc/stereotypes/*.md` (auto-generated reference) |
| Code-as-model (`java-ap://<dir>`, `@Stereotype` annotations) | `core/cgv19-annotationprocessor/README.md` |
| Mustache templates | `examples/cgv19-mustache/Readme.md` + `@CGV19MustacheGenerator` |
| Build layout, CLI flags, releases (repo operations) | `AGENTS.md` (repository root) |
| First project from scratch (CLI workflow) | `docs/GettingStarted.md` |

## 11. Pitfalls (things that silently break)

1. **Marker line.** Generated files must contain
   `THIS FILE IS GENERATED AS LONG AS THIS LINE EXISTS` (`ProtectionStrategie.GENERATED_LINE`)
   **within the first 5 lines**. Semantics (default `ProtectionStrategieDefaultImpl`):
   file exists **with** marker → overwritten each run; file exists **without** marker → treated
   as hand-written and **never touched**. Groovy templates: print it as the first line.
   CodeTarget: pass `"// " + ProtectionStrategieDefaultImpl.GENERATED_LINE` to
   `JavaSections.createJavaCodeTarget(…)`. Don't confuse this with
   `CodeTargetToCodeConverter.withMarkers()` — that only adds debug comments.
2. **Generator matching direction.** `GeneratorWrapper` checks
   `element.getClass().isAssignableFrom(operatesOn)` — use the **exact** element class in
   `operatesOn` (`MClass.class`). The default `ModelElement.class` matches nothing useful.
   (Transformations use the normal direction and accept superclasses.)
3. **Version skew.** `core` is 26.x; older cartridge buildscripts pin 23.1.0 from Central and the
   local `repo/` holds 24.1.1 → expect `NoSuchMethodError`. Develop against local core
   (examples use a composite build with substitution).
4. **Silent model fallback.** `OOMModelLoader` on a failing URL falls back to
   `<last-segment>.oom` in the working dir — examples ship minimal stubs. Verify you generated
   from the *real* model, not a stub.
5. **CLI flags.** `-d` is a *flag* that deletes generated files; cartridge selection is
   `-c name[:name…]`. Docs claiming `-d <name>` selects a cartridge are wrong.
6. **Git.** `**/*-gen` is gitignored — never check in generated sources, except
   `core/cgv19-metacartridge/src/main/java-gen/**` (tracked on purpose: bootstrap).
7. **Editing generated templates.** Metacartridge-generated `.groovy` files in `resources/`
   carry the marker line — remove it before editing or your edits are wiped on regeneration.
8. **Package scanning.** `AnnotatedCartridgeImpl` scans the cartridge class's own package
   (org.reflections). Put `@CGV19Component` classes in the same package or a subpackage, and
   give them no-arg constructors.
9. **Static engine state.** `NextGen.workingDir` and the cartridge-name filter are static —
    programmatic runs in one JVM share them (`NextGen.setWorkingDir`, `runCartridgeWithName`,
    `clearCartridgeNames`).
10. **Duplicated braces with CodeTarget.** `JavaSections.createJavaCodeTarget` pre-populates
    `CLASS_BLOCK_BEGIN` (`{`) and `CLASS_BLOCK_ENDS` (`}`). If your aspect or template also
    writes the class declaration with braces, the output contains `{ { … } }` — invalid Java
    that `contains`-based tests will not catch. Write the declaration without braces, or
    replace() the default snippets (see §8).
