package de.spraener.nxtgen.aicb;

import de.spraener.nxtgen.CodeBlock;
import de.spraener.nxtgen.GroovyCodeBlockImpl;
import de.spraener.nxtgen.annotations.CGV19Component;
import de.spraener.nxtgen.annotations.CGV19Generator;
import de.spraener.nxtgen.annotations.OutputTo;
import de.spraener.nxtgen.annotations.OutputType;
import de.spraener.nxtgen.java.JavaCodeBlock;
import de.spraener.nxtgen.model.ModelElement;
import de.spraener.nxtgen.oom.model.MClass;

@CGV19Component
public class AiCodeBlockGenerator {

    @CGV19Generator(
            requiredStereotype = "PoJo",
            operatesOn = MClass.class,
            outputType = OutputType.JAVA,
            outputTo = OutputTo.SRC_GEN
    )
    public CodeBlock generateWithAiCodeBlock(ModelElement me) {
        MClass mc = (MClass)me;
        GroovyCodeBlockImpl gcb = new GroovyCodeBlockImpl("ai-pojo", me, "/AiPoJoTemplate.groovy");
        JavaCodeBlock jcb = new JavaCodeBlock("src/main/java-gen", mc.getPackage().getFQName(), me.getName());
        jcb.addCodeBlock(gcb);
        return jcb;
    }
}
