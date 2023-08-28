package controller

import de.spraener.nxtgen.ProtectionStrategie
import de.spraener.nxtgen.cartridge.rest.RESTStereotypes
import de.spraener.nxtgen.cartridge.rest.cntrl.ApiControllerComponent
import de.spraener.nxtgen.oom.cartridge.GeneratorGapTransformation

def getDataType(me) {
    def fqName= ApiControllerComponent.getDataType(me)
    if( fqName ==null ) {
        fqName = me.name;
    }
    return fqName.substring(fqName.lastIndexOf('.')+1);
}

def orgClass = GeneratorGapTransformation.getOriginalClass(modelElement)
def eName = getDataType(orgClass);
def route = "/api/${eName.toLowerCase()}s";
def routeNamePrefix = "api_${eName.toLowerCase()}s_"

return """<?php
//${ProtectionStrategie.GENERATED_LINE}}

namespace App\\Controller\\Base;


use App\\Entity\\${eName};
use App\\Repository\\${eName}Repository;
use Symfony\\Bundle\\FrameworkBundle\\Controller\\AbstractController;
use Symfony\\Component\\HttpFoundation\\JsonResponse;
use Symfony\\Component\\HttpFoundation\\Request;
use Symfony\\Component\\HttpFoundation\\Response;
use Symfony\\Component\\HttpKernel\\Exception\\NotFoundHttpException;
use Symfony\\Component\\Routing\\Annotation\\Route;

class ${modelElement.getName()} extends AbstractController {

    public function __construct(private ${eName}Repository \$repository) {
    }

    #[Route('${route}/ping', name: '${routeNamePrefix}ping', methods: ['GET'])]
    public function ping(): Response {
        \$resp = new Response();
        \$resp->headers->add(array('Content-Type'=>'text/plain'));
        \$resp->setContent("pong");
        \$resp->setStatusCode(Response::HTTP_OK);
        return \$resp;
    }

    #[Route('${route}/meta-inf', name: '${routeNamePrefix}meta_inf', methods: ['GET'])]
    public function getMetaInf(): JsonResponse {
        return new JsonResponse(${eName}::META_INF());
    }

    #[Route('${route}', name: '${routeNamePrefix}new', methods: ['POST'])]
    public function add(Request \$request): JsonResponse {
        \$data = json_decode(\$request->getContent(), true);

        \$obj = ${eName}::fromArray(\$data);
        \$entity = \$this->save${eName}(\$obj);

        return new JsonResponse([
                'status' => '${eName} created!',
                'id' => \$entity->getId()
        ], Response::HTTP_CREATED);
    }


    #[Route('${route}', name: '${routeNamePrefix}index', methods: ['GET'])]
    public function getAll(): JsonResponse {
        \$entityList = \$this->repository->findAll();

        return \$this->listToJsonResponse(\$entityList);
    }

    #[Route('${route}/{id}', name: '${routeNamePrefix}get', methods: ['GET'])]
    public function getById(\$id): JsonResponse {
        \$entity = \$this->repository->findOneBy(['id' => \$id]);
        return new JsonResponse(\$entity->jsonSerialize(), Response::HTTP_OK, [], true);
    }

    #[Route('${route}_find', name: '${routeNamePrefix}find_by', methods: ['GET'])]
    public function findByPropertyValue(Request \$request): JsonResponse {
        \$property = \$request->query->get('property');
        \$value = \$request->query->get('value');
        \$entityList = \$this->repository->findBy([\$property => \$value]);

        return \$this->listToJsonResponse(\$entityList);
    }

    #[Route('${route}_like', name: '${routeNamePrefix}find_like', methods: ['GET'])]
    public function findLikePropertyValue(Request \$request): JsonResponse {
        \$property = \$request->query->get('property');
        \$value = \$request->query->get('value');
        \$entityList = \$this->repository->findLike(\$property,\$value);

        return \$this->listToJsonResponse(\$entityList);
    }

    #[Route('${route}/{id}', name: '${routeNamePrefix}update', methods: ['PUT'])]
    public function update(\$id, Request \$request): JsonResponse {
        \$entity = \$this->repository->findOneBy(['id' => \$id]);
        if( \$entity==null ) {
            return new JsonResponse(null, Response::HTTP_NOT_FOUND);
        }
        \$data = json_decode(\$request->getContent(), true);
        \$entity->updateFromArray(\$data);
        \$update${eName} = \$this->update${eName}(\$entity);

        return new JsonResponse(\$update${eName}->jsonSerialize(), Response::HTTP_OK, [], true);
    }

    #[Route('${route}/{id}', name: '${routeNamePrefix}delete', methods: ['DELETE'])]
    public function delete(\$id): Response {
        \$entity = \$this->repository->findOneBy(['id' => \$id]);

        \$this->remove${eName}(\$entity);

        return new Response(Response::HTTP_NO_CONTENT);
    }

    function listToJsonResponse(\$entityList): JsonResponse {
        if( count(\$entityList)===0 ) {
            return new JsonResponse("{}", Response::HTTP_NO_CONTENT, [], true);
        }

        \$dataList = [];
        foreach( \$entityList as \$entity) {
            \$dataList[] = \$entity->jsonSerialize();
        }

        return new JsonResponse('[' . implode(',', \$dataList) . ']'
            , Response::HTTP_OK, [], true
        );
    }

    public function save${eName}(${eName} \$entity): ${eName} {
        \$newEntity = \$this->manager->persist(\$entity);
        \$this->manager->flush();

        return \$newEntity;
    }

    public function update${eName}(${eName} \$entity): ${eName} {
        \$this->manager->persist(\$entity);
        \$this->manager->flush();

        return \$entity;
    }
}
"""
