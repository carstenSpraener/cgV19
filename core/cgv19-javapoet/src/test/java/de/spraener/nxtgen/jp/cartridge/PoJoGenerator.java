package de.spraener.nxtgen.jp.cartridge;

import com.squareup.javapoet.*;
import de.spraener.nxtgen.oom.cartridge.JavaHelper;
import de.spraener.nxtgen.oom.model.MAssociation;
import de.spraener.nxtgen.oom.model.MAttribute;
import de.spraener.nxtgen.oom.model.MClass;

import javax.lang.model.element.Modifier;

public class PoJoGenerator {
    public static void build(TypeSpec.Builder builder, MClass mClass) {
        new PoJoGenerator().generate(builder, mClass);
    }

    public void generate(TypeSpec.Builder builder, MClass mClass) {
        for (MAttribute attr : mClass.getAttributes()) {
            generateAttribute(builder, attr);
        }
        for (MAssociation assoc : mClass.getAssociations()) {
            generateAssociation(builder, assoc);
        }
    }

    public PoJoGenerator generateAttribute(TypeSpec.Builder builder, MAttribute attr) {
        FieldSpec fieldSpec = FieldSpec.builder(String.class, attr.getName(), Modifier.PRIVATE)
                .build();
        builder.fieldSpecs.add(fieldSpec);
        String accessName = JavaHelper.firstToUpperCase(attr.getName());

        MethodSpec setter = MethodSpec.methodBuilder("set" + accessName)
                .addModifiers(Modifier.PUBLIC)
                .returns(TypeName.VOID)
                .addParameter(ClassName.bestGuess(attr.getType()), attr.getName())
                .addStatement("this.$L = value;", attr.getName())
                .build();

        MethodSpec getter = MethodSpec.methodBuilder("set" + accessName)
                .addModifiers(Modifier.PUBLIC)
                .returns(ClassName.bestGuess(attr.getType()))
                .addStatement("return this.$L;", attr.getName())
                .build();

        builder.methodSpecs.add(setter);
        builder.methodSpecs.add(getter);
        return this;
    }

    public PoJoGenerator generateAssociation(TypeSpec.Builder builder, MAssociation assoc) {

        return this;
    }

}
