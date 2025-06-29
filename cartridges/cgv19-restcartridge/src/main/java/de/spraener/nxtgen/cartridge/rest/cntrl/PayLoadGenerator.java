package de.spraener.nxtgen.cartridge.rest.cntrl;

import de.spraener.nxtgen.CodeBlock;
import de.spraener.nxtgen.annotations.CGV19Component;
import de.spraener.nxtgen.annotations.CGV19Generator;
import de.spraener.nxtgen.annotations.OutputTo;
import de.spraener.nxtgen.annotations.OutputType;
import de.spraener.nxtgen.java.JavaCodeBlock;
import de.spraener.nxtgen.model.ModelElement;
import de.spraener.nxtgen.oom.model.MClass;
import de.spraener.nxtgen.pojo.PoJoCodeTargetCreator;
import de.spraener.nxtgen.target.CodeTargetCodeBlockAdapter;

import javax.management.relation.RelationServiceNotRegisteredException;

@CGV19Component
public class PayLoadGenerator {

    @CGV19Generator(
            requiredStereotype = "PayLoad",
            outputType = OutputType.JAVA,
            outputTo = OutputTo.SRC_GEN,
            operatesOn = MClass.class
    )
    public CodeBlock generatePayLoadBase(ModelElement element, String templateName) {
        MClass mc = (MClass)element;
        JavaCodeBlock jCB = new JavaCodeBlock("src/main/java-gen", mc.getPackage().getFQName(), mc.getName() );
        jCB.addCodeBlock(new CodeTargetCodeBlockAdapter(new PoJoCodeTargetCreator(mc).createPoJoTarget()));
        return jCB;
    }
}
