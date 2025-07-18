package de.spraener.nxtgen.oom.cartridge;

import de.spraener.nxtgen.model.ModelElement;
import de.spraener.nxtgen.model.ModelHelper;
import de.spraener.nxtgen.oom.OOModelBuilder;
import de.spraener.nxtgen.oom.model.*;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Disabled;

import de.spraener.nxtgen.oom.cartridge.OOModelMother;

public class ModelElementFactoryGeneratorTest {
    private ModelElementFactoryGenerator uut = new ModelElementFactoryGenerator();

    private OOModel model;
    private ModelElement testME;
    
    @BeforeEach
    void setup() {
        model = OOModelMother.createDefaultModel();
    }
    
    @Test
    void testImplClassGeneration() throws Exception {
        MClass meFactory = model.findClassByName(OOModelMother.MODEL_ROOT_PKG+"."+ElementFactoryCreationTransformation.FACTORY_CLASS_NAME);
        String code = uut.resolve(meFactory, "").toCode();
        Assertions.assertThat(code)
                .containsIgnoringWhitespaces("public class "+ElementFactoryCreationTransformation.FACTORY_CLASS_NAME+" extends "+ElementFactoryCreationTransformation.FACTORY_CLASS_NAME+"Base {");
    }

}
