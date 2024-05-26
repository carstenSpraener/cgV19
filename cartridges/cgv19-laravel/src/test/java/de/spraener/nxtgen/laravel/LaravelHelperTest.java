package de.spraener.nxtgen.laravel;

import de.spraener.nxtgen.oom.model.MClass;
import de.spraener.nxtgen.oom.model.OOModel;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

public class LaravelHelperTest {

    @Test
    public void testNamespaceBuilding() throws Exception {
        OOModel model = OOModelMother.createDefaultModel();
        MClass event = OOModelMother.getEvent(model);
        String namespace = LaravelHelper.toNameSpace(event);

        Assertions.assertEquals("App\\Models\\Events", namespace);
    }
}
