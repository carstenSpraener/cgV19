package de.spraener.nxtgen.jp.cartridge;

import com.squareup.javapoet.AnnotationSpec;
import com.squareup.javapoet.ClassName;
import com.squareup.javapoet.FieldSpec;
import com.squareup.javapoet.TypeSpec;
import de.spraener.nxtgen.oom.model.MAttribute;
import de.spraener.nxtgen.oom.model.MClass;

public class EntityGenerator {
    public static void build(TypeSpec.Builder builder, MClass mClass) {
        new EntityGenerator().generate(builder, mClass);
    }

    public void generate(TypeSpec.Builder builder, MClass mClass) {
        addEntityAnnotations(builder, mClass);
        for (MAttribute attr : mClass.getAttributes()) {
            FieldSpec fs = findFieldSpec(builder,attr);
            if( fs==null ) {
                continue;
            }
            toEntityFieldSpec(builder, fs);
        }
    }

    private void addEntityAnnotations(TypeSpec.Builder builder, MClass mClass) {
        builder.addAnnotation(ClassName.get("javax.persistence", "Entity"));
        AnnotationSpec tableSpec = AnnotationSpec
                .builder(ClassName.get("javax.persistence", "Table"))
                        .addMember("name", "\""+ mClass.getName().toLowerCase()+"\"")
                        .build();
        builder.addAnnotation(tableSpec);
    }

    private void toEntityFieldSpec(TypeSpec.Builder builder, FieldSpec fs) {
        FieldSpec.Builder entityFieldBuilder = fs.toBuilder();
        entityFieldBuilder.addAnnotation(ClassName.get("javax.persistence", "Column"));
        replaceFieldSpec(builder, fs, entityFieldBuilder.build());
    }

    private void replaceFieldSpec(TypeSpec.Builder builder, FieldSpec fs, FieldSpec entityFieldSpec) {
        builder.fieldSpecs.remove(fs);
        builder.fieldSpecs.add(entityFieldSpec);
    }

    private FieldSpec findFieldSpec(TypeSpec.Builder builder, MAttribute attr) {
        return builder.fieldSpecs.stream()
                .filter(fs ->fs.name.equals(attr.getName()))
                .findFirst()
                .orElse(null);
    }
}
