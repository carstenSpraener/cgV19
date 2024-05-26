package de.spraener.nxtgen.symfony;

import de.spraener.nxtgen.model.ModelHelper;
import de.spraener.nxtgen.oom.OOModelBuilder;
import de.spraener.nxtgen.oom.model.MPackage;
import de.spraener.nxtgen.oom.model.OOModel;

public class OOModelMother {

    public static OOModel createDefaultModel() {
        return OOModelBuilder.createModel(
            m -> OOModelBuilder.createPackage(m, "my",
                    my->OOModelBuilder.createPackage(my, "test",
                            test->OOModelBuilder.createPackage(test, "model",
                                model -> OOModelBuilder.addStereotype(model, SymfonyStereotypes.SYMFONYAPP.getName())
                            )
                    )
            )
        );
    }

    public static MPackage getRootPkg(OOModel model) {
        return (MPackage) ModelHelper.findByFQName(model, "my.test.model", ".");
    }
}
