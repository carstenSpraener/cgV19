//THIS FILE IS GENERATED AS LONG AS THIS LINE EXISTS
package de.spraener.nxtgen.cloud;

import de.spraener.nxtgen.oom.OOModelBuilder;
import de.spraener.nxtgen.oom.model.OOModel;

public class OOModelMother {
    // TODO: Build a default model to use in your test cases
    public static OOModel createDefaultModel() {
        return OOModelBuilder.createModel(
            m -> OOModelBuilder.createPackage(m, "my.test.model")
        );
    }
}
