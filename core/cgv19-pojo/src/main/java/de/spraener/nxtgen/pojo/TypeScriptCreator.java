package de.spraener.nxtgen.pojo;

import de.spraener.nxtgen.ProtectionStrategieDefaultImpl;
import de.spraener.nxtgen.oom.model.MClass;
import de.spraener.nxtgen.target.CodeBlockSnippet;
import de.spraener.nxtgen.target.CodeTarget;
import de.spraener.nxtgen.target.typescript.TypeScriptSections;

public class TypeScriptCreator {
    public static final String TS_CLASS_FRAME = "tsclass-frame";
    public static final Object TS_ATTRIBUTES = "ts-attributes";

    private MClass mClass;

    public TypeScriptCreator(MClass mClass) {
        this.mClass = mClass;
    }

    public CodeTarget createPoJo() {
        CodeTarget codeTarget = TypeScriptSections.createCodeTarget("// "+ ProtectionStrategieDefaultImpl.GENERATED_LINE);

        codeTarget.inContext(TS_CLASS_FRAME, mClass,
                this::classDeclaration,
                new TypeScriptAttributes(mClass)
        );
        return codeTarget;
    }

    protected void classDeclaration(CodeTarget target) {
        target.getSection(TypeScriptSections.DECLARATION)
                .add(new CodeBlockSnippet("export class "+mClass.getName()));
    }
}
