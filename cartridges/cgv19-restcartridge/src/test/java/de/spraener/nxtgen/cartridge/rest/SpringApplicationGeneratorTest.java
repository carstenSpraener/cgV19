package de.spraener.nxtgen.cartridge.rest;

import de.spraener.nxtgen.CodeBlock;
import de.spraener.nxtgen.oom.model.MClass;
import de.spraener.nxtgen.oom.model.OOModel;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertTrue;

public class SpringApplicationGeneratorTest {

    @Test
    void testSpringBootApplicationGeneration() {
        SpringBootAppGenerator uut = new SpringBootAppGenerator();
        OOModel model = OOMObjectMother.createModel(
                m->OOMObjectMother.createPackage(m, "app",
                        pkg -> OOMObjectMother.createMClass(pkg, "MyApp",
                                mc -> OOMObjectMother.createStereotype(mc, "SpringBootApp")
                        )
                )
        );
        MClass mc = model.findClassByName("app.MyApp");

        CodeBlock cb = uut.resolve(mc, "");
        String impl = cb.toCode();
        assertTrue(impl.contains("@SpringBootApplication"));
    }
}
