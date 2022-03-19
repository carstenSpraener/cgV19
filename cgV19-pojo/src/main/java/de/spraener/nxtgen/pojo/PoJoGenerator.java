package de.spraener.nxtgen.pojo;

import de.spraener.nxtgen.CodeBlock;
import de.spraener.nxtgen.CodeGenerator;
import de.spraener.nxtgen.ProtectionStrategie;
import de.spraener.nxtgen.java.JavaCodeBlock;
import de.spraener.nxtgen.model.ModelElement;
import de.spraener.nxtgen.oom.model.JavaHelper;
import de.spraener.nxtgen.oom.model.MAttribute;
import de.spraener.nxtgen.oom.model.MClass;

public class PoJoGenerator implements CodeGenerator {
    @Override
    public CodeBlock resolve(ModelElement element, String templateName) {
        MClass mc = (MClass)element;
        JavaCodeBlock jCB = new JavaCodeBlock("src/main/java-gen", mc.getPackage().getFQName(), mc.getName() );
        generatePoJo(jCB,mc);
        return jCB;
    }

    private void generatePoJo(CodeBlock cb, MClass mc) {
        cb.println("// "+ ProtectionStrategie.GENERATED_LINE);
        cb.println("package "+mc.getPackage().getFQName()+";");
        cb.println("");
        cb.println("public class "+mc.getName()+" {");
        cb.println("");
        generateAttributeDefinitions(cb, mc);
        cb.println("");
        cb.println("    public "+mc.getName()+"() {");
        cb.println("        super();");
        cb.println("    }");
        cb.println("");
        generateAttributeAccessMethods(cb,mc);
        cb.println("");
        cb.println("}");
    }

    private void generateAttributeDefinitions(CodeBlock cb, MClass mc) {
        for(MAttribute a : mc.getAttributes() ) {
            String aType = a.getType();
            String aName = a.getName();
            cb.println( "    private "+aType+" "+aName+";");
        }
    }


    private void generateAttributeAccessMethods(CodeBlock cb, MClass mc) {
        for(MAttribute a : mc.getAttributes() ) {
            String aType = a.getType();
            String aName = a.getName();
            String methodName = JavaHelper.toPorpertyName(a);
            cb.println( "    public "+aType+" get"+methodName+"() {");
            cb.println( "        return this."+aName+";");
            cb.println( "    }");
            cb.println("");
            cb.println( "    public void set"+methodName+"( "+aType+" value) {");
            cb.println( "        this."+aName+" = value;");
            cb.println( "    }");
        }
    }

}
