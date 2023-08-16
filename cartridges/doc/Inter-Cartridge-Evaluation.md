# Inter cartridge evaluation

# _Incubator s
tate_

## The idea of _ICE_
![InterCartridgeEvaluation.png](images%2FInterCartridgeEvaluation.png)

Imagine you can play _PackMan_ on an old ATARI 7800 and poke in another
cartridge like _Galaga_, wire them up and suddenly _PacMan_ gets help from a _Galaga_
spaceship.

With _Inter Cartridge Evaluation_ a cartridge _A_ can make use of another
cartridge _B_ to fulfill parts of the task.

## Schedule sub runs of cgv19

```java
NextGen.schedule(NextGenInvocation invocation);
```

The Cloud cartridge uses this method to generate the content of
the contained << _CloudModule_ >> sub packages. Each << _CloudModule_ >>
can use its own cartridge.

In the __tiny-webapp__ is a << _CloudModule_ >> for a SpringBoot-Application,
a JavaLin-Application and a Angular-Application. Each with its own
submodel and own cartridge in use.
![cgv19-cloud.png](images%2Fcgv19-cloud.png)

## Execute generators of cartridge _B_ in the run of cartridge _A_

```bash
NextGen.evaluate(String cartridgeName, Model m, ModelElement e, Stereotype sType, String aspect);
```
Looks for a cartridge with name _cartridgeName_ and calls the optional
interface method `evaluate` which returns a string.

The parameters are:

The name of the cartrige, the actual Model, 
the actual ModelElement, the stereotype in question and 
optional a narrowing aspect.

It was invented while generating a docker-compose.yaml for a 
cloud application. The cloud cartridge calls this method on the 
given cartridge of a << _CloudModule_ >> to let the module generate
the service definition. This way the cloud module does not need
to know how to create a service for all possible << _CloudModule_ >>
instances.

