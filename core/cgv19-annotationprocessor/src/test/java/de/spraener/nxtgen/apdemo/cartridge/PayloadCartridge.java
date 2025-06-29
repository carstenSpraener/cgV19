package de.spraener.nxtgen.apdemo.cartridge;

import de.spraener.nxtgen.CodeBlock;
import de.spraener.nxtgen.GroovyCodeBlockImpl;
import de.spraener.nxtgen.annotations.*;
import de.spraener.nxtgen.cartridge.rest.annotations.PayLoad;
import de.spraener.nxtgen.cartridges.AnnotatedCartridgeImpl;
import de.spraener.nxtgen.java.JavaCodeBlock;
import de.spraener.nxtgen.model.ModelElement;
import de.spraener.nxtgen.oom.model.MClass;
import de.spraener.nxtgen.target.CodeTargetCodeBlockAdapter;
import de.spreaner.nxtgen.annoproc.APModelElementImpl;

import java.util.Arrays;
import java.util.List;

@CGV19Cartridge("PayloadCartridge")
@CGV19Component
public class PayloadCartridge extends AnnotatedCartridgeImpl {

    public PayloadCartridge() {
        super();
    }

    @CGV19Generator(
            requiredStereotype = "PayLoad",
            operatesOn = APModelElementImpl.class,
            outputType = OutputType.JAVA,
            outputTo = OutputTo.SRC_GEN,
            templateName = "/payload/Payload.groovy",
            implementationKind = ImplementationKind.GROOVY_TEMPLATE
    )
    public CodeBlock generatePayload(ModelElement element, String templateName) {
        APModelElementImpl mc = (APModelElementImpl)element;
        mc.setProperty("srcClassName", mc.getName());
        mc.setName(mc.getName()+"PayLoader");
        JavaCodeBlock jCB = new JavaCodeBlock("src/main/java-gen", toPackageName(mc), mc.getName() );
        jCB.addCodeBlock(new GroovyCodeBlockImpl("payload", element, "/payload/PayLoad.groovy"));
        return jCB;
    }

    public static String toPackageName(APModelElementImpl mc) {
        ModelElement parent = mc.getParent();
        String pkgName = "";
        while( parent!=null ) {
            pkgName = parent.getName()+"."+pkgName;
            parent = parent.getParent();
        }
        if( pkgName.endsWith(".") ) {
            pkgName = pkgName.substring(0, pkgName.length()-1);
        }
        return pkgName;
    }

    @Override
    public List<String> getAnnotationTypes() {
        return Arrays.asList(new String[]{PayLoad.class.getName()});
    }
}
