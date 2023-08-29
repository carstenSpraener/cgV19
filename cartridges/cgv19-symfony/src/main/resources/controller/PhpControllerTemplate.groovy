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

use App\\Controller\\Base\\${eName}ControllerBase;
use App\\Repository\\${eName}Repository;
use Doctrine\\ORM\\EntityManagerInterface;

class ${eName}Controller extends ${eName}ControllerBase {
    private \$classRepository;

    public function __construct(${eName}Repository \$classRepository, EntityManagerInterface \$manager ) {
        parent::__construct(\$classRepository, \$manager);
    }
}
"""
