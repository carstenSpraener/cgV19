# Using Java sources as model (annotation processor)

cgV19 transformations run on a model. Traditionally that model comes from
Visual Paradigm (`.oom` files) or is written as a Groovy DSL script.

The `cgv19-annotationprocessor` module offers a third way: **plain Java source
code is the model**. You define your model as ordinary, compilable Java classes
marked with a small annotation, and cgV19 derives the OOM from them at runtime.
You get full IDE support (compilation, completion, refactoring) and git-friendly
text files instead of a heavyweight UML tool.

This example shows how to integrate the `AnnotationProcessorModelLoader` from
that module into your own project and start cgV19 with the PoJo cartridge.

## 1. Define your model as Java sources

The model lives in `model/src/main/java` — a **separate Gradle module** of this
example:

```text
model/src/main/java/de/spraener/nxtgen/apdemo/
├── meta/PoJo.java   ← the marker annotation, meta-annotated with @Stereotype
└── model/
    ├── Person.java  ← @PoJo, fields name/age + association to Address
    └── Address.java ← @PoJo, fields street/zipCode
```

Why a separate module? The PoJo cartridge generates `Person` and `Address` into
the *same package* as the model classes (generator gap: a generated `PersonBase`
plus a thin `Person` that extends it). Model and generated code therefore share
FQNs, so they must live in different modules — otherwise the IDE reports
"duplicate class" as soon as both directories are source roots. The model module
is a plain Java project that only needs `cgv19-annotationprocessor` on its
classpath (for the `@PoJo`/`@Stereotype` annotations); the app module does
**not** depend on it.

The only convention is the marker annotation. An annotation that is
meta-annotated with `de.spraener.nxtgen.ap.metameta.Stereotype` becomes a
Stereotype in the OOM, named after the annotation's simple name:

```java
@Stereotype
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
public @interface PoJo {
}

@PoJo
public class Person {
    private String name;
    private int age;
    private Address address;   // type inside the model package → association
}
```

That is why the annotation in this example is called `PoJo`: the PoJo cartridge
generates for classes carrying the stereotype `PoJo`.

## 2. Put the ModelLoader on the classpath

The module registers its `AnnotationProcessorModelLoader` via the standard
ServiceLoader file (`META-INF/services/de.spraener.nxtgen.ModelLoader`). So all
you have to do is add the module as a dependency:

```gradle
dependencies {
    cartridge "de.spraener.nxtgen:cgv19-annotationprocessor:24.1.1"
    implementation "de.spraener.nxtgen:cgv19-annotationprocessor:24.1.1"
}
```

The loader handles model URIs with the protocol `java-ap://`:

```text
cgV19 -m java-ap://model/src/main/java -c PoJo-Cartridge
```

It walks the given directory for `.java` files and compiles them **in-process**
(`-proc:only`, no class files), letting the `CGV19OomBuildingProcessor` build
the OOM in memory. Therefore a **JDK** (not just a JRE) is required, and the
sources must compile.

## 3. Start cgV19 with the PoJo cartridge

The `cgV19` Gradle task (from the `de.spraener.nxtgen.cgV19` plugin) is
configured with that model URI:

```gradle
cgV19 {
    model = 'java-ap://model/src/main/java'
}
```

Run it:

```bash
./gradlew cgV19
```

NextGen locates the `AnnotationProcessorModelLoader` via ServiceLoader, asks it
to load `java-ap://model/src/main/java`, and runs every cartridge on the
classpath — here the PoJo cartridge. For each class with stereotype `PoJo` it

* creates a `<Name>Base` class (stereotype `PoJoBase`) holding all attributes
  and associations, generated into `src/main/java-gen`, and
* generates the `<Name>` class itself (extending the Base) into `src/main/java`.

That is the classic *generator gap* pattern: generated data holders plus a thin
class you can extend with your own logic.

The little `TestRun` class does the same in-process (working dir
`build/demo-app`) and asserts on the generated files, so you can start it from
your debugger:

```bash
gradle test
```

The generated files land in `src/main/java` and `src/main/java-gen` of this
project (both are compiled by the build). They carry the protection marker and
are regenerated on every run — feel free to delete them at any time.

## Running this example

From the `examples` directory (composite build against `../core`):

```bash
cd examples
gradle :cgv19-annotationprocessor:test
```

or standalone from this directory (uses the local maven repository in `../repo`):

```bash
gradle test
```

## Notes and limitations

* **All compiled types are modeled** — including the `PoJo` annotation type
  itself. Only classes carrying a stereotype that a cartridge reacts to produce
  output, so the extra model elements are harmless.
* The loader compiles with `-proc:only`: no class files are written, the model
  exists only in memory during the generation run.
* Fields whose type lives inside the model's root package become associations;
  everything else (`String`, `int`, third-party types) becomes a plain attribute.
