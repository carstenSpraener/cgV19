import de.spraener.nxtgen.ProtectionStrategieDefaultImpl
import de.spraener.nxtgen.laravel.CreateResourceInfrastructure
import de.spraener.nxtgen.laravel.LaravelHelper
import de.spraener.nxtgen.laravel.LaravelStereotypes
import de.spraener.nxtgen.oom.model.MClass
import de.spraener.nxtgen.oom.model.OOModel

MClass mClass = this.getProperty("modelElement");
OOModel model = mClass.getModel();
MClass rsrc = CreateResourceInfrastructure.getResource(mClass);

String generateActions(MClass mc) {
    String actionDef = mc.getTaggedValue(LaravelStereotypes.FILAMENTPAGE.getName(), "headerActions")
    if( actionDef==null) {
        return "";
    }
    String actions = "";
    for( String action : actionDef.split(",")) {
        if( !"".equals(actions) ) {
            actions+=",\n            ";
        }
        actions += action
    }
    return """
    protected function getHeaderActions(): array
    {
        return [
${actions}
        ];
    }
"""
}

String namespace = LaravelHelper.toNameSpace(mClass);
String baseResourcePage = mClass.getTaggedValue(LaravelStereotypes.FILAMENTPAGE.getName(), "baseResourcePage")
String baseResourcePageClass = baseResourcePage.substring(baseResourcePage.lastIndexOf('\\')+1);

"""<?php
//${ProtectionStrategieDefaultImpl.GENERATED_LINE}
namespace ${namespace};

use ${LaravelHelper.toNameSpace(rsrc)}\\${rsrc.getName()};
use Filament\\Actions;
use ${baseResourcePage};

class ${mClass.getName()} extends ${baseResourcePageClass}
{
    protected static string \$resource = ${rsrc.getName()}::class;
${generateActions(mClass)}
}

"""
