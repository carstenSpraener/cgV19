# Release Notes — cgV19 26.0.2

**Release date:** September 2026  
**Previous release:** 26.0.0

This release adds two new APIs that make model transformations and nested code generation dramatically more readable: a **fluent transformation language** on the OOM model elements, and a **TreeWalker visitor engine** for element-bound generation.

---

## Highlights

- **Fluent Transformation Language** — build and enrich models with chainable expressions instead of getter/setter boilerplate
- **TreeWalker Visitor Engine** — walk model trees in pre-/post-order with annotated `@ElementVisitor` methods, the same pattern as ANTLR's `ParseTreeWalker`, applied to model elements

---

## New Features

### Fluent Transformation Language (cgv19-oom)

Transformations are where cartridges enrich a model — new classes, stereotypes, attributes, relations. Until now that meant verbose getter/setter chains. The OOM model elements (`de.spraener.nxtgen.oom.model`) now expose a fluent language:

- **Type-specific factory methods** with varargs `Consumer` modifiers
- The **consumer variant returns the parent** (sibling chaining); the plain variant returns the child
- **Covariant `getParent()`** — typed navigation without casts
- **`addStereotype(name, TaggedValue...)`** with upsert semantics on `ModelElementImpl` (an existing stereotype is updated, never duplicated)

```java
model.createPackage("com.example", pkg -> {
    pkg.createMClass("Person", c -> {
        c.addStereotype("Entity", TaggedValue.of("tableName", "person"),
                                  TaggedValue.of("isRootEntity", true));
        c.createAttribute("name", a -> a.setType("String"))
         .createAttribute("orders", a -> { a.setType("Order"); });
        c.createAssociation("orders", "com.example.Order", "0..*", a -> a.setComposite("true"));
    })
     .createMClass("PersonResource", c -> {
         MPackage p = c.getParent();   // typed navigation, no cast
         c.addStereotype("Rest", TaggedValue.of("apiUrl", "persons"));
         c.createOperation("findAll", op -> {
             op.setType("List");
             op.createParameter("page", pa -> pa.setType("int"));
         });
     });
});
```

**New API methods (selection):**

| Method | Description |
|---|---|
| `OOModel.createPackage(String, Consumer<MPackage>...)` | Create + configure a package; returns the model for chaining |
| `MPackage.createMClass(String, Consumer<MClass>...)` | Create a class in the package; returns the **package** (sibling chaining) |
| `MPackage.findOrCreatePackage(String)` | Look up or create a nested package |
| `MClass.createAttribute / createOperation / createReference(String, Consumer...)` | Create a child; returns the **class** (sibling chaining) |
| `MClass.createAssociation(String, String targetFQName, String multiplicity, Consumer<MAssociation>...)` | Create an association with fluent configuration |
| `MClass.cloneTo(MPackage, String)`, `MAttribute.cloneTo(MClass)` | Clone elements into another container |
| `MOperation.createParameter(String, Consumer<MParameter>...)` | Create a parameter; returns the **operation** |
| `MAttribute.setType`, `MOperation.setType / setParameters`, `MAssociation.set*` | Fluent setters (each returns `this`) |
| `MAbstractModelElement.createDependency(String, Consumer<MDependency>...)`, `putObject / removeObject` | Dependencies + fluent object map |

The activity family (`MActivity`, `MActivityDecision`, `MActivityControlFlow`) is fluent as well — still **ALPHA** as in 26.0.0, subject to breaking changes.

Transformations written with this language plug into the existing `Transformation` SPI (`doTransformation(ModelElement)`); see [LLM-QuickReference.md](LLM-QuickReference.md) §6 for the annotation recipe.

### TreeWalker — Visitor Engine for Nested Generation (cgv19-core)

New package `de.spraener.nxtgen.visitor`: an engine that walks a model tree in pre-/post-order and invokes annotated visitor methods. For each element it runs the matching **BEFORE** visitors, recurses into its children (optionally ordered/filtered), then runs the matching **AFTER** visitors in reverse order — stack semantics. All visitors share one fresh `CodeTarget`, which is returned by `walk()`.

