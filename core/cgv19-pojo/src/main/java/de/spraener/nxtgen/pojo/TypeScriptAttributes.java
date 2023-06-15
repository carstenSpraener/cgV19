package de.spraener.nxtgen.pojo;

import de.spraener.nxtgen.oom.model.MAttribute;
import de.spraener.nxtgen.oom.model.MClass;
import de.spraener.nxtgen.target.CodeTarget;
import de.spraener.nxtgen.target.SingleLineSnippet;
import de.spraener.nxtgen.target.typescript.TypeScriptSections;

import java.util.function.Consumer;

public class TypeScriptAttributes implements Consumer<CodeTarget> {
    private MClass mClass;

    public TypeScriptAttributes(MClass mClass) {
        this.mClass = mClass;
    }

    @Override
    public void accept(CodeTarget target) {
        for(MAttribute attr : mClass.getAttributes() ) {
            target.inContext(TypeScriptCreator.TS_ATTRIBUTES, attr, t -> {
                declareAttribute(t, attr);
            });
        }
    }

    protected void declareAttribute(CodeTarget t, MAttribute attr) {
        String name = attr.getName();
        String type = toTsType(attr.getType());
        t.getSection(TypeScriptSections.ATTRIBUTES)
                .add(new SingleLineSnippet("    "+name+": "+type+" | undefined"));
    }

    private String toTsType(String type) {
        if( type.contains(".") ) {
            return type.substring(type.lastIndexOf('.')+1);
        }
        switch( type.toLowerCase() ) {
            case "string":
                return "string";
            case "int":
            case "integer":
            case "long":
            case "double":
            case "float":
                return "number";
            case "boolean":
                return "boolean";
            default:
                return "string";
        }
    }
}
