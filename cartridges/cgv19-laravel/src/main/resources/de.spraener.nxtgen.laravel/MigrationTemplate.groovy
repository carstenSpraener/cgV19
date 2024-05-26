import de.spraener.nxtgen.ProtectionStrategieDefaultImpl
import de.spraener.nxtgen.laravel.LaravelHelper
import de.spraener.nxtgen.oom.cartridge.GeneratorGapTransformation
import de.spraener.nxtgen.oom.model.MAssociation
import de.spraener.nxtgen.oom.model.MAttribute
import de.spraener.nxtgen.oom.model.MClass
import de.spraener.nxtgen.oom.model.OOModel

MClass mClass = this.getProperty("modelElement");
OOModel model = mClass.getModel();
MClass orgClass = GeneratorGapTransformation.getOriginalClass(mClass)

String table = LaravelHelper.toTableName(orgClass);

String generateFieldDefintion(MAttribute a) {
    StringBuilder sb = new StringBuilder();
    sb.append("            \$table->${a.getType().toLowerCase()}('${a.getName().toLowerCase()}')\n")
    if(LaravelHelper.Attributes.isUnique(a)) {
        sb.append("                ->unique()\n")
    }
    if(LaravelHelper.Attributes.isNullable(a)) {
        sb.append("                ->nullable()\n")
    }
    sb.append("            ;\n");

    return sb.toString();
}

String generateFieldDefinitions(MClass mc) {
    StringBuilder sb = new StringBuilder();

    for(MAttribute a : mc.getAttributes() ) {
        sb.append(generateFieldDefintion(a));
    }
    return sb.toString();
}

String generateRelations(MClass mc) {
    StringBuilder sb = new StringBuilder();

    for(MAssociation a : mc.getAssociations() ) {
        if(a.getAssociationType().endsWith("ToOne")) {
            sb.append("""            \$table->foreignIdFor(${getFQTargetName(mc, a.getType())}::class)
                ->nullable(${isNullable(a.getMultiplicity())})
                ->constrained()
                ;
""")
        }
    }
    return sb.toString();
}

"""<?php
//${ProtectionStrategieDefaultImpl.GENERATED_LINE}

use Illuminate\\Database\\Migrations\\Migration;
use Illuminate\\Database\\Schema\\Blueprint;
use Illuminate\\Support\\Facades\\Schema;

class ${mClass.getName()} extends Migration
{
    /**
     * Run the migrations.
     */
    public function up(): void
    {
        Schema::create('${table}', function (Blueprint \$table) {
            \$table->id();
${generateFieldDefinitions(orgClass)}
${generateRelations(orgClass)}
            \$table->timestamps();
        });
    }

    /**
     * Reverse the migrations.
     */
    public function down(): void
    {
        Schema::dropIfExists('${table}');
    }
};
"""

String getFQTargetName(MClass mc, String type) {
    MClass target = ((OOModel)mc.getModel()).findClassByName(type);
    return LaravelHelper.toFQPhpName(target);
}

String isNullable(String multiplicity) {
    return multiplicity.startsWith("0");
}
