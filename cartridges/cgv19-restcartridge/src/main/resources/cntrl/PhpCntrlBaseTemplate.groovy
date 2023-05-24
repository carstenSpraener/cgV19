import de.spraener.nxtgen.ProtectionStrategie
import de.spraener.nxtgen.cartridge.rest.RESTStereotypes

def getDataType(me) {
    def fqName= me.getTaggedValue(RESTStereotypes.RESTCONTROLLER.name, "dataType");
    if( fqName ==null ) {
        fqName = me.name;
    }
    return fqName.substring(fqName.lastIndexOf('.')+1);
}

def eName = getDataType(modelElement);
def route = "${eName.toLowerCase()}s";

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

class ${eName}ControllerBase extends AbstractController {
    private \$classRepository;

    public function __construct(${eName}Repository \$classRepository) {
        \$this->classRepository = \$classRepository;
    }

    /**
     * @Route("/${route}/ping", name="ping", methods={"GET"})
     */
    public function ping(): Response {
        \$resp = new Response();
        \$resp->headers->add(array('Content-Type'=>'text/plain'));
        \$resp->setContent("pong");
        \$resp->setStatusCode(Response::HTTP_OK);
        return \$resp;
    }

    /**
     * @Route("/${route}/meta-inf", name="meta_inf", methods={"GET"})
     *
     */
    public function getMetaInf(): JsonResponse {
        return new JsonResponse(${eName}::META_INF());
    }
    /**
     * @Route("/${route}", name="add_${eName}", methods={"POST"})
     */
    public function add(Request \$request): JsonResponse {
        \$data = json_decode(\$request->getContent(), true);

        \$obj = ${eName}::fromArray(\$data);
        \$entity = \$this->classRepository->save${eName}(\$obj);

        return new JsonResponse([
                'status' => '${eName} created!',
                'id' => \$entity->getId()
        ], Response::HTTP_CREATED);
    }


    /**
     * @Route("/${route}", name="get_all_${eName}s", methods={"GET"})
     */
    public function getAll(): JsonResponse
    {
        \$${route} = \$this->classRepository->findAll();
        \$data = [];

        foreach (\$${route} as \$entity) {
            \$data[] = \$entity->toArray();
        }

        return new JsonResponse(\$data, Response::HTTP_OK);
    }

    /**
     * @Route("/${route}/{id}", name="get_one_${eName}", methods={"GET"})
     */
    public function getById(\$id): JsonResponse {
        \$entity = \$this->classRepository->findOneBy(['id' => \$id]);

        \$data = \$entity->toArray();

        return new JsonResponse(\$data, Response::HTTP_OK);
    }

    /**
     * @Route("/${route}_find", name="find_${eName}_equals", methods={"GET"})
     */
    public function getFindByPropertyValue(Request \$request): JsonResponse {
        \$property = \$request->query->get('property');
        \$value = \$request->query->get('value');
        \$entityList = \$this->classRepository->findBy([\$property => \$value]);

        return \$this->listToJsonResponse(\$entityList);
    }

    /**
     * @Route("/${route}_like", name="find_${eName}_like", methods={"GET"})
     */
    public function getFindLikePropertyValue(Request \$request): JsonResponse {
        \$property = \$request->query->get('property');
        \$value = \$request->query->get('value');
        \$entityList = \$this->classRepository->findLike(\$property,\$value);

        return \$this->listToJsonResponse(\$entityList);
    }

    function listToJsonResponse(\$entityList): JsonResponse {
        \$dataList = [];
        foreach( \$entityList as \$entity) {
            \$data = \$entity->toArray();
            array_push(\$dataList, \$data);
        }

        return new JsonResponse(\$dataList, Response::HTTP_OK);
    }

    /**
     * @Route("/${route}/{id}", name="update_${eName}", methods={"PUT"})
     */
    public function update(\$id, Request \$request): JsonResponse
    {
        \$entity = \$this->classRepository->findOneBy(['id' => \$id]);
        if( \$entity==null ) {
            return new JsonResponse(null, Response::HTTP_NOT_FOUND);
        }
        \$data = json_decode(\$request->getContent(), true);
        \$entity->updateFromArray(\$data);
        \$update${eName} = \$this->classRepository->update${eName}(\$entity);

        return new JsonResponse(\$update${eName}->toArray(), Response::HTTP_OK);
    }


    /**
     * @Route("/${route}/{id}", name="delete_${eName}", methods={"DELETE"})
     */
    public function delete(\$id): JsonResponse
    {
        \$entity = \$this->classRepository->findOneBy(['id' => \$id]);

        \$this->classRepository->remove${eName}(\$entity);

        return new JsonResponse(['status' => 'Entity deleted'], Response::HTTP_NO_CONTENT);
    }

}
"""
