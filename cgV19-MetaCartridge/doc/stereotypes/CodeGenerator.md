
[comment]: <> (THIS FILE IS GENERATED AS LONG AS THIS LINE EXISTS)

# Stereotype "CodeGenerator"

This Stereotype marks classes that are used 
to generate code.

The marked class will be generated as a CodeGenerator and added to the cartridges 
codeBlockMapping.

If you specify a outputType like Java, PHP or DOC, the generated code will set the appropriate output file strategy. If you do not specify the value you have to set the strategy on you own.

## BaseClass(es)
This stereotype is applicable to the following UML-ELements:

* Element

## Associated Tagged Values
| Name | Type | Documentation |
|------|-------|----------------------------------------|
|__generatesOn__| META-DSL.MetaTypes | Specifies the Stereotype that this generator is responsible for.<br/><br/>If you do not specify a value, the generator will never be called. |
|__outputType__| META-DSL.OutputTypeEnum | The kind of output. It can be Java, PHP, TypeScript<br/>DOC or other.<br/>Ich you specify other, you have to provide a <br/>OutputFileStrategy by yourself. |
|__requiredStereotype__| String | The name of the Stereotype that this generator<br/>will act on.<br/> |
|__outputTo__| META-DSL.OutputToEnum | Defines if the code should be generated to src ore src-gen or any other directory.<br/><br/>If you choose src or src-gen the real output depends on the outputType (Java, PHP, DOC etc) |
|__templateScript__| Any | Gives the name of the underlying Groovy Script<br/>for generation. <br/>If not specified, the Script will be generated in a Groovy-Script that names as the class name.  |

