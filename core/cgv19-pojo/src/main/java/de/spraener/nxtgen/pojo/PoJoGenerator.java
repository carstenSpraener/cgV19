package de.spraener.nxtgen.pojo;

import de.spraener.nxtgen.CodeBlock;
import de.spraener.nxtgen.annotations.*;
import de.spraener.nxtgen.java.JavaCodeBlock;
import de.spraener.nxtgen.model.ModelElement;
import de.spraener.nxtgen.model.impl.StereotypeImpl;
import de.spraener.nxtgen.oom.cartridge.GeneratorGapTransformation;
import de.spraener.nxtgen.oom.model.MClass;
import de.spraener.nxtgen.oom.model.MClassRef;
import de.spraener.nxtgen.target.CodeTargetCodeBlockAdapter;

@CGV19Component
public class PoJoGenerator {
    public static final String ORIGINAL_CLASS="originalClass";

    public static MClass getOriginalClass(MClass mClass) {
        return (MClass)mClass.getObject(ORIGINAL_CLASS);
    }

    public static void setOriginalClass(MClass pojo, MClass originalClass) {
        pojo.putObject(ORIGINAL_CLASS, originalClass);
    }

    @CGV19Transformation(
            requiredStereotype = PoJoCartridge.ST_POJO,
            operatesOn = MClass.class
    )
    public void doTransformation(ModelElement me) {
        new GeneratorGapTransformation().doTransformation(me);
    }

    @CGV19Generator(
            requiredStereotype = "PoJoBase",
            outputTo = OutputTo.SRC,
            outputType = OutputType.JAVA,
            operatesOn = MClass.class,
            implementationKind = ImplementationKind.CLASS_TARGET
    )
    public CodeBlock generatePoJoBase(ModelElement element, String templateName) {
        MClass mc = (MClass)element;
        JavaCodeBlock jCB = new JavaCodeBlock("src/main/java-gen", mc.getPackage().getFQName(), mc.getName() );
        jCB.addCodeBlock(new CodeTargetCodeBlockAdapter(new PoJoCodeTargetCreator(mc).createPoJoTarget()));
        return jCB;
    }

    @CGV19Generator(
            requiredStereotype = "PoJo",
            outputTo = OutputTo.SRC_GEN,
            outputType = OutputType.JAVA,
            operatesOn = MClass.class,
            implementationKind = ImplementationKind.CLASS_TARGET
    )
    public CodeBlock generatePoJo(ModelElement element, String templateName) {
        MClass mc = (MClass)element;
        JavaCodeBlock jCB = new JavaCodeBlock("src/main/java", mc.getPackage().getFQName(), mc.getName() );
        jCB.addCodeBlock( new CodeTargetCodeBlockAdapter(new ClassFrameTargetCreator(mc).createPoJoTarget()));
        return jCB;
    }
}
