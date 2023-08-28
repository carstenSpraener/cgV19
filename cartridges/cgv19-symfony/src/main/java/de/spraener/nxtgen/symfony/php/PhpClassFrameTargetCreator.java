package de.spraener.nxtgen.symfony.php;

import de.spraener.nxtgen.ProtectionStrategieDefaultImpl;
import de.spraener.nxtgen.oom.StereotypeHelper;
import de.spraener.nxtgen.oom.model.MClass;
import de.spraener.nxtgen.oom.model.MDependency;
import de.spraener.nxtgen.oom.model.OOModel;
import de.spraener.nxtgen.target.CodeBlockSnippet;
import de.spraener.nxtgen.target.CodeSection;
import de.spraener.nxtgen.target.CodeTarget;
import de.spraener.nxtgen.target.SingleLineSnippet;

import static de.spraener.nxtgen.symfony.php.PhpCodeHelper.toImportStatement;
import static de.spraener.nxtgen.symfony.php.PhpCodeHelper.toNameSpace;

public class PhpClassFrameTargetCreator {
    private MClass mClass = null;
    public static final String CLAZZ_FRAME = "clazz-frame";
    public static final String DEFAULT_CONSTRUCTOR = "clazz-default-constructor";

    public PhpClassFrameTargetCreator(MClass mc) {
        this.mClass = mc;
    }

    public CodeTarget createPhoClassTarget() {
        CodeTarget target = PhpSections.createCodeTarget("<?php\n//" + ProtectionStrategieDefaultImpl.GENERATED_LINE);
        target.inContext(CLAZZ_FRAME, mClass,
                this::declareNamespace,
                this::declareClazz,
                this::declareExtends,
                this::declareImplements,
                this::createConstructor
        );
        return target;
    }

    protected void createConstructor(CodeTarget target) {
        target.inContext(DEFAULT_CONSTRUCTOR, mClass, t -> {
            StringBuilder sb = new StringBuilder();
            sb.append("    public function __construct() {\n");
            sb.append("    }");
            t.getSection(PhpSections.CONSTRUCTORS)
                    .add(new CodeBlockSnippet(sb.toString()));
        });
    }

    protected CodeSection declareNamespace(CodeTarget t) {
        return t.getSection(PhpSections.HEADER).add(
                new SingleLineSnippet("namespace " + toNameSpace(mClass) + ";")
        );
    }

    protected CodeSection declareClazz(CodeTarget t ) {
        return t.getSection(PhpSections.CLASS_DECLARATION)
                .add(new CodeBlockSnippet("class " + mClass.getName() + " "));
    }

    protected void declareExtends(CodeTarget t) {
        if (mClass.getInheritsFrom() != null) {
            MClass superClass = mClass.getInheritsFrom().getMClass(mClass.getModel());
            t.getSection(PhpSections.EXTENDS)
                    .add(new CodeBlockSnippet("extends " + superClass.getName()));
            if( !superClass.getPackage().getName().equals(mClass.getPackage().getName()) ) {
                t.getSection(PhpSections.IMPORTS)
                        .add(new SingleLineSnippet(toImportStatement(superClass)));
            }
        }
    }

    protected void declareImplements(CodeTarget target) {
        if (mClass.getDependencies() != null) {
            for (MDependency dependency : mClass.getDependencies()) {
                if (StereotypeHelper.hasStereotype(dependency, "Implements")) {
                    MClass depTarget = (MClass) ((OOModel) mClass.getModel()).findClassByName(dependency.getTarget());
                    addImplements(target, depTarget);
                }
            }
        }
    }

    protected void addImplements(CodeTarget target, MClass depTarget) {
        target.getSection(PhpSections.IMPLEMENTS)
                .add(new SingleLineSnippet(depTarget.getName()));
        if (!depTarget.getPackage().getFQName().equals(mClass.getPackage().getFQName())) {
            target.getSection(PhpSections.IMPORTS)
                    .add(new SingleLineSnippet(toImportStatement(depTarget)));
        }
    }

}
