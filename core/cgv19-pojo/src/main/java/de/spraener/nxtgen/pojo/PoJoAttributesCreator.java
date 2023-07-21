package de.spraener.nxtgen.pojo;


import de.spraener.nxtgen.oom.cartridge.JavaHelper;
import de.spraener.nxtgen.oom.model.MAttribute;
import de.spraener.nxtgen.oom.model.MClass;
import de.spraener.nxtgen.target.CodeBlockSnippet;
import de.spraener.nxtgen.target.CodeTarget;
import de.spraener.nxtgen.target.SingleLineSnippet;
import de.spraener.nxtgen.target.java.JavaSections;

import java.util.HashMap;
import java.util.Map;
import java.util.function.BiConsumer;

public class PoJoAttributesCreator implements BiConsumer<CodeTarget, MClass> {
    public void accept(CodeTarget target, MClass pojo) {
        for (var attr : pojo.getAttributes()) {
            target.inContext(PoJoCodeTargetCreator.ATTRIBUTE_ASPECT, attr, t -> addAttribute(t, attr));
        }
    }

    private void addAttribute(CodeTarget target, MAttribute attr) {
        Map<String,String> varMap = new HashMap<>();
        varMap.put("attr.type", attr.getType());
        varMap.put("attr.name", attr.getName());
        varMap.put("type", attr.getType());
        varMap.put("accessName", JavaHelper.firstToUpperCase(attr.getName()));
        varMap.put("field", attr.getName());

        target.getSection(JavaSections.ATTRIBUTE_DECLARATIONS)
                .add(new SingleLineSnippet(StringEvaluator.evaluate("    private ${attr.type} ${attr.name};", varMap)));

        if (!attr.getType().startsWith("java.lang") && attr.getType().indexOf('.') >= 0) {
            target.getSection(JavaSections.IMPORTS)
                    .add(new SingleLineSnippet(StringEvaluator.evaluate("import ${attr.type};", varMap)));
        }

        target.getSection(JavaSections.METHODS)
                .add(new CodeBlockSnippet("attribute", attr, StringEvaluator.evaluate("""
                
                    public ${type} get${accessName}() {
                        return ${field};
                    }
                
                """, varMap)
                ));
        target.getSection(JavaSections.METHODS)
                .add(new CodeBlockSnippet("attribute", attr, StringEvaluator.evaluate("""
                
                    public void set${accessName}( ${type} value) {
                        this.${field} = value;
                    }
                
                """, varMap))
                );
    }
}
