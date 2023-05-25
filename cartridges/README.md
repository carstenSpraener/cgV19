# You are here

![cartridge-you-are-here.png](..%2Fcore%2Fcgv19-metacartridge%2Fdoc%2Fimages%2Fcartridge-you-are-here.png)

# What is a cartridge
__No! Not this. But the idea is very similar.__

![cartridge-retro.png](doc%2Fimages%2Fcartridge-retro.png)

You can plug in a cartridge into cgv19 and it will run it. But in opposite to
the old game catridges you can plug several cartridges into cgv19 to combine
them and use them together. 

![cartridge-model.png](doc%2Fimages%2Fcartridge-model.png)

cgv19 will call each cartridge with a copy of each loaded models. So
modifcations to the model from one cartridge are not visible in the 
second cartridge. A cartridge runs independently of all other plugged 
cartridges.

Cartridges interprets a model with its model elements and handle them
according to some rules. Therefor they are providing:

## Transformations
![transformation-model.png](doc%2Fimages%2Ftransformation-model.png)

Transformations take a model element and will modify them ore create new 
model elements. Transformations will run on the input model __M__ and all together
they will modify __M__ to a model new model enhanced __M'__.

Transformations are the key factor to abstraction. They are the most powerfull
tool in a code generator.

## CodeGenerators
![controller-model.png](doc%2Fimages%2Fcontroller-model.png)

CodeGenerators do the real work. They receive the elements of the __M'__ model
and will generate the _artifacts_.

You will have many of this code generators in a cartridge. Each type of artifact
will need its own generator. There could be many generators operating on the
same model element. Which generator is mapped to a model element is a decision 
of the cartridge itself.

cgv19 provides some helper classes to generate java, php or type script code.
