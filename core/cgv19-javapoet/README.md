# Using JavaPoet inside cgV19
If you want to generate java code, and you don't like the template approach you may consider
using this code generator to bind cgV19 with [JavaPoet](https://github.com/square/javapoet).

## JavaPoetGenerator

From the perspective of cgV19 the JavaPoetGenerator is a normal CodeGenerator that
produces a CodeBlock from a ModelElement. But from the cartridge developer perspective
it is a complete different story.

### How a Cartridge maps to an JavaPoetGenerator
A JavaPoetGenerator can only be mapped to a MClass. That makes sense because JavaPoet is
specialized in generating Java classes. At least for now this is a limitation but not realy.

If the cartridge decides that a specific MClass should be generated with JavaPoet it
has to create a CodeGeneratorMapping that holds a JavaPoetGenerator and an MClass. 

The constructor of a JavaPoetGenerator 

```java
    public JavaPoetGenerator(Function<MClass, TypeSpec.Builder> typeSpecCreator, BiConsumer<TypeSpec.Builder, MClass>... typeSpecConsumers) {
```

takes at least a TypeSpec.Builder creator function and a list of TypeSpec.Builder and MClass consumers. The consumers
will receive the TypeSpec.Builder and the ModelElement MClass to fill the Builder.

Here is an example of a Cartridge that maps a MClass with stereotype "PoJo" to a PoJoGenertaor, that uses JavaPoet:
```Java
if (me instanceof MClass mc && StereotypeHelper.hasStereotye(mc, "PoJo")) {
    mappingList.add(
            CodeGeneratorMapping.create(mc, new JavaPoetGenerator(
                    mClazz -> TypeSpec.classBuilder(mClazz.getName()),
                    PoJoGenerator::build)
            )
    );
}
```

The PoJoGenerator.build-Method can now receive the JavaPoet TypeSpec.Builder and the MClass to 
generate the code.
```java
public static void build(TypeSpec.Builder builder, MClass mClass) {
    new PoJoGenerator().generate(builder, mClass);
}
```

## OK, but what's the catch?

Yes, that looks all very similar to the template generator. And you may think that template writing is much easier. Well
it is. But only at the first look.

### Keeping aspects together

With JavaPoet the methods of a generator solve a specific problem like adding an attribute to a class. In a classic
template based generator you fill a specific section of the code. The aspect of adding an attribute will spread out
to several section like:
* Adding imports
* declaring the attributes
* adding setter and getters
* adding 1..1 and 1..n access methods.

When these aspects mix up with other aspects the template easily gets dirty and badly maintainable.

JavaPoet let you generate all these different sections of the output class at one place. This keeps your code much more
maintainable.

__Note__: cgV19 release 23.1 and above has a mechanism to solve this problem with templates as well.

### Generating Entities

When the cartridge gets more and more complex you can define reusable generators
with JavaPoet. That is the reals power of this approach. 


The PoJo generator in the cartridge can do a lot of things:
* It generated field declarations for all attributes of the MClass
* It generates getter and setter methods for each attribute
* it defines references to other PoJos for each aggregation
* it generates access methods to these aggregated classes
* it handles 1..1 and 1..n aggregations and generates the correct methods for these kinds of multiplicity.
* it also handles imports for all kinds of types required

This is something you really want to reuse when you will generate the entity. Especially when the entity is like 
a PoJo but added with some JPA annotations.

With a JavaPoet generator this can easily be achieved. The next example shows how to map Entity Classes to an 
EntityGenerator with pre generating a PoJo:

```Java
if (me instanceof MClass mc && StereotypeHelper.hasStereotye(mc, "Entity")) {
    mappingList.add(
            CodeGeneratorMapping.create(mc, new JavaPoetGenerator(
                    mClazz -> TypeSpec.classBuilder(mClazz.getName()),
                    PoJoGenerator::build,
                    EntityGenerator::build)
            )
    );
}
```

In the EntityGenerator you can now modify/replace the FieldSpecs by simple do:
```java
public static void build(TypeSpec.Builder builder, MClass mClass) {
    new EntityGenerator().generate(builder, mClass);
}

public void generate(TypeSpec.Builder builder, MClass mClass) {
    addEntityAnnotations(builder, mClass);
    for (MAttribute attr : mClass.getAttributes()) {
        FieldSpec fs = findFieldSpec(builder,attr);
        if( fs==null ) {
            continue;
        }
        toEntityFieldSpec(builder, fs);
    }
}
```

As you can see below, the EntityGenerator has no knowledge of how the TypeSpec.Builder was filled. It just needs
the FieldSpecs for each attribute, copy it into a new FieldSpec.Builder, adds the required JPA-Annotation
to that field and replaces it in the TypeSpecBuilder.

```java
private void toEntityFieldSpec(TypeSpec.Builder builder, FieldSpec fs) {
    FieldSpec.Builder entityFieldBuilder = fs.toBuilder();
    entityFieldBuilder.addAnnotation(ClassName.get("javax.persistence", "Column"));
    replaceFieldSpec(builder, fs, entityFieldBuilder.build());
}
```

The EntityGenerator has no idea of how the access methods to the attribute and aggregations are implemented. It
just adds the JPA annotations, as you would do as a programmer.

__Note__: You can NOT convert a FieldSpec into a builder with FieldSpec.toBuilder and add some data to it. This
data will not be part of the TypeSpec. The old FieldSpec must be removed and the new one added. 
