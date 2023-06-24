package de.spraener.nxtgen.cartridge.rest;

import de.spraener.nxtgen.CodeBlock;
import de.spraener.nxtgen.MustacheGenerator;
import de.spraener.nxtgen.model.impl.StereotypeImpl;
import de.spraener.nxtgen.oom.model.MClass;
import org.junit.Test;

public class TestMustacheGenerator {

    @Test
    public void testMustacheGenerator() {
        MustacheGenerator gen = new MustacheGenerator(
                "/mustache/springBootApp/build.gradle.mustache",
                "build.gradle",
                SpringBootApp::fillBuildScriptMap
        );
        MClass app = OOMObjectMother.createSimpleModelForClass("TestApp");
        app.addStereotypes(new StereotypeImpl("SpringBootApp"));
        CodeBlock cb = gen.resolve(app, "");
        System.out.println(cb.toCode());
    }
}
