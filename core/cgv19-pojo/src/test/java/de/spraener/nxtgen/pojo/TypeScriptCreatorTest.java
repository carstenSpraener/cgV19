package de.spraener.nxtgen.pojo;

import de.spraener.nxtgen.oom.model.MClass;
import de.spraener.nxtgen.oom.model.OOModel;
import de.spraener.nxtgen.target.CodeTarget;
import de.spraener.nxtgen.target.CodeTargetToCodeConverter;
import org.junit.jupiter.api.Test;

import static de.spraener.nxtgen.pojo.ModelMother.createModel;
import static org.assertj.core.api.Assertions.assertThat;

public class TypeScriptCreatorTest {
    private static final String EXPECTED_CODE = """
            // THIS FILE IS GENERATED AS LONG AS THIS LINE EXISTS
                        
            export class APojo {
                name: string | undefined
                surname: string | undefined
                age: number | undefined
            }
            """;
    @Test
    void testTypeScriptCreator() {
        OOModel model = createModel();
        MClass pojo = model.findClassByName("a.APojo");
        CodeTarget target = new TypeScriptCreator(pojo).createPoJo();
        String code = new CodeTargetToCodeConverter(target).toString();
        assertThat(code).containsIgnoringWhitespaces(EXPECTED_CODE);
    }
}
