package de.spraener.nxtgen.jp;

import com.squareup.javapoet.TypeSpec;
import de.spraener.nxtgen.CodeBlock;
import de.spraener.nxtgen.CodeGenerator;
import de.spraener.nxtgen.model.ModelElement;
import de.spraener.nxtgen.oom.model.MClass;

import java.util.ArrayList;
import java.util.List;
import java.util.function.BiConsumer;
import java.util.function.Consumer;
import java.util.function.Function;

public class JavaPoetGenerator implements CodeGenerator {
    Function<MClass, TypeSpec.Builder> typeSpecCreator;
    List<BiConsumer<TypeSpec.Builder, MClass>> typeSpecConsumers = new ArrayList<>();

    public JavaPoetGenerator(Function<MClass, TypeSpec.Builder> typeSpecCreator, BiConsumer<TypeSpec.Builder, MClass>... typeSpecConsumers ) {
        this.typeSpecCreator = typeSpecCreator;
        if( typeSpecConsumers != null ) {
            for( BiConsumer<TypeSpec.Builder, MClass> tsC : typeSpecConsumers ) {
                addTypeSpecConsumer(tsC);
            }
        }
    }

    public JavaPoetGenerator addTypeSpecConsumer(BiConsumer<TypeSpec.Builder, MClass> tsC) {
        this.typeSpecConsumers.add(tsC);
        return this;
    }

    @Override
    public CodeBlock resolve(ModelElement element, String templateName) {
        if( element instanceof MClass mc ) {
            String srcDir = "";
            TypeSpec.Builder typeSpecBuilder = this.typeSpecCreator.apply(mc);
            for( BiConsumer<TypeSpec.Builder, MClass> c : this.typeSpecConsumers ) {
                c.accept(typeSpecBuilder, mc);
            }
            return new JavaPoetRootCodeBlock(srcDir, typeSpecBuilder, mc);
        } else {
            throw new IllegalArgumentException("JavaPoetGenerator is applicable only on MClass instances. You tried to apply it on : "+element.getName()+" of type "+element.getClass().getName()+".");
        }
    }
}
