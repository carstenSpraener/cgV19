package de.spraener.cgv19.webtest;

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
public class WebTestGenerators {

    @CGV19Generator(
         operatesOn = MClass.class,
         requiredStereotype = "FormBean",
         outputTo = OutputTo.SRC_GEN,
         outputType = OutputType.JAVA
    )
    public CodeBlock generateFormBean(ModelElement me, String templateName) {
        MClass mc = (MClass)me;
        GroovyCodeBlockImpl gcb = new GroovyCodeBlockImpl("FormBeanGenerator", me, "/FormBean.groovy");
        JavaCodeBlock jcb = new JavaCodeBlock("src/main/java-gen", mc.getPackage().getFQName(), me.getName());
        jcb.addCodeBlock(gcb);
        return jcb;
    }


    @CGV19Generator(
            operatesOn = MClass.class,
            requiredStereotype = "SelectOptionEnum",
            outputTo = OutputTo.SRC_GEN,
            outputType = OutputType.JAVA
    )
    public CodeBlock generateSelectOptionEnum(ModelElement me, String templateName) {
        MClass mc = (MClass)me;
        GroovyCodeBlockImpl gcb = new GroovyCodeBlockImpl("SelectOptionEnum", me, "/SelectOptionEnum.groovy");
        JavaCodeBlock jcb = new JavaCodeBlock("src/main/java-gen", mc.getPackage().getFQName(), me.getName());
        jcb.addCodeBlock(gcb);
        return jcb;
    }
}
