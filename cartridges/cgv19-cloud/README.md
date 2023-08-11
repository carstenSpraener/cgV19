# The cgv19-cloud Cartridge (_Incubator State_)

![cloud-deployment-model.png](..%2Fdoc%2Fimages%2Fcloud-deployment-model.png)

## Generates:

* ClusterIP-Services
* Ingress-Services
* Deployments
* TODO: PersistenceVolumaClaims
* Dockefile
* docker-compose.yml
* bash-Script to upload docker images

The model `cgv19-cloud/src/test/resources/de.spraener.tinyapp.oom` compiles
and starts in a k8s cluster.

### It contains 3 modules:
* A SpringBoot application under `/api`
* A Angular frontend under '/'
* A JavaLin worker with 4 replicas.
* TODO: A mariadb database with PMC and init sql script
* TODO: A KeyCloak instance with initial realm
* TODO: A mariadb for the KeyCloak instance with pmc

### Planed for later use:

* Extracting PHP from the REST-Cartridge
* A Symfony Application Module
* A NestJS Application Module
* An Express/React Application Module
