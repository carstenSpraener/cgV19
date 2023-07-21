package de.spraener.nxtgen.cartridge.rest;

import de.spraener.nxtgen.incubator.BlueprintCompiler;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class TestBlueprintCompiler {
    private BlueprintCompiler uut = new BlueprintCompiler("/blueprints/springCloud");

    @Test
    void testListAllFiles() {
        List<String> fileList = uut.getFileList();
        assertNotNull(fileList);
        assertTrue(fileList.contains("{{appname}}-frontend/Dockerfile.mustache"));
    }

    @Test
    void testReadScope() {
        assertNotNull(uut.getScope());
        assertTrue(uut.getScope().containsKey("appname"));
        assertTrue(uut.getScope().containsKey("externalURL"));
        assertTrue(uut.getScope().containsKey("applicationPort"));
    }

    @Test
    void testEvaluate() {
        uut.getScope().put("appname", "testapp");
        uut.getScope().put("externalURL", "http://localhost:8081");
        uut.getScope().put("applicationPort", "8081");

        uut.evaluteTo("build/tmp/blueprint");
        assertThat(new File("build/tmp/blueprint/keycloak/Dockerfile")).exists();
        assertThat(new File("build/tmp/blueprint/docker-compose.yml")).exists();
    }
}
