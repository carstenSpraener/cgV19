import de.spraener.nxtgen.ProtectionStrategieDefaultImpl
import de.spraener.nxtgen.laravel.LaravelHelper
import de.spraener.nxtgen.oom.cartridge.GeneratorGapTransformation
import de.spraener.nxtgen.oom.model.MAssociation
import de.spraener.nxtgen.oom.model.MAttribute
import de.spraener.nxtgen.oom.model.MClass
import de.spraener.nxtgen.oom.model.OOModel

MClass mClass = this.getProperty("modelElement");
MClass orgClass = GeneratorGapTransformation.getOriginalClass(mClass);


String generateFillables(MClass mc) {
    StringBuilder sb = new StringBuilder();
    sb.append("    protected \$fillable = [\n");
    for(MAttribute a : mc.getAttributes() ) {
        sb.append("        '${a.getName()}',\n")
    }
    for(MAssociation a : mc.getAssociations() ) {
        if(a.getAssociationType().endsWith("ToOne")) {
            String idName = LaravelHelper.Associations.toForeignIdName(a);
            sb.append("        '${idName}',\n")
        }
    }
    sb.append("    ];")
    return sb.toString();
}

String generateCasts( MClass mc) {
    StringBuilder sb = new StringBuilder();
    for( MAttribute a : mc.getAttributes() ) {
        String cast = LaravelHelper.Attributes.getCast(a);
        if( cast != null ) {
            sb.append( "        '${a.getName()}' => '${cast}',\n")
        }
    }
    if( sb.isEmpty() ) {
        return "";
    }
    return "    protected \$casts = [\n"+sb.toString()+"    ];";
}

String generateRelations(MClass mc) {
    StringBuilder sb = new StringBuilder();

    for( MAssociation a : mc.getAssociations() ) {
        MClass target = ((OOModel)a.getModel()).findClassByName(a.getType());
        if( a.getAssociationType().endsWith("ToOne")) {
            sb.append(
"""    public function ${a.getName()}() {
        return \$this->belongsTo(\\${LaravelHelper.toNameSpace(target)}\\${target.getName()}::class);
    }
"""
            );
        }
        if( a.getAssociationType().endsWith("ToMany")) {
            sb.append(
                    """    public function ${a.getName()}() {
        return \$this->hasMany(\\${LaravelHelper.toNameSpace(target)}\\${target.getName()}::class);
    }
"""
            );
        }
    }
    return sb.toString();
}
"""<?php
//${ProtectionStrategieDefaultImpl.GENERATED_LINE}
namespace ${LaravelHelper.toNameSpace(mClass)};

use Illuminate\\Database\\Eloquent\\Factories\\HasFactory;
use Illuminate\\Database\\Eloquent\\Model;

class ${mClass.getName()} extends Model
{
    use HasFactory;
${generateFillables(orgClass)}
${generateCasts(orgClass)}
${generateRelations(orgClass)}
}
"""
