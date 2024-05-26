import de.spraener.nxtgen.ProtectionStrategieDefaultImpl
import de.spraener.nxtgen.cartridge.rest.RESTStereotypes
import de.spraener.nxtgen.oom.cartridge.GeneratorGapTransformation
import de.spraener.nxtgen.oom.model.MAttribute
import de.spraener.nxtgen.oom.model.MClass
import de.spraener.nxtgen.oom.model.OOModel
import de.spraener.nxtgen.symfony.php.PhpCodeHelper

MClass mClass = this.getProperty("modelElement");
MClass orgClass = GeneratorGapTransformation.getOriginalClass(mClass);
OOModel model = mClass.getModel();


"""<?php
//${ProtectionStrategieDefaultImpl.GENERATED_LINE}
namespace ${PhpCodeHelper.toNameSpace(mClass)};

use Symfony\\Component\\Form\\AbstractType;
use Symfony\\Component\\Form\\Extension\\Core\\Type\\DateType;
use Symfony\\Component\\Form\\Extension\\Core\\Type\\SubmitType;
use Symfony\\Component\\Form\\Extension\\Core\\Type\\TextType;
use Symfony\\Component\\Form\\Extension\\Core\\Type\\IntegerType;
use Symfony\\Component\\Form\\FormBuilderInterface;

class ${mClass.getName()} extends AbstractType {

    protected function internBuildForm(FormBuilderInterface \$builder, array \$options): FormBuilderInterface {
        \$builder
${addFields(orgClass)}
            ->add('save', SubmitType::class)
        ;
        return \$builder;
    }

    protected function toFormType(\$fieldName, \$defaultType) {
        return \$defaultType;
    }
}
"""

String addFields(MClass mClass) {
    StringBuilder sb = new StringBuilder();
    for(MAttribute a : mClass.attributes ) {
        if( isPersistentKey(a) ) {
            continue;
        }
        String name = a.getName();
        String type = PhpCodeHelper.toFormTypa(a);
        String label = PhpCodeHelper.readLabel(a)
        sb.append( "            ->add('$name', \$this->toFormType('$name', $type), ['label'=>'$label'])\n")
    }
    return sb.toString();
}

boolean isPersistentKey(MAttribute mAttribute) {
    return "true".equals(mAttribute.getTaggedValue(RESTStereotypes.DBFIELD.name,"isKey"));
}
