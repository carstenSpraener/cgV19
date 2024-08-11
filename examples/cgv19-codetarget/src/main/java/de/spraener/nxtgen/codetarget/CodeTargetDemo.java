package de.spraener.nxtgen.codetarget;

import de.spraener.nxtgen.CodeBlock;
import de.spraener.nxtgen.annotations.CGV19Component;
import de.spraener.nxtgen.annotations.CGV19Generator;
import de.spraener.nxtgen.annotations.OutputTo;
import de.spraener.nxtgen.annotations.OutputType;
import de.spraener.nxtgen.java.JavaCodeBlock;
import de.spraener.nxtgen.model.ModelElement;
import de.spraener.nxtgen.oom.model.MClass;
import de.spraener.nxtgen.pojo.ClassFrameTargetCreator;
import de.spraener.nxtgen.pojo.PoJoCodeTargetCreator;
import de.spraener.nxtgen.target.CodeTarget;
import de.spraener.nxtgen.target.CodeTargetCodeBlockAdapter;
import de.spraener.nxtgen.target.SingleLineSnippet;
import de.spraener.nxtgen.target.java.JavaSections;

import java.util.logging.Logger;

@CGV19Component
public class CodeTargetDemo {
    private static final Logger LOGGER = Logger.getLogger(CodeTargetDemo.class.getName());

    @CGV19Generator(
            requiredStereotype = "PoJo",
            operatesOn = MClass.class,
            outputTo = OutputTo.SRC_GEN,
            outputType = OutputType.JAVA
    )
    public CodeBlock generateViaCodeTarget(ModelElement me, String templateName) {
        MClass mc = (MClass) me;
        // create a starting point for your extensions
        CodeTarget clazzTarget = new PoJoCodeTargetCreator(mc).createPoJoTarget();

        // apply your enhancements to the code target
        JavaLoggerAdding.addJavaLogging(clazzTarget, mc);

        // wrap the target into a CodeBlock and add it to a JavaCodeBlock to write
        // it as a java class.
        JavaCodeBlock jCB = new JavaCodeBlock("src/main/java-gen", mc.getPackage().getFQName(), mc.getName());
        jCB.addCodeBlock(
                new CodeTargetCodeBlockAdapter(clazzTarget)
        );
        return jCB;
    }
}
