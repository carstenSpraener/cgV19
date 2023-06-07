package de.spraener.nxtgen.jp;

import com.squareup.javapoet.FieldSpec;
import com.squareup.javapoet.MethodSpec;
import com.squareup.javapoet.TypeSpec;
import de.spraener.nxtgen.oom.model.MClass;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;

import javax.lang.model.element.Modifier;

import static org.junit.jupiter.api.Assertions.*;

class JavaPoetGeneratorTest {

    void createPojo(TypeSpec.Builder tsBuilder, MClass mc) {
        FieldSpec fs = FieldSpec.builder(String.class, "name")
                .addModifiers(Modifier.PRIVATE)
                .build();
        tsBuilder.addField(fs);
        MethodSpec getter = MethodSpec.methodBuilder("getName")
                .returns(String.class)
                .addStatement("return $L;", "name")
                .build();
        tsBuilder.addMethod(getter);
        MethodSpec setter = MethodSpec.methodBuilder("setName")
                .returns(String.class)
                .addParameter(String.class, "value")
                .addStatement("return this.$L=value;", "name")
                .build();
        tsBuilder.addMethod(setter);
    }
    @Test
    void testJavaPoetCallsHandler() {
        MClass myClass = new MClass();
        myClass.setName("SimplePoJo");
        JavaPoetGenerator gen = new JavaPoetGenerator(mc ->TypeSpec.classBuilder(mc.getName()))
                .addTypeSpecConsumer(this::createPojo);
        String code = gen.resolve(myClass, "").toCode();
        Assertions.assertThat(code)
                .contains("""
                        class SimplePoJo {
                          private java.lang.String name;
                                                
                          java.lang.String getName() {
                            return name;;
                          }
                                                
                          java.lang.String setName(java.lang.String value) {
                            return this.name=value;;
                          }
                        }
                        """);
    }
}
