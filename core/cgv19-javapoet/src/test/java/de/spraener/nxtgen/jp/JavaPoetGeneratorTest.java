package de.spraener.nxtgen.jp;

import com.squareup.javapoet.*;
import de.spraener.nxtgen.oom.model.MClass;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;

import javax.lang.model.element.Modifier;

import java.lang.annotation.Retention;

import static org.junit.jupiter.api.Assertions.*;

class JavaPoetGeneratorTest {

    void createPojo(TypeSpec.Builder tsBuilder, MClass mc) {
        FieldSpec fs = FieldSpec.builder(String.class, "name")
                .addModifiers(Modifier.PRIVATE)
                .build();
        tsBuilder.addField(fs);
        MethodSpec getter = MethodSpec.methodBuilder("getName")
                .returns(String.class)
                .addStatement("return $L", "name")
                .addModifiers(Modifier.PUBLIC)
                .build();
        tsBuilder.addMethod(getter);
        MethodSpec setter = MethodSpec.methodBuilder("setName")
                .returns(TypeName.VOID)
                .addParameter(String.class, "value")
                .addStatement("this.$L=value", "name")
                .addModifiers(Modifier.PUBLIC)
                .build();
        tsBuilder.addMethod(setter);
    }

    void addDepreatedAnnotations(TypeSpec.Builder builder, MClass mc) {
        MethodSpec setNameSpec = builder.methodSpecs.stream().filter( mSpec -> mSpec.name.equals("setName")).findFirst().orElse(null);
        if( setNameSpec!=null ) {
            AnnotationSpec as = AnnotationSpec.builder(Deprecated.class).build();
            MethodSpec mSpec = setNameSpec.toBuilder().addAnnotation(as).build();
            builder.methodSpecs.remove(setNameSpec);
            builder.methodSpecs.add(mSpec);
        }
    }

    @Test
    void testJavaPoetCallsHandler() {
        MClass myClass = new MClass();
        myClass.setName("SimplePoJo");
        JavaPoetGenerator gen = new JavaPoetGenerator(mc ->TypeSpec.classBuilder(mc.getName()))
                .addTypeSpecConsumer(this::createPojo)
                .addTypeSpecConsumer(this::addDepreatedAnnotations)
                ;
        String code = gen.resolve(myClass, "").toCode();
        Assertions.assertThat(code)
                .contains("""
                        class SimplePoJo {
                          private String name;
                                                
                          public String getName() {
                            return name;
                          }
                          
                          @Deprecated                      
                          public void setName(String value) {
                            this.name=value;
                          }
                        }
                        """);
    }
}
