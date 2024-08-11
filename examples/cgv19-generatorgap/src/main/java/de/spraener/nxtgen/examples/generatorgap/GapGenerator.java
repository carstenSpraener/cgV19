package de.spraener.nxtgen.examples.generatorgap;

import de.spraener.nxtgen.CodeBlock;
import de.spraener.nxtgen.annotations.*;
import de.spraener.nxtgen.java.JavaCodeBlock;
import de.spraener.nxtgen.model.ModelElement;
import de.spraener.nxtgen.oom.cartridge.GeneratorGapTransformation;
import de.spraener.nxtgen.oom.model.MClass;
import de.spraener.nxtgen.pojo.ClassFrameTargetCreator;
import de.spraener.nxtgen.pojo.PoJoCodeTargetCreator;
import de.spraener.nxtgen.target.CodeTarget;
import de.spraener.nxtgen.target.CodeTargetCodeBlockAdapter;

/**
 * Demonstration of the <b>GeneratorGap</b> implementation in cgv19.
 * This class is a CGV19Component containing the required transformation
 * and the two generators for the base and the template class.
 */
@CGV19Component
public class GapGenerator {

    /**
     * Implement the model transformation with the help of the
     * cgv19 provided class GeneratorGapTransformation.
     *
     * @param me
     */
    @CGV19Transformation(
            requiredStereotype = "PoJo",
            operatesOn = MClass.class
    )
    public void pojoGeneratorGapTransformation(ModelElement me) {
        new GeneratorGapTransformation().doTransformation((MClass) me);
    }

    /**
     * <p>This method implements the generation of the template class into
     * the src directory. The class has to be a simple class frame with
     * no attributes or setter / getter. It only has to extend the base
     * class.
     * </p>
     * <p>
     *     The implementation of the generator uses the ClassFrameTargetCreator
     *     from the pojo cartridge of cgv19. The ClassFrameTargetCreator
     *     creates a code target that only contains the declaration, imports,
     *     package and extend expressions of a java class.
     * </p>
     * @param me
     * @param templateName
     * @return
     */
    @CGV19Generator(
            requiredStereotype = "PoJo",
            outputType = OutputType.JAVA,
            outputTo = OutputTo.SRC,
            operatesOn = MClass.class
    )
    public CodeBlock generatePoJo(ModelElement me, String templateName) {
        MClass mc = (MClass)me;
        JavaCodeBlock jCB = new JavaCodeBlock("src/main/java", mc.getPackage().getFQName(), mc.getName() );
        jCB.addCodeBlock(
                new CodeTargetCodeBlockAdapter(
                        new ClassFrameTargetCreator(mc).createPoJoTarget()
                )
        );
        return jCB;
    }

    /**
     * <p>
     *     The implementation of the base class generator. It generates a complete
     *     class conaining all attributes and setter / getter methods from the model
     *     class. This class is the base class for the template generated it the
     *     second generator.
     * </p>
     * <p>
     *     The implementation uses the PoJoCodeTargetCreator which creates a CodeTarget
     *     that contains all necessary code for a PoJo with attributes, relations, extensions
     *     etc.
     * </p>
     * @param me
     * @param templateName
     * @return
     */
    @CGV19Generator(
            requiredStereotype = "PoJoBase",
            outputType = OutputType.JAVA,
            outputTo = OutputTo.SRC_GEN,
            operatesOn = MClass.class
    )
    public CodeBlock generatePoJoBase(ModelElement me, String templateName) {
        MClass mc = (MClass)me;
        JavaCodeBlock jCB = new JavaCodeBlock("src/main/java-gen", mc.getPackage().getFQName(), mc.getName() );
        jCB.addCodeBlock(
                new CodeTargetCodeBlockAdapter(
                        new PoJoCodeTargetCreator(mc).createPoJoTarget()
                )
        );
        return jCB;
    }
}
