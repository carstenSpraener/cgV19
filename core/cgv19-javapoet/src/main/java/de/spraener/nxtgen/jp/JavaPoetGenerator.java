package de.spraener.nxtgen.jp;

import com.squareup.javapoet.TypeSpec;
import de.spraener.nxtgen.CodeBlock;
import de.spraener.nxtgen.CodeGenerator;
import de.spraener.nxtgen.ProtectionStrategie;
import de.spraener.nxtgen.SimpleStringCodeBlock;
import de.spraener.nxtgen.model.ModelElement;
import de.spraener.nxtgen.oom.model.MClass;

import java.util.ArrayList;
import java.util.List;
import java.util.function.BiConsumer;
import java.util.function.Function;

/**
 * This generator enables cgV19 to use JavaPoet as a code generation framework.
 * Inside your cartridge you can map this JavaPoetGenerator to an Instance of MClass.
 * You can provide some BiConsumers, that take a TypeSpec.Builder and a MClass and
 * add several aspects to an JavaClass.
 * <p>
 * With this approach it is possible to provide basic java generator functionality and
 * use it when you need to implement other aspects.
 * <p>
 * You can derive your own CodeGenerators for a specific task from this class or
 * use it and configure it in the cartridges mapElement-Method.
 * <p>
 * JavaPoetCodeGenerator is only applicable to MClass instances. Otherwise, it will
 * throw an IllegalArgumentException.
 */
public class JavaPoetGenerator implements CodeGenerator {
    Function<MClass, TypeSpec.Builder> typeSpecCreator;
    List<BiConsumer<TypeSpec.Builder, MClass>> typeSpecConsumers = new ArrayList<>();
    List<CodeBlock> codeBlocksBefore = new ArrayList<>();

    /**
     * Creates a new CodeGenerator to use JavaPoet. Use this constructor in your cartridge
     * when it has to map a MClass with Stereotype to an CodeGenerator.
     *
     * @param typeSpecCreator   Converts a MClass to a JavaPoet TypeSpec.Builder
     * @param typeSpecConsumers none or more Consumers which take the TypeSpec.Builder and a MClass instance to modify the TypeSpec.Builder
     */
    public JavaPoetGenerator(Function<MClass, TypeSpec.Builder> typeSpecCreator, BiConsumer<TypeSpec.Builder, MClass>... typeSpecConsumers) {
        this.typeSpecCreator = typeSpecCreator;
        if (typeSpecConsumers != null) {
            for (BiConsumer<TypeSpec.Builder, MClass> tsC : typeSpecConsumers) {
                addTypeSpecConsumer(tsC);
            }
        }
    }

    public JavaPoetGenerator withCodeBlockBefore(CodeBlock cb) {
        this.codeBlocksBefore.add(cb);
        return this;
    }

    /**
     * Add more consumers to the generator. Each consumer will be called in the order
     * they are added.
     *
     * @param tsConsumers none or more consumers to be applied to the MClass
     * @return this instance for queueing more calls
     */
    public JavaPoetGenerator addTypeSpecConsumer(BiConsumer<TypeSpec.Builder, MClass>... tsConsumers) {
        if (tsConsumers != null) {
            for (BiConsumer<TypeSpec.Builder, MClass> tsC : tsConsumers) {
                this.typeSpecConsumers.add(tsC);
            }
        }
        return this;
    }

    /**
     * Apply this code generator to the given ModelElement. The JavaPoet CodeGenerator
     * can only be applied to MClass instances. Otherwise, it will throw an IllegalArgumentException
     * describing the wrong usage.
     *
     * @param element a instance of MClass! to generate code for
     * @param templateName a Template to apply on the model element.
     *
     * @return JavaPoetCodeBlock instance
     */
    @Override
    public CodeBlock resolve(ModelElement element, String templateName) {
        if (element instanceof MClass mc) {
            String srcDir = "";
            TypeSpec.Builder typeSpecBuilder = this.typeSpecCreator.apply(mc);
            for (BiConsumer<TypeSpec.Builder, MClass> c : this.typeSpecConsumers) {
                c.accept(typeSpecBuilder, mc);
            }
            CodeBlock codeBlock = new JavaPoetRootCodeBlock(srcDir, typeSpecBuilder, mc);
            for( CodeBlock cb : this.codeBlocksBefore ) {
                codeBlock.addCodeBlock(cb);
            }
            return codeBlock;
        } else {
            throw new IllegalArgumentException("JavaPoetGenerator is applicable only on MClass instances. You tried to apply it on : " + element.getName() + " of type " + element.getClass().getName() + ".");
        }
    }
}
