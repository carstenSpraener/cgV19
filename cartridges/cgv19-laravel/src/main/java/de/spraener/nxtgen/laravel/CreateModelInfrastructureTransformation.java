package de.spraener.nxtgen.laravel;

import de.spraener.nxtgen.oom.OOModelBuilder;
import de.spraener.nxtgen.oom.cartridge.GeneratorGapTransformation;
import de.spraener.nxtgen.oom.model.MClass;
import de.spraener.nxtgen.oom.model.MPackage;
import de.spraener.nxtgen.oom.model.OOModel;
import static de.spraener.nxtgen.laravel.LaravelHelper.*;
import static de.spraener.nxtgen.php.PhpHelper.*;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

public class CreateModelInfrastructureTransformation extends CreateModelInfrastructureTransformationBase {

    @Override
    public void doTransformationIntern(MClass entity) {
        OOModel model = (OOModel) entity.getModel();
        MPackage pkg = entity.getPackage();
        OOModelBuilder.createMClass(pkg, entity.getName(),
                c->OOModelBuilder.addStereotype(c, LaravelStereotypes.LARAVELMODEL.getName()),
                c->setOutputDirForModelElement(c, LaravelStereotypes.LARAVELMODEL.getName(),"app/" ),
                c->GeneratorGapTransformation.setOriginalClass(c, entity)
        );
        OOModelBuilder.createMClass(pkg, entity.getName()+"Factory",
                c->OOModelBuilder.addStereotype(c, LaravelStereotypes.LARAVELFACTORY.getName(),
                        "namespaceModel="+LaravelHelper.toNameSpace(entity)
                ),
                c->setOutputDirForModelElement(c, LaravelStereotypes.LARAVELFACTORY.getName(),"database/factories/" ),
                c->GeneratorGapTransformation.setOriginalClass(c, entity)
        );
        OOModelBuilder.createMClass(pkg, entity.getName()+"Migration",
                c->OOModelBuilder.addStereotype(c, LaravelStereotypes.LARAVELMIGRATION.getName()),
                c->setOutputDirForModelElement(c, LaravelStereotypes.LARAVELMIGRATION.getName(),"database/migrations/" ),
                c->GeneratorGapTransformation.setOriginalClass(c, entity)
        );
        OOModelBuilder.createMClass(pkg, entity.getName()+"Seeder",
                c->OOModelBuilder.addStereotype(c, LaravelStereotypes.LARAVELSEEDER.getName()),
                c->setOutputDirForModelElement(c, LaravelStereotypes.LARAVELSEEDER.getName(),"database/seeders/" ),
                c->GeneratorGapTransformation.setOriginalClass(c, entity)
        );
    }

    public static String toFactoryName(MClass entity) {
        return createPhpPath(entity) + entity.getName()+"Factory.php";
    }

    public static String toMigrationName(MClass entity) {
        return createPhpPath(entity) + getTimestampPrefix() + "_create_"+entity.getName().toLowerCase()+"_table.php";
    }

    public static String toSeederName(MClass entity) {
        return createPhpPath(entity) + entity.getName()+"Seeder.php";
    }

    public static String getTimestampPrefix() {
        DateFormat df = new SimpleDateFormat("yyyy_MM_dd_HHmmss");
        return df.format(new Date());
    }

}
