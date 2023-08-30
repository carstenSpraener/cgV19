package controller

import de.spraener.nxtgen.ProtectionStrategie
import de.spraener.nxtgen.cartridge.rest.RESTStereotypes
import de.spraener.nxtgen.cartridge.rest.cntrl.ApiControllerComponent

def getDataType(me) {
    def fqName= ApiControllerComponent.getDataType(me)
    if( fqName == null ) {
        return me.name;
    }
    return fqName.substring(fqName.lastIndexOf('.')+1);
}

def eName = getDataType(modelElement);
def route = "${eName.toLowerCase()}s";

return """<?php
//${ProtectionStrategie.GENERATED_LINE}

namespace App\\Controller;

use App\\Controller\\Service\\${eName}CntrlService;
use App\\Entity\\${eName};
use Psr\\Log\\LoggerInterface;
use Symfony\\Bundle\\FrameworkBundle\\Controller\\AbstractController;
use Symfony\\Component\\HttpFoundation\\JsonResponse;
use Symfony\\Component\\HttpFoundation\\Request;
use Symfony\\Component\\HttpFoundation\\Response;
use Symfony\\Component\\Routing\\Annotation\\Route;

#[Route('/api/${route}')]
class ${eName}Controller extends AbstractController {

    public function __construct(
        private ${eName}CntrlService \$service,
        private LoggerInterface \$logger
    ) {}

    #[Route('/ping', name: 'api_projects_ping', methods: ['GET'])]
    public function ping(): Response {
        \$this->logger->info('Received a Ping!');
        \$resp = new Response();
        \$resp->headers->add(array('Content-Type' => 'text/plain'));
        \$resp->setContent("pong");
        \$resp->setStatusCode(Response::HTTP_OK);
        return \$resp;
    }

    #[Route('/meta-inf', name: 'api_projects_meta_inf', methods: ['GET'])]
    public function getMetaInf(): JsonResponse {
        return new JsonResponse(${eName}::META_INF());
    }

    #[Route('', name: 'api_projects_new', methods: ['POST'])]
    public function add(Request \$request): JsonResponse {
        \$data = json_decode(\$request->getContent(), true);

        \$obj = ${eName}::fromArray(\$data);
        \$entity = \$this->service->save(\$obj);

        return new JsonResponse([
            'status' => '${eName} created!',
            'id' => \$entity->getId()
        ], Response::HTTP_CREATED);
    }

    #[Route('', name: 'api_projects_index', methods: ['GET'])]
    public function getAll(): JsonResponse {
        \$entityList = \$this->service->getAll();

        return \$this->service->listToJsonResponse(\$entityList);
    }

    #[Route('/find', name: 'api_projects_find_by', methods: ['GET'])]
    public function findByQueryParam(Request \$request): JsonResponse {
        \$entityList = \$this->service->findByRequestQuery(\$request);
        if( count(\$entityList) == 0 ) {
            return new Response(Response::HTTP_NO_CONTENT);
        }

        return \$this->service->listToJsonResponse(\$entityList);
    }

    #[Route('/{id}', name: 'api_projects_get', methods: ['GET'])]
    public function getById(${eName} \$prj): JsonResponse {
        return new JsonResponse(\$prj->jsonSerialize(), Response::HTTP_OK, [], true);
    }

    #[Route('/{id}', name: 'api_projects_update', methods: ['PUT'])]
    public function update(${eName} \$entity, Request \$request): JsonResponse {
        if( \$entity==null ) {
            return new JsonResponse(null, Response::HTTP_NOT_FOUND);
        }
        \$update${eName} = \$this->service->update(\$entity);

        return new JsonResponse(\$update${eName}->jsonSerialize(), Response::HTTP_OK, [], true);
    }

    #[Route('/{id}', name: 'api_projects_delete', methods: ['DELETE'])]
    public function delete(${eName} \$entity): Response {
        if( \$entity ) {
            \$this->service->delete(\$entity);
        }
        return new Response(Response::HTTP_NO_CONTENT);
    }

}
"""
