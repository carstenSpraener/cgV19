package de.spraener.nxtgen.cartridge.meta;

import de.spraener.nxtgen.model.impl.StereotypeImpl;
import de.spraener.nxtgen.oom.model.MClass;
import de.spraener.nxtgen.oom.model.OOModel;
import org.junit.Before;

public class AbstractOOModelTest {
    protected OOMetaModelObjetctMother oomObjectMother;
    protected OOModel model;
    @Before
    public void setup() {
        oomObjectMother = new OOMetaModelObjetctMother();
        oomObjectMother.createDefaultOOModel();
        this.model = oomObjectMother.getModel();
    }
}
