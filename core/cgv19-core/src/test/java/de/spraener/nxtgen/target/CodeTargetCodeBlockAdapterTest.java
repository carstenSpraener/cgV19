package de.spraener.nxtgen.target;

import de.spraener.nxtgen.model.impl.ModelElementImpl;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;

class CodeTargetCodeBlockAdapterTest {

    @Test
    void toCodeEvaluateCorrect() {
        CodeTarget pojoTarget = ObjectMother.createPoJoTarget(new ModelElementImpl());
        assertThat(new CodeTargetCodeBlockAdapter(pojoTarget).toCode())
                .containsIgnoringWhitespaces(ObjectMother.POJO_CODE);
    }
}
