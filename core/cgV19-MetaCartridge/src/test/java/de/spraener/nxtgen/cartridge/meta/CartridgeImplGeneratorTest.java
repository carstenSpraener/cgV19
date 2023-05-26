package de.spraener.nxtgen.cartridge.meta;

import de.spraener.nxtgen.model.impl.StereotypeImpl;
import de.spraener.nxtgen.oom.model.MClass;
import org.junit.Before;
import org.junit.Test;

import static org.assertj.core.api.Assertions.assertThat;

public class CartridgeImplGeneratorTest extends AbstractOOModelTest {
    public static final String DEFAULT_IMPL = """
            //THIS FILE IS GENERATED AS LONG AS THIS LINE EXISTS
            package pkg;
                        
            public class ACgv19Cartridge extends pkg.ACgv19CartridgeBase {
            
                @Override
                public String getName() {
                    return "ACgv19Cartridge";
                }
                        
            }
            """;
    private CartridgeImplGenerator uut = new CartridgeImplGenerator();
    private StereotypeImpl stGen = new StereotypeImpl(MetaStereotypes.CGV19CARTRIDGE.getName());

    @Before
    public void setup() {
        super.setup();
    }

    @Test
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
