# The cgv19-cloud Cartridge (_Incubator State_)

![cloud-deployment-model.png](..%2Fdoc%2Fimages%2Fcloud-deployment-model.png)

## Generates:

* ClusterIP-Services
* Ingress-Services
* Deployments
* TODO: PersistenceVolumaClaims
* Dockerfiles (via _ICE_)
* docker-compose.yml with use of _ICE_
* bash-Script to upload docker images

The model `cgv19-cloud/src/test/resources/de.spraener.tinyapp.oom` compiles
and starts in a k8s cluster.

### It contains 3 modules:
* A SpringBoot application under `/api`
* A Angular frontend under `/`
* A JavaLin worker with 4 replicas.
* TODO: A mariadb/postgresql database with PVC and init sql script
* TODO: A KeyCloak instance with initial realm under `/kc`
* TODO: A mariadb/postgresql database for the KeyCloak instance with PVC
* TODO: A Jenkinsfile for CI/CD

### Planed for later releases if needed:

* Extracting PHP from the REST-Cartridge
* Extracting JPA from the REST-Cartridge
* A Symfony Application Module
* A NestJS Application Module
* An Express/React Application Module
