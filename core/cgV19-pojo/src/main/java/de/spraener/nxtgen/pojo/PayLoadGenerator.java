package de.spraener.nxtgen.pojo;

import de.spraener.nxtgen.CodeBlock;
import de.spraener.nxtgen.CodeGenerator;
import de.spraener.nxtgen.ProtectionStrategie;
import de.spraener.nxtgen.filestrategies.TypeScriptFileStrategy;
import de.spraener.nxtgen.java.JavaCodeBlock;
import de.spraener.nxtgen.model.ModelElement;
import de.spraener.nxtgen.model.impl.ModelElementImpl;

public class PayLoadGenerator implements CodeGenerator {
    @Override
    public CodeBlock resolve(ModelElement element, String templateName) {
        JavaCodeBlock jCB = new JavaCodeBlock("src/main/typescript", "", element.getName() );
        jCB.setToFileStrategy(new TypeScriptFileStrategy("src/main/typescript-gen", element.getName()));
        generatePayload(jCB,(ModelElementImpl)element);
        return jCB;
    }

    private void generatePayload(CodeBlock cb, ModelElementImpl mc) {
        cb.println("//"+ ProtectionStrategie.GENERATED_LINE);
        cb.println("");
        cb.println("export class "+mc.getName()+" {");
        mc.getChilds().stream().filter(c -> "mAttribute".equals(c.getMetaType())).forEach(
                c -> generateField(cb, mc, c)
        );
        cb.println("}");
    }

    private void generateField(CodeBlock cb, ModelElementImpl mc, ModelElement c) {
        ModelElementImpl attr = (ModelElementImpl) c;
        cb.println("    "+attr.getName()+": "+toTSType(attr));
    }

    private String toTSType(ModelElementImpl attr) {
        String type = attr.getProperty("type");
        String tsType = "string";
        if( type.equalsIgnoreCase("int")) {
            tsType = "number";
        }
        if( type.equals("java.util.Date")) {
            tsType = "Date";
        }
        return tsType;
    }
}
