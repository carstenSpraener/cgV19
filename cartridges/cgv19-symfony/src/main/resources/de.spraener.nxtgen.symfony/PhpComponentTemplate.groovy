import de.spraener.nxtgen.ProtectionStrategieDefaultImpl
import de.spraener.nxtgen.oom.cartridge.GeneratorGapTransformation
import de.spraener.nxtgen.oom.cartridge.JavaHelper
import de.spraener.nxtgen.oom.model.MAttribute
import de.spraener.nxtgen.oom.model.MClass
import de.spraener.nxtgen.oom.model.OOModel
import de.spraener.nxtgen.symfony.php.PhpCodeHelper

MClass mClass = this.getProperty("modelElement");
OOModel model = mClass.getModel();
MClass orgClass = GeneratorGapTransformation.getOriginalClass(mClass);

def entityName = PhpCodeHelper.toNameSpace(orgClass)+"\\"+orgClass.getName();

"""<?php
//${ProtectionStrategieDefaultImpl.GENERATED_LINE}

namespace ${PhpCodeHelper.toNameSpace(mClass)};
use $entityName;
use Symfony\\UX\\TwigComponent\\Attribute\\AsTwigComponent;

#[AsTwigComponent]
class ${mClass.getName()} {
${listAttributes(mClass)}

    public function __construct(${orgClass.name} \$entity) {
${copyAttributes(mClass)}
    }
}
"""
StringBuilder listAttributes(MClass mc) {
    StringBuilder sb = new StringBuilder();
    for (MAttribute a : mc.attributes) {
        sb.append("    public \$${a.getName();};\n")
    }
    return sb;
}

String copyAttributes(MClass mc) {
    StringBuilder sb = new StringBuilder();

    for (MAttribute a : mc.attributes) {
        sb.append("         \$this->${a.getName()} = \$entity->get${JavaHelper.firstToUpperCase(a.getName())}();\n")
    }
    return sb;
}
