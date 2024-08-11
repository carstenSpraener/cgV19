# Generator Gap pattern

This example shows the implementation of the__Generator Gap__
pattern with cgv19.

## What is the _Generator Gap_ pattern?

The pattern is a good method to separate generated code from 
templated code in a model driven project. The pattern generates
two classes A and ABase from a single model Class << Stereotype>> A.

The class << Stereotype>> A is transformed to two new classes
<< Stereotype>> A extends ABase and << StereotypeBase>> ABase.

The class ABase is 100% generated and can be regenerated at any
time. Therefor it is generated in the src-gen directory.

The class A is generated as a template for further manual 
development. Therefor it is generated in the src directory.

### Handling inheritance
If the model declares A as an extension of B, then this has
to  be reflected to the code in that form that ABase extends
B.

## Support for the _Generator Gap_ in CGV19

CGV19 comes with a transformation class implementing this 
behaviour. All you need to do is to apply the transformation
to the stereotypes you want to deal with and implement the 
generators form the Stereotype and the StereotypeBase.

```java
    @CGV19Transformation(
            requiredStereotype = "PoJo",
            operatesOn = MClass.class
    )
    public void pojoGeneratorGapTransformation(ModelElement me) {
        MClass mc = (MClass) me;
        new GeneratorGapTransformation().doTransformation(mc);
    }
```
