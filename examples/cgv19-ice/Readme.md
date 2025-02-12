### Imagine...

Imagine a gaming console like an ATARI 7800 would be able to use multiple cartridges. Not only one. And you can use them together to make your game more exciting... Like Combining Pac-Man with Galaga and out of the sudden Pac-Man gets help from a spaceship. What would that sound like?  

![InterCartridgeEvaluation.png](doc/images/InterCartridgeEvaluation.png)

# Welcome to Inter Cartridge Evaluation (ICE)

In some environments it makes sense to create a frame from a parent cartridge like a cloud application cartridge. This cartridge needs to generate a docker-compose.yml where all subprojects need to be listed. 

It would be very nice if the cloud cartridge don't need to know anything about its subprojects and how to generate the service block of each. It would be much better if the subproject knows about it. A angular frontend may need a different service block than a api service or a database service.

With ICE the root cartridge (like pac-man) calls the sub cartridges (like galaga) to help with the generation of the overall docker-compose.yml. It does it by calling ```NextGen.evaluate()```.

It can also start generating the subprojects by scheduling cgV19-runs on each included subproject with ```NextGen.scheduleInvocation()```.

## The ICE Example
