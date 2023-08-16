package de.spraener.nxtgen;

import de.spraener.nxtgen.model.ModelElement;

import java.util.Objects;

public class CodeGeneratorMapping {
    private ModelElement generatorBaseElement;
    private CodeGenerator codeGen;
    private String stereoptype = null;

    private CodeGeneratorMapping(ModelElement me, CodeGenerator cg) {
        this.generatorBaseElement = me;
        this.codeGen = cg;
    }

    public static CodeGeneratorMapping create(ModelElement me, CodeGenerator cg ) {
        return new CodeGeneratorMapping(me,cg);
    }

    public ModelElement getGeneratorBaseELement() {
        return generatorBaseElement;
    }

    public void setStereotype(String stereptype) {
        this.stereoptype = stereptype;
    }

    public CodeGenerator getCodeGen() {
        return codeGen;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof CodeGeneratorMapping that)) return false;
        return Objects.equals(generatorBaseElement, that.generatorBaseElement) &&
                Objects.equals(codeGen, that.codeGen) &&
                Objects.equals(stereoptype, that.stereoptype);
    }

    @Override
    public int hashCode() {
        return Objects.hash(generatorBaseElement, codeGen, stereoptype);
    }
}
