package de.spraener.nxtgen.cartridge.meta;

import de.spraener.nxtgen.oom.model.OOModel;
import org.junit.Before;

public class AbstractOOModelTest {
    protected OOMetaModelObjectMother oomObjectMother;
    protected OOModel model;
    @Before
    public void setup() {
        oomObjectMother = new OOMetaModelObjectMother();
        oomObjectMother.createDefaultOOModel();
        this.model = oomObjectMother.getModel();
    }
}