```java
static class PoJoBuilder {

    @ElementVisitor(operatesOn = MClass.class, phase = EBeforeOrAfter.BEFORE)
    public void open(MClass c, ElementContext ctx) {
        ctx.append(JavaSections.CLASS_BLOCK_BEGIN, "public class " + c.getName() + " {");
    }

    @ElementVisitor(operatesOn = MAttribute.class)
    public void field(MAttribute a, ElementContext ctx) {
        ctx.append(JavaSections.ATTRIBUTES, "private " + a.getType() + " " + a.getName() + ";");
    }

    @ElementVisitor(operatesOn = MClass.class, phase = EBeforeOrAfter.AFTER)
    public void close(MClass c, ElementContext ctx) {
        ctx.append(JavaSections.CLASS_BLOCK_END, "}");
    }
}

CodeTarget ct = TreeWalker.on(mClass)
        .order(o -> o.then(MAttribute.class).then(MOperation.class))  // optional child ordering
        .walk();                                                      // → shared CodeTarget
```

**API:**

| Member | Description |
|---|---|
| `TreeWalker.on(ModelElement root)` | Entry point; returns a walker for the tree below `root` |
| `TreeWalker.order(Consumer<ChildOrder>)` | Configure child ordering/filtering (fluent) |
| `TreeWalker.walk()` / `walk(CodeTarget)` | Execute the walk; returns the shared CodeTarget (or reuse your own) |
| `@ElementVisitor(operatesOn, requiredStereotype="", phase=BEFORE, order=0)` | Marks a visitor method. Signature: 2-arg `(element, ctx)`, or 3-arg `(..., EBeforeOrAfter)` for `BOTH` |
| `ChildOrder.then(Class)`, `setPredicate(Predicate)` | Priority list + filter for children; unlisted types sort after listed ones |
| `ElementContext.append(section, code)`, `getCodeTarget()`, `getElement()` | Write into the shared CodeTarget from a visitor |
| `EBeforeOrAfter` | `BEFORE`, `AFTER`, `BOTH` (the phase is passed as the 3rd parameter) |

Matching rules: a visitor fires when `operatesOn` is assignable from the element's runtime class (sub-type matching), `requiredStereotype` is empty or present on the element, and the phase matches. Visitors are resolved via a static `VisitorRegistry` (idempotent re-registration, deterministic `order`). Exceptions in a visitor abort the walk, wrapped in `NxtGenRuntimeException`.

**Cartridge integration:** `@ElementVisitor` methods on any `@CGV19Component` class are discovered and registered automatically when the cartridge is constructed (`AnnotatedCartridgeImpl`) — one shared owner instance per component.

---

## API Changes

### Core (cgv19-core)
**Added:**
- Package `de.spraener.nxtgen.visitor`: `TreeWalker`, `VisitorRegistry` (+ nested `Entry`), `@ElementVisitor`, `ChildOrder`, `ElementContext`, `EBeforeOrAfter`
- `AnnotatedCartridgeImpl`: auto-registration of `@ElementVisitor` methods on components; `getElementVisitors()` accessor
- `ModelElementImpl.addStereotype(String, TaggedValue...)` — upsert semantics
- `TaggedValue.of(String, Object)` factory overloads (String/Boolean/Integer; null stays null)

### OOM Model (cgv19-oom)
**Added:** fluent factory and setter methods on `OOModel`, `MPackage`, `MClass`, `MAttribute`, `MOperation`, `MParameter`, `MReference`, `MAssociation`, the activity family, and `MAbstractModelElement` (see table above); covariant `getParent()` overrides

---

## Bug Fixes

- `NextGen`: the re-entrancy flag for scheduled sub-runs is now reset in a `finally` block. Previously, after any top-level `run()` the flag stayed set, so a **second** `run()` in a long-lived JVM (MCP server, IDE integration) silently skipped all scheduled sub-runs.

---

## Links

- Design doc: [`core/doc/cgv19-Improvements.md`](../core/doc/cgv19-Improvements.md) (Idee 1 = TreeWalker, Idee 2 = fluent transformation language)
- End-to-end fluent example: `core/cgv19-oom/src/test/java/de/spraener/nxtgen/oom/model/TestFluentLanguageEndToEnd.java`
- Visitor examples: `core/cgv19-core/src/test/java/de/spraener/nxtgen/visitor/TreeWalkerTest.java`
