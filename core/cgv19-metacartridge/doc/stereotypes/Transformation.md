
[comment]: <> (THIS FILE IS GENERATED AS LONG AS THIS LINE EXISTS)

# Stereotype "Transformation"



A Transformation runs on an input Model M and enhances this model to a M'.

Transformations can act an any kind of model
elements. But mostly they do transformations on
a special kind of model elements.

You can specify this elements with the tagged
values metaType and requiredStereotype. The
generated code will run only on model elements
that matches this criteria.


## BaseClass(es)
This stereotype is applicable to the following UML-ELements:

* Element


## Associated Tagged Values
| Name | Type | Documentation |
|------|-------|----------------------------------------|
|__transformedMetaType__| adhoc-enum | There is no documentation yet.<br/><br/>__Allowed values:__<br/>* 'MActivity'<br/>* 'MAttribute'<br/>* 'MClass'<br/>* 'MOperation'<br/>* 'MPackage'<br/> |
|__requiredStereotype__| String | There is no documentation yet. |
|__priority__| integer | There is no documentation yet.<br/><br/>_Default Value:_ '0' |

