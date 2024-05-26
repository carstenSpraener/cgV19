package de.spraener.nxtgen.cartridge.meta;

import de.spraener.nxtgen.model.impl.StereotypeImpl;
import de.spraener.nxtgen.oom.model.MClass;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

public class CartridgeImplGeneratorTest extends AbstractOOModelTest {
    public static final String DEFAULT_IMPL = """
            //THIS FILE IS GENERATED AS LONG AS THIS LINE EXISTS
            package pkg;
                
                    
            public class ACgv19Cartridge extends ACgv19CartridgeBase{
            

                public ACgv19Cartridge() {
                    super();
                }

                @Override
                public String getName() {
                    return "ACgv19Cartridge";
                }
                        
            }
            """;
    private CartridgeImplGenerator uut = new CartridgeImplGenerator();
    private StereotypeImpl stGen = new StereotypeImpl(MetaStereotypes.CGV19CARTRIDGE.getName());

    @BeforeEach
    public void setup() {
        super.setup();
    }

    @Test
    @Disabled("when building a release this test fails with a MultipleCompilationErrorsException?")
    public void testCartridgeImplGeneratorDefault() throws Exception {
        MClass gen = oomObjectMother.createClass("ACgv19Cartridge",
                c -> c.addStereotypes(stGen)
        );
        CartridgeBaseForCartridgeTransformation t = new CartridgeBaseForCartridgeTransformation();
        t.doTransformation(gen);

        String code = uut.resolve(gen, "").toCode();
        assertThat(code)
                .contains(DEFAULT_IMPL)
        ;
    }
}
