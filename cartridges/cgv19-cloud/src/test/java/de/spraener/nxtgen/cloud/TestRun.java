package de.spraener.nxtgen.cloud;

import de.spraener.nxtgen.NextGen;
import de.spraener.nxtgen.cloud.util.FileLister;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;

public class TestRun {
    public static final String[] EXPEXCTED_FILES = new String[]{
            "frontend/tsconfig.app.json",
            "frontend/Dockerfile",
            "frontend/README.md",
            "frontend/angular.json",
            "frontend/package.json",
            "frontend/tsconfig.json",
            "frontend/tsconfig.spec.json",
            "frontend/src/index.html",
            "frontend/src/app/app.component.html",
            "frontend/src/app/app-routing.module.ts",
            "frontend/src/app/app.component.spec.ts",
            "frontend/src/app/app.module.ts",
            "frontend/src/app/app.component.ts",
            "frontend/src/app/app.component.css",
            "frontend/src/main.ts",
            "frontend/src/styles.css",
            "frontend/src/assets/.gitkeep",
            "k8s/Ingress-service.yaml",
            "k8s/AppDatabase-service.yaml",
            "k8s/DatabaseDir-persistent-volume-claim.yaml",
            "k8s/KeyCloak-deployment.yaml",
            "k8s/Api-deployment.yaml",
            "k8s/KeyCloak-service.yaml",
            "k8s/keycloakdb-deployment.yaml",
            "k8s/keycloakdb-service.yaml",
            "k8s/AppDatabase-deployment.yaml",
            "k8s/Worker-deployment.yaml",
            "k8s/Worker-service.yaml",
            "k8s/Api-service.yaml",
            "k8s/Frontend-deployment.yaml",
            "k8s/Frontend-service.yaml",
            ".gitignore",
            "api/Dockerfile",
            "api/build.gradle",
            "api/src/main/java/de/spraener/tinyapp/api/NumberResourceController.java",
            "api/src/main/java/de/spraener/tinyapp/api/logic/NumberResourceLogic.java",
            "api/src/main/java/de/spraener/tinyapp/api/FrontendApp.java",
            "api/src/main/java-gen/de/spraener/tinyapp/api/NumberResourceControllerBase.java",
            "api/src/main/java-gen/de/spraener/tinyapp/api/logic/NumberResourceLogicBase.java",
            "api/src/main/java-gen/de/spraener/tinyapp/api/model/NumberResourceRepository.java",
            "api/src/main/java-gen/de/spraener/tinyapp/api/model/NumberResource.java",
            "buildDocker.sh",
            "docker-compose.yml",
            "worker/Dockerfile",
            "worker/build.gradle",
            "worker/src/main/java/de/spraener/tinyapp/worker/PerformComplexOperationHandler.java",
            "worker/src/main/java-gen/de/spraener/tinyapp/worker/WorkerApp.java",
            "worker/src/main/java-gen/de/spraener/tinyapp/worker/PerformComplexOperationHandlerBase.java",
            "keycloak/Dockerfile",
            "settings.gradle",
            "appdb/mariadb-appdb.cnf",
            "appdb/Dockerfile",
            "appdb/sql/init.sh",
            "appdb/sql/init.sql",
            "keycloakdb/sql/init.sh",
            "keycloakdb/sql/init.sql",
            "k8s/keycloakdb-persistent-volume-claim.yaml",
            "keycloakdb/Dockerfile",
            "keycloakdb/mariadb-keycloakdb.cnf"
    };
    private CloudCartridge uut = new CloudCartridge();

    @Test
    @Disabled("This test requires VisualParadigm up an running and loaded with cgv19-cartdiges model")
    public void testWholeGeneration() throws Exception {
        File dir = new File("./build/source-gen");
        dir.mkdirs();
        NextGen.setWorkingDir("./build/source-gen");
        System.setProperty("cgv19_globalHostURL", "http://192.168.0.201");
        NextGen.runCartridgeWithName(uut.getName());
        try (InputStream in = getClass().getResourceAsStream("/de.spraener.tinyapp.oom")) {
            Files.copy(in, Path.of("de.spraener.tinyapp.oom"), StandardCopyOption.REPLACE_EXISTING);
        }
        NextGen.main(new String[]{"http://localhost:7001/de.spraener.tinyapp"});

        FileLister lister = new FileLister(dir.getAbsolutePath());
        lister.asserContentEquals(EXPEXCTED_FILES);
    }
}
