# Groovy Code Block

This kind of code block supports the implementation of
generator templates in groovy. 

Groovy has a build in template engine that can be very 
usefull for implementing templates.

## Interface
The GroovyCodeBlock is created with a Script-File and
mapped to some kind of model elements. 

On startup the script gets the following variables from
the GroovyCodeBlock:

* modelElement: The ModelElement to generate code for
* codeBlock: The reference to this codeBlock.

The invocation of the script is done in the "toCode"
method.

