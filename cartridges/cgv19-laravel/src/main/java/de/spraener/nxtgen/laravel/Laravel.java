package de.spraener.nxtgen.laravel;

import de.spraener.nxtgen.AfterEmptyDir;
import de.spraener.nxtgen.CodeGeneratorMapping;
import de.spraener.nxtgen.NextGen;
import de.spraener.nxtgen.OnEmptyRootDir;
import de.spraener.nxtgen.annotations.CGV19Cartridge;
import de.spraener.nxtgen.laravel.filestrategies.LaravelFactoryToFileStrategy;
import de.spraener.nxtgen.laravel.tools.EnvPatcher;
import de.spraener.nxtgen.model.Model;
import de.spraener.nxtgen.model.ModelElement;
import de.spraener.nxtgen.oom.ModelHelper;
import de.spraener.nxtgen.oom.StereotypeHelper;
import de.spraener.nxtgen.oom.model.MClass;
import de.spraener.nxtgen.oom.model.MPackage;
import de.spraener.nxtgen.php.PhpHelper;

import java.io.File;
import java.util.List;

@CGV19Cartridge("Laravel")
public class Laravel extends LaravelBase implements OnEmptyRootDir, AfterEmptyDir {
    public static final String NAME = "Laravel";


    public Laravel() {
        super();
    }

    @Override
    public List<CodeGeneratorMapping> mapGenerators(Model m) {
        PhpHelper.setPackageNameProvider(LaravelHelper::toPackageName);
        return super.mapGenerators(m);
    }

    @Override
    public String getName() {
        return NAME;
    }

    @Override
    protected CodeGeneratorMapping createMapping(ModelElement me, String stereotypeName) {
        if( me instanceof MClass mc && LaravelStereotypes.LARAVELFACTORY.getName().equals(stereotypeName)) {
            return CodeGeneratorMapping.create(mc, new FactoryGenerator(
               cb->cb.setToFileStrategy(new LaravelFactoryToFileStrategy(mc))
            ));
        }
        return super.createMapping(me, stereotypeName);
    }

    @Override
    public void afterEmptyRootDir(NextGen nextGen, File rootDir, Model m) {
        try {
            NextGen.LOGGER.info("Adding additional components to new laravel project. This may take a while...");
            nextGen.executeCommand(rootDir, null, "composer -n require laravel/jetstream");
            nextGen.executeCommand(rootDir, null, "php artisan jetstream:install livewire --api -q --verification -n");
            nextGen.executeCommand(rootDir, null, "composer -n require filament/filament:^3.0-stable -W");
            nextGen.executeCommand(rootDir, null, "php artisan filament:install --panels -q");
            nextGen.executeCommand(rootDir, null, "npm install");
            nextGen.executeCommand(rootDir, null, "npm run build");

            configureEnvironment(rootDir, m);

        } catch( Exception e ) {
            throw new RuntimeException(e);
        }
        try {
            nextGen.executeCommand(rootDir, null, "php artisan migrate --force --no-interaction");
            nextGen.executeCommand(rootDir, null, "php artisan make:filament-user --name admin --email admin@localhost.net --password password");
            NextGen.LOGGER.info("Create user 'admin' with email 'admin@localhost.net' and password 'password.");
        } catch( Exception e ) {
            NextGen.LOGGER.warning("Migration and/or user creation failed. Please do this steps manually.");
        }
    }

    private static void configureEnvironment(File rootDir, Model m) {
        MPackage rootPkg = (MPackage)ModelHelper.findInStream(m.getModelElements().stream(),
                e -> e instanceof MPackage pkg && StereotypeHelper.hasStereotype(pkg, LaravelStereotypes.LARAVELAPPLICATION.getName())

        );
        if( rootPkg != null ) {
            new EnvPatcher(rootDir,rootPkg).patchEnv();
        }
    }

    @Override
    public void onEmptyRootDir(NextGen nextGen, File rootDir) {
        try {
            NextGen.LOGGER.info("Initializing new laravel project. This may take a while...");
            nextGen.executeCommand(rootDir, null, "composer", "-n", "create-project", "laravel/laravel",  ".");
        } catch( Exception e ) {
            throw new RuntimeException(e);
        }
    }
}
