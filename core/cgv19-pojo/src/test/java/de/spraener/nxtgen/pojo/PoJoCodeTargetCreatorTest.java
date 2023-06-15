package de.spraener.nxtgen.pojo;

import de.spraener.nxtgen.model.impl.StereotypeImpl;
import de.spraener.nxtgen.oom.model.*;
import de.spraener.nxtgen.target.CodeTarget;
import de.spraener.nxtgen.target.CodeTargetToCodeConverter;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;

import java.util.function.Consumer;

import static de.spraener.nxtgen.pojo.ModelMother.createModel;
import static org.assertj.core.api.Assertions.assertThat;

class PoJoCodeTargetCreatorTest {
    private static final String EXPECTED_CODE = """
            //THIS FILE IS GENERATED AS LONG AS THIS LINE EXISTS
            package a;
                        
            import java.util.List;
            import b.BPojo;
            import b.CPojo;
                        
            public class APojo {
                        
                private String name;
                private String surname;
                private Integer age;
                private BPojo myBPojo = null;
                private List<CPojo> myCPoJos = null;
                        
                public APojo() {
                }
                        
                public String getName() {
                    return name;
                }
                        
                public void setName( String value) {
                    this.name = value;
                }
                        
                public String getSurname() {
                    return surname;
                }
                        
                public void setSurname( String value) {
                    this.surname = value;
                }
                        
                public Integer getAge() {
                    return age;
                }
                        
                public void setAge( Integer value) {
                    this.age = value;
                }
                        
                public BPojo getMyBPojo() {
                    return this.myBPojo;
                }
                public void setMyBPojo( BPojo value ) {
                    this.myBPojo = value;
                }
                        
                public List<CPojo> getMyCPoJos() {
                    return this.myCPoJos;
                }
                public void setMyCPoJos( List<CPojo> value ) {
                    this.myCPoJos = value;
                }
                        
                public void addToMyCPoJos( CPojo value ) {
                    this.myCPoJos.add(value);
                }
                        
                public void removeFromMyCPoJos( CPojo value ) {
                    this.myCPoJos.remove(value);
                }
                        
            }
            """;

    @Test
    void testPoJoCreator() {
        OOModel model = createModel();
        MClass pojo = model.findClassByName("a.APojo");
        CodeTarget target = new PoJoCodeTargetCreator(pojo).createPoJoTarget();
        String code = new CodeTargetToCodeConverter(target).toString();
        assertThat(code).containsIgnoringWhitespaces(EXPECTED_CODE);
    }
}
