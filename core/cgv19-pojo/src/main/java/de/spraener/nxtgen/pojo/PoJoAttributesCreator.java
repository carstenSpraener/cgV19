package de.spraener.nxtgen.pojo;

import de.spraener.nxtgen.oom.cartridge.JavaHelper;
import de.spraener.nxtgen.oom.model.MAttribute;
import de.spraener.nxtgen.oom.model.MClass;
import de.spraener.nxtgen.target.CodeBlockSnippet;
import de.spraener.nxtgen.target.CodeTarget;
import de.spraener.nxtgen.target.SingleLineSnippet;
import de.spraener.nxtgen.target.java.JavaSections;

import java.util.function.BiConsumer;

public class PoJoAttributesCreator implements BiConsumer<CodeTarget, MClass> {

    @Override
    public void accept(CodeTarget target, MClass pojo) {
        for( MAttribute attr : pojo.getAttributes() ) {
            target.inContext( PoJoCodeTargetCreator.ATTRIBUTE_ASPECT, attr, t -> {
                addAttribute(t, attr);
            });
        }
    }

    private void addAttribute(CodeTarget target, MAttribute attr) {
        target.getSection(JavaSections.ATTRIBUTE_DECLARATIONS)
                .add( new SingleLineSnippet("    private "+attr.getType()+" "+attr.getName()+";"));
        if(!attr.getType().startsWith("java.lang") && attr.getType().indexOf('.')>=0 ) {
            target.getSection(JavaSections.IMPORTS)
                    .add(new SingleLineSnippet("import "+attr.getType()+";"));
        }
        String type = attr.getType();
        String accessName = JavaHelper.firstToUpperCase(attr.getName());
        String field = attr.getName();

        StringBuilder getterSB = new StringBuilder();
        getterSB.append("    public "+type+" get"+accessName+"() {\n");
        getterSB.append("        return "+field+";\n");
        getterSB.append("    }\n");
        getterSB.append("\n");
        target.getSection(JavaSections.METHODS)
                .add(new CodeBlockSnippet("attribute", attr, getterSB.toString()));

        StringBuilder setterSB = new StringBuilder();
        setterSB.append("    public void set"+accessName+"( "+type+" value) {\n");
        setterSB.append("        this."+field+" = value;\n");
        setterSB.append("    }\n");
        setterSB.append("\n");
        target.getSection(JavaSections.METHODS)
                .add(new CodeBlockSnippet("attribute", attr, setterSB.toString()));
    }

}
