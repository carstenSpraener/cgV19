import de.spraener.nxtgen.ProtectionStrategieDefaultImpl
import de.spraener.nxtgen.laravel.LaravelHelper
import de.spraener.nxtgen.oom.cartridge.GeneratorGapTransformation
import de.spraener.nxtgen.oom.model.MClass
import de.spraener.nxtgen.oom.model.OOModel

MClass mClass = this.getProperty("modelElement");
OOModel model = mClass.getModel();
MClass orgClass = GeneratorGapTransformation.getOriginalClass(mClass);

String factory = mClass.getName();
String namespacedModel = LaravelHelper.toNameSpace(orgClass)+"\\"+orgClass.getName()
String factoryNS = LaravelHelper.toNameSpace(mClass).substring(4);
// Cut the "Models" from prefix
factoryNS = factoryNS.substring(factoryNS.indexOf('\\')+1);

"""<?php
//${ProtectionStrategieDefaultImpl.GENERATED_LINE}

namespace Database\\Factories\\${factoryNS};

use Illuminate\\Database\\Eloquent\\Factories\\Factory;

/**
 * @extends \\Illuminate\\Database\\Eloquent\\Factories\\Factory<\\${namespacedModel}>
 */
class ${factory} extends Factory
{
    /**
     * Define the model's default state.
     *
     * @return array<string, mixed>
     */
    public function definition(): array
    {
        return [
            //
        ];
    }
}
"""
