
[comment]: <> (THIS FILE IS GENERATED AS LONG AS THIS LINE EXISTS)

# Stereotype "Transformation"

A Transformation runs on an input Model M and enhances this model to a M'.

Transformations can act an any kind of model
elements. But mostly they do transformations on
a special kind of model elements. 

You can specify this elements with the tagged
values metaType and requiredStereotype. The generated code will run only on model elements
that matches this criteria.

## BaseClass(es)
This stereotype is applicable to the following UML-ELements:

* Element

## Associated Tagged Values
| Name | Type | Documentation |
|------|-------|----------------------------------------|
|__transformedMetaType__| META-DSL.MetaTypes | if specified this transformation will only run on<br/>model elements of this type. The metaType can<br/>be one of the enumeration literals in MetaTypes.<br/><br/>If you do not specify this value, there will<br/>be no filtering on meta type at all.<br/> |
|__requiredStereotype__| String | The transformation will only run on model <br/>elements with the stereotype set.<br/><br/>This could be any kind of model element. If you<br/>need a special meta model type, you can combine<br/>this value with the transformationMetaType value.  |
|__priority__| Integer | The executiopn of all Transformations in a cartridge is ordered by this priority. <br/><br/>if it is not defined it will be Integer.MAX_VALUE |

