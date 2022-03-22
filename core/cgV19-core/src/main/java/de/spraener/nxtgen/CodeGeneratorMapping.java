package de.spraener.nxtgen;

import de.spraener.nxtgen.model.ModelElement;

public class CodeGeneratorMapping {
    private ModelElement generatorBaseElement;
    private CodeGenerator codeGen;

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

    public CodeGenerator getCodeGen() {
        return codeGen;
    }
}
