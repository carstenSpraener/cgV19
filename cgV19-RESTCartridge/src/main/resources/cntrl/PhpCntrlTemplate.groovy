import de.spraener.nxtgen.ProtectionStrategie
import de.spraener.nxtgen.cartridge.rest.RESTStereotypes

def getDataType(me) {
    def fqName= me.getTaggedValue(RESTStereotypes.CONTROLLER.name, "dataType");
    return fqName.substring(fqName.lastIndexOf('.')+1);
}

def eName = getDataType(modelElement);
def route = "${eName.toLowerCase()}s";

return """<?php
//${ProtectionStrategie.GENERATED_LINE}

namespace App\\Controller;

use App\\Controller\\Base\\${eName}ControllerBase;
use App\\Repository\\${eName}Repository;

class ${eName}Controller extends ${eName}ControllerBase {
    private \$classRepository;

    public function __construct(${eName}Repository \$classRepository) {
        parent::__construct(\$classRepository);
    }
}
"""
