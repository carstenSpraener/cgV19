package de.spraener.nxtgen.laravel.tools;

import de.spraener.nxtgen.laravel.LaravelStereotypes;
import de.spraener.nxtgen.model.ModelElement;
import de.spraener.nxtgen.oom.model.MPackage;

import java.io.File;
import java.io.IOException;

/**
 * Reads the TaggedValues of a Laravel Application from the model and patches
 * the .env-File with this tagged values.
 */
public class EnvPatcher {
    private static final String LARAVEL_APP = LaravelStereotypes.LARAVELAPPLICATION.getName();

    private File rootDir = null;

    private String dbConnection = "mysql";
    private String dbHost = "127.0.0.1";
    private String dbPort="3306";
    private String dbName = "laravel";
    private String dbUser = "root";
    private String dbPassword = "";

    public EnvPatcher(File rootDir, ModelElement me) {
        this.rootDir = rootDir;
        if( me instanceof MPackage rootPkg) {
            this.dbConnection = readTV(rootPkg, "dbConnection", this.dbConnection);
            this.dbHost = readTV(rootPkg, "dbHost", this.dbHost);
            this.dbPort = readTV(rootPkg, "dbPort", this.dbPort);
            this.dbName = readTV(rootPkg, "dbName", this.dbName);
            this.dbUser = readTV(rootPkg, "dbUser", this.dbUser);
            this.dbPassword = readTV(rootPkg, "dbPassword", this.dbPassword);
        }
    }

    private String readTV(MPackage rootPkg, String taggedValue, String defaultValue) {
        String value = rootPkg.getTaggedValue(LARAVEL_APP, taggedValue);
        if( value == null ) {
            value = defaultValue;
        }
        return value;
    }

    public void patchEnv() {
        this.patchEnv(this.rootDir.getAbsolutePath()+"/.env");
    }

    public void patchEnv(String envName) {
        try(FilePatcher patcher = new FilePatcher(envName)){
            patcher.replaceOrAppend("DB_CONNECTION=.*", "DB_CONNECTION=%s", this.dbConnection);
            patcher.replaceOrAppend("DB_HOST=.*", "DB_HOST=%s", this.dbHost);
            patcher.replaceOrAppend("DB_PORT=.*", "DB_PORT=%s", this.dbPort);
            patcher.replaceOrAppend("DB_DATABASE=.*", "DB_DATABASE=%s", this.dbName);
            patcher.replaceOrAppend("DB_USERNAME=.*", "DB_USERNAME=%s", this.dbUser);
            patcher.replaceOrAppend("DB_PASSWORD=.*", "DB_PASSWORD=%s", this.dbPassword);
        } catch( IOException xc ) {
            throw new RuntimeException(xc);
        }
    }
}
