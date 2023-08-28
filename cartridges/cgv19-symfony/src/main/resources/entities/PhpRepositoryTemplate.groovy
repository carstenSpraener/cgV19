package entites

import de.spraener.nxtgen.cartridge.rest.RESTStereotypes
import de.spraener.nxtgen.ProtectionStrategie
import de.spraener.nxtgen.oom.model.MClass
import de.spraener.nxtgen.php.PhpHelper

def pkgName = "App\\Repository";
def cName = modelElement.name;
def dataType = PhpHelper.toClassName(modelElement.getTaggedValue(RESTStereotypes.REPOSITORY.name, 'dataType'));

def generateSetterList() {
    MClass mc = (MClass) modelElement;
    attrList = "${PhpHelper.toClassName(modelElement.getTaggedValue(RESTStereotypes.REPOSITORY.name, 'attrList'))}";
    String[] attrs = attrList.split(";");
    String result = "";
    for (String attr : attrs) {
        def attrSetterName = "${Character.toUpperCase(attr.charAt(0))}${attr.substring(1)}";
        result += "\n            ->set${attrSetterName}(\$${attr})";
    }
    return result;
}

def phpAttrList() {
    MClass mc = (MClass) modelElement;
    attrList = "${PhpHelper.toClassName(modelElement.getTaggedValue(RESTStereotypes.REPOSITORY.name, 'attrList'))}";
    String[] attrs = attrList.split(";");
    String result = "";
    for (String attrName : attrs) {
        if( !"".equals(result) ) {
            result += ", ";
        }
        result += "\$${attrName}";
    }
    return result;
}

return """<?php

namespace ${pkgName};
// ${ProtectionStrategie.GENERATED_LINE}

use App\\Entity\\${dataType};
use Doctrine\\Bundle\\DoctrineBundle\\Repository\\ServiceEntityRepository;
use Doctrine\\ORM\\EntityManager;
use Doctrine\\ORM\\EntityManagerInterface;
use Doctrine\\Persistence\\ManagerRegistry;

/**
 * @extends ServiceEntityRepository<${dataType}>
 *
 * @method ${dataType}|null find(\$id, \$lockMode = null, \$lockVersion = null)
 * @method ${dataType}|null findOneBy(array \$criteria, array \$orderBy = null)
 * @method ${dataType}[]    findAll()
 * @method ${dataType}[]    findBy(array \$criteria, array \$orderBy = null, \$limit = null, \$offset = null)
 */

class ${cName} extends ServiceEntityRepository {
    private \$manager;

    public function __construct(ManagerRegistry \$registry, EntityManagerInterface \$manager) {
        parent::__construct(\$registry, ${dataType}::class);
        \$this->manager = \$manager;
    }

    public function findLike(\$property, \$value): array {
        return \$this->createQueryBuilder('e')
            ->andWhere('e.' . \$property . ' like :\$property')
            ->setParameter('\$property', \$value . '%')
            ->getQuery()
            ->execute();
    }
}
"""
