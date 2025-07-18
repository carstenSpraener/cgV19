package de.spraener.nxtgen.oom.cartridge;

import de.spraener.nxtgen.model.ModelElement;
import de.spraener.nxtgen.oom.model.OOModel;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

public class MElementBaseGeneratorTest {
    private MElementBaseGenerator uut = new MElementBaseGenerator();

    private OOModel model;
    private ModelElement testME;

    @BeforeEach
    void setup() {
        // Create the required model elements with the OOModelBuilder
        model = OOModelMother.createDefaultModel();
        testME = model.findClassByName(OOModelMother.MCLASS_FQNAME+"Base");
    }

    @Test
    void testCodeGeneratorGapBase() throws Exception {
        // Generate the Code
        String code = uut.resolve(testME, "").toCode();
        String subCode = new MElementGenerator().resolve(OOModelMother.getMClass(model), "").toCode();
        // Check the generated code to contain...
        assertThat(code)
                .containsIgnoringWhitespaces("public class MClassBase extends MAbstractModelElement {")
        ;
        assertThat(subCode)
                .containsIgnoringWhitespaces("public class MClass extends my.test.model.MClassBase {")
        ;

    }

    @Test
    void testToNReference() throws Exception {
        String code = uut.resolve(testME, "").toCode();
        // Check the generated code to contain...
        assertThat(code)
                .contains("private List<my.test.model.MOperation> operations = null;")
                .contains("public List<my.test.model.MOperation> getOperations() {")
                .containsIgnoringWhitespaces("""
                                if( this.operations == null ) {
                                    operations = filterChilds(child -> child instanceof my.test.model.MOperation)
                                            .map(child -> (my.test.model.MOperation)child)
                                            .collect(Collectors.toList());
                                }
                                return operations;
                        """)
        ;
    }

    @Test
    void testToOneReference() throws Exception {
        String code = uut.resolve(testME, "").toCode();
        assertThat(code)
                .containsIgnoringWhitespaces("private my.test.model.MClassRef inheritsFrom = null;")
        ;
        assertThat(code)
                .containsIgnoringWhitespaces("""
                        public my.test.model.MClassRef getInheritsFrom() {
                            return this.inheritsFrom;
                        }
                        """)
        ;
        assertThat(code)
                .containsIgnoringWhitespaces("""
                        public MClass setInheritsFrom(my.test.model.MClassRef value) {
                            this.inheritsFrom = value;
                            return this;
                        }
                        """)
        ;

    }
}