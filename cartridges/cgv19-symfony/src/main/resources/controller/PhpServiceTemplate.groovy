import de.spraener.nxtgen.ProtectionStrategieDefaultImpl
import de.spraener.nxtgen.cartridge.rest.cntrl.ApiControllerComponent
import de.spraener.nxtgen.oom.cartridge.GeneratorGapTransformation
import de.spraener.nxtgen.oom.model.MClass
import de.spraener.nxtgen.oom.model.OOModel

MClass mClass = this.getProperty("modelElement");
OOModel model = mClass.getModel();
def orgClass = GeneratorGapTransformation.getOriginalClass(modelElement)
def eName = getDataType(orgClass);

def getDataType(me) {
    def fqName= ApiControllerComponent.getDataType(me)
    if( fqName == null ) {
        return me.name;
    }
    return fqName.substring(fqName.lastIndexOf('.')+1);
}

"""<?php
//THIS FILE IS GENERATED AS LONG AS THIS LINE EXISTS

namespace App\\Controller\\Service;

use App\\Entity\\${eName};
use App\\Repository\\${eName}Repository;
use Doctrine\\Bundle\\DoctrineBundle\\Repository\\ServiceEntityRepository;
use Doctrine\\ORM\\EntityManagerInterface;
use Psr\\Log\\LoggerInterface;

class ${mClass.getName()} {
    use ControllerServiceTrait;

    public function __construct(
        private ${eName}Repository \$repository,
        private EntityManagerInterface \$manager,
        private LoggerInterface \$logger
    ) {}

    public function getRepository(): ServiceEntityRepository {
        return \$this->repository;
    }

    public function delete(${eName} \$entity): void {
        \$this->manager->remove(\$entity);
        \$this->manager->flush();
    }

    public function save(${eName} \$entity) {
        \$this->manager->persist(\$entity);
        \$this->manager->flush();
    }

    public function update(${eName} \$entity): ${eName} {
        \$this->manager->persist(\$entity);
        \$this->manager->flush();

        return \$entity;
    }
}
"""
