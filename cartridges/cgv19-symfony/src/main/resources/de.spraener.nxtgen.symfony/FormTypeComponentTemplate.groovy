import de.spraener.nxtgen.ProtectionStrategieDefaultImpl
import de.spraener.nxtgen.oom.model.MClass
import de.spraener.nxtgen.oom.model.OOModel
import de.spraener.nxtgen.symfony.php.PhpCodeHelper

MClass mClass = this.getProperty("modelElement");
OOModel model = mClass.getModel();

"""<?php
//${ProtectionStrategieDefaultImpl.GENERATED_LINE}
namespace ${PhpCodeHelper.toNameSpace(mClass)};

use Symfony\\Component\\Form\\FormBuilderInterface;
use ${PhpCodeHelper.toNameSpace(mClass)}\\Base\\${mClass.name}Base;

class ${mClass.getName()} extends ${mClass.getName()}Base {


    public function buildForm(FormBuilderInterface \$builder, array \$options): void {
        \$this->internBuildForm(\$builder, \$options);
    }

    protected function toFormType(\$fieldName, \$defaultType) {
        return parent::toFormType(\$fieldName, \$defaultType); 
    }

}
"""
