package de.spraener.nxtgen.laravel.filestrategies;

import de.spraener.nxtgen.NextGen;
import de.spraener.nxtgen.filestrategies.ToFileStrategy;
import de.spraener.nxtgen.laravel.LaravelHelper;
import de.spraener.nxtgen.oom.model.MClass;

import java.io.File;

public class LaravelFactoryToFileStrategy implements ToFileStrategy {
    MClass factory;

    public LaravelFactoryToFileStrategy(MClass factory) {
        this.factory = factory;
    }

    @Override
    public File open() {
        String phpPath = LaravelHelper.createPhpPath(factory).replace("App/Models/", "");
        String path = NextGen.getWorkingDir()+"/database/factories/"+phpPath+"/"+factory.getName()+".php";
        return new File(path);
    }
}
