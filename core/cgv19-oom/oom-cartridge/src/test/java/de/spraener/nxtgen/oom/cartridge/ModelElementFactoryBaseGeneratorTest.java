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

public class ModelElementFactoryBaseGeneratorTest {
    private ModelElementFactoryBaseGenerator uut = new ModelElementFactoryBaseGenerator();

    private OOModel model;
    private ModelElement testME;

    @BeforeEach
    void setup() {
        model = OOModelMother.createDefaultModel();
    }

    @Test
    void testBaseClassImplementsModelElementFactory() throws Exception {
        MClass meFactoryBase = model.findClassByName(OOModelMother.MODEL_ROOT_PKG + "." + ElementFactoryCreationTransformation.FACTORY_CLASS_NAME+"Base");
        String code = uut.resolve(meFactoryBase, "").toCode();
        Assertions.assertThat(code)
                .containsIgnoringWhitespaces("public class " + ElementFactoryCreationTransformation.FACTORY_CLASS_NAME + "Base implements ModelElementFactory {");
    }

    @Test
    void testBaseClassMapsMClass() throws Exception {
        MClass meFactoryBase = model.findClassByName(OOModelMother.MODEL_ROOT_PKG + "." + ElementFactoryCreationTransformation.FACTORY_CLASS_NAME+"Base");
        String code = uut.resolve(meFactoryBase, "").toCode();
        Assertions.assertThat(code)
                .containsIgnoringWhitespaces(
                        """
                                case "mClass":
                                    return new my.test.model.MClass();
                                    break;
                                """
                );
    }

    @Test
    void testBaseClassMapsUndefinedModelElementNameToClassName() throws Exception {
        MClass meFactoryBase = model.findClassByName(OOModelMother.MODEL_ROOT_PKG + "." + ElementFactoryCreationTransformation.FACTORY_CLASS_NAME+"Base");
        String code = uut.resolve(meFactoryBase, "").toCode();
        Assertions.assertThat(code)
                .containsIgnoringWhitespaces(
                        """
                                case "mOperation":
                                    return new my.test.model.MOperation();
                                    break;
                                """
                );
    }
}
