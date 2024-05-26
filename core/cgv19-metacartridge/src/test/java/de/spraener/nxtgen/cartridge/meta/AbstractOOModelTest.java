package de.spraener.nxtgen.cartridge.meta;

import de.spraener.nxtgen.oom.model.OOModel;
import org.junit.jupiter.api.BeforeEach;

public class AbstractOOModelTest {
    protected OOMetaModelObjectMother oomObjectMother;
    protected OOModel model;

    @BeforeEach
    public void setup() {
        oomObjectMother = new OOMetaModelObjectMother();
        oomObjectMother.createDefaultOOModel();
        this.model = oomObjectMother.getModel();
    }
}
