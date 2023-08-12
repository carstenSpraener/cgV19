
[comment]: <> (THIS FILE IS GENERATED AS LONG AS THIS LINE EXISTS)

# Stereotype "CodeGenerator"

This Stereotype marks classes that are used to generate code.  The marked class will be generated as a CodeGenerator and added to the cartridges codeBlockMapping.  If you specify a outputType like Java, PHP or DOC, the generated code will set the appropriate output file strategy. If you do not specify the value you have to set the strategy on you own. 


## BaseClass(es)
This stereotype is applicable to the following UML-ELements:

* Element


## Associated Tagged Values
| Name | Type | Documentation |
|------|-------|----------------------------------------|
|__requiredStereotype__| String | There is no documentation yet. |
|__outputType__| adhoc-enum | There is no documentation yet.<br/><br/>__Allowed values:__<br/>* 'Java'<br/>* 'PHP'<br/>* 'MarkDown'<br/>* 'TypeScript'<br/>* 'GroovyScript'<br/>* 'other'<br/> |
|__generatesOn__| adhoc-enum | There is no documentation yet.<br/><br/>__Allowed values:__<br/>* 'MActivity'<br/>* 'MAttribute'<br/>* 'MClass'<br/>* 'MOperation'<br/>* 'MPackage'<br/> |
|__outputTo__| adhoc-enum | There is no documentation yet.<br/><br/>__Allowed values:__<br/>* 'other'<br/>* 'src'<br/>* 'src-gen'<br/> |
|__templateScript__| String | There is no documentation yet. |

