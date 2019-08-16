package de.spraener.nxtgen.oom.cartridge;

import de.spraener.nxtgen.oom.model.MClass;
import de.spraener.nxtgen.CodeBlock;
import de.spraener.nxtgen.CodeGenerator;
import de.spraener.nxtgen.java.JavaCodeBlock;
import de.spraener.nxtgen.model.ModelElement;

public class OOMGenerator implements CodeGenerator {
    @Override
    public CodeBlock resolve(ModelElement element, String templateName) {
        MClass mClass = (MClass)element;
        String pkgName = OOMHelper.findPackage(element);
        String clzzName = element.getName();
        String implementStr = JavaHelper.getImplementsStr(element);
        String extendStr = JavaHelper.getExtendsStr(element);
        JavaCodeBlock jCB = new JavaCodeBlock("./src/main/java-gen", pkgName, element.getName());

        jCB.println("// THIS FILE IS GENERATED AS LONG AS THIS LINE EXISTS");
        jCB.println("package "+pkgName+";");
        jCB.println("");
        jCB.println("import java.util.List;");
        jCB.println("import de.spraener.nxtgen.model.*;");
        jCB.println("import de.spraener.nxtgen.model.impl.*;");
        jCB.println("");
        jCB.println("");
        jCB.println("public class "+clzzName+implementStr+extendStr+" {");
        // jCB.println("${attributeDefinition}");
        generateAttributeDefinition(jCB, mClass);
        jCB.println("");
        jCB.println("    public "+clzzName+"() {");
        jCB.println("    }");
        jCB.println("");
        generateAttributeAccessMethods(jCB,mClass);
        jCB.println("");
        jCB.println("}");
        return jCB;
    }

    public static void generateAttributeDefinition(CodeBlock cb, MClass mc) {
        mc.getAttributes().forEach( attr -> {
            cb.println(JavaHelper.printAttributeDefinition(attr));
        });
    }

    public void generateAttributeAccessMethods(CodeBlock cb, MClass mc) {
        mc.getAttributes().forEach( attr -> {
            cb.println(JavaHelper.printAttributeAccessMethods(attr));
        });

    }
}
