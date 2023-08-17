package de.spraener.nxtgen.pojo;

import de.spraener.nxtgen.Transformation;
import de.spraener.nxtgen.model.Model;
import de.spraener.nxtgen.model.ModelElement;
import de.spraener.nxtgen.model.ModelHelper;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertNotNull;

public class TestCartridgeRun {

    @Test
    void testCartridgeRun() throws Exception {
        Model model = ModelMother.createModel();
        PoJoCartridge uut = new PoJoCartridge();

        for(Transformation t : uut.getTransformations() ) {
            for(ModelElement e : model.getModelElements() ) {
                t.doTransformation(e);
            }
        }
        assertNotNull(ModelHelper.findByFQName(model, "a.APojoBase", "."));
    }
}
