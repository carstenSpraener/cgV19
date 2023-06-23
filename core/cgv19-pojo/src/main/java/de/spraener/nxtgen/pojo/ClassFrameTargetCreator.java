package de.spraener.nxtgen.pojo;

import de.spraener.nxtgen.ProtectionStrategieDefaultImpl;
import de.spraener.nxtgen.oom.StereotypeHelper;
import de.spraener.nxtgen.oom.model.MClass;
import de.spraener.nxtgen.oom.model.MDependency;
import de.spraener.nxtgen.oom.model.OOModel;
import de.spraener.nxtgen.target.CodeBlockSnippet;
import de.spraener.nxtgen.target.CodeSection;
import de.spraener.nxtgen.target.CodeTarget;
import de.spraener.nxtgen.target.SingleLineSnippet;
import de.spraener.nxtgen.target.java.JavaSections;

public class ClassFrameTargetCreator {
    private MClass mClass = null;
    public static final String CLAZZ_FRAME = "clazz-frame";
    public static final String DEFAULT_CONSTRUCTOR = "clazz-default-constructor";

    public ClassFrameTargetCreator(MClass mc) {
        this.mClass = mc;
    }

    public CodeTarget createPoJoTarget() {
        CodeTarget target = JavaSections.createJavaCodeTarget("//" + ProtectionStrategieDefaultImpl.GENERATED_LINE);
        target.inContext(CLAZZ_FRAME, mClass,
                this::declarePackage,
                this::declareClazz,
                this::declareExtends,
                this::declareImplements,
                this::createConstructor
        );
        return target;
    }


    private void declareImplements(CodeTarget target) {
        if (mClass.getDependencies() != null) {
            for (MDependency dependency : mClass.getDependencies()) {
                if (StereotypeHelper.hasStereotype(dependency, "Implements")) {
                    MClass depTarget = (MClass) ((OOModel) mClass.getModel()).findClassByName(dependency.getTarget());
                    addImplements(target, depTarget);
                }
            }
        }
    }

    private void declareExtends(CodeTarget t) {
        if (mClass.getInheritsFrom() != null) {
            MClass superClass = mClass.getInheritsFrom().getMClass(mClass.getModel());
            t.getSection(JavaSections.EXTENDS)
                    .add(new CodeBlockSnippet("extends " + superClass.getName()));
            if( !superClass.getPackage().getName().equals(mClass.getPackage().getName()) ) {
                t.getSection(JavaSections.IMPORTS)
                        .add(new SingleLineSnippet("import " + superClass.getFQName() + ";"));
            }
        }
    }

    private CodeSection declarePackage(CodeTarget t) {
        return t.getSection(JavaSections.HEADER).add(new SingleLineSnippet("package " + mClass.getPackage().getFQName() + ";"));
    }

    private void declareClazz(CodeTarget t) {
        t.getSection(JavaSections.CLASS_DECLARATION)
                .add(new CodeBlockSnippet("public class " + mClass.getName() + " "));
    }

    protected void createConstructor(CodeTarget target) {
        target.inContext(DEFAULT_CONSTRUCTOR, mClass, t -> {
            StringBuilder sb = new StringBuilder();
            sb.append("    public " + mClass.getName() + "() {\n");
            sb.append("        super();\n");
            sb.append("    }");
            t.getSection(JavaSections.CONSTRUCTORS)
                    .add(new CodeBlockSnippet(sb.toString()));
        });
    }

    protected void addImplements(CodeTarget target, MClass depTarget) {
        target.getSection(JavaSections.IMPLEMENTS)
                .add(new SingleLineSnippet(depTarget.getName()));
        if (!depTarget.getPackage().getFQName().equals(mClass.getPackage().getFQName())) {
            target.getSection(JavaSections.IMPLEMENTS)
                    .add(new SingleLineSnippet("import " + depTarget.getFQName() + ";"));
        }
    }
}
