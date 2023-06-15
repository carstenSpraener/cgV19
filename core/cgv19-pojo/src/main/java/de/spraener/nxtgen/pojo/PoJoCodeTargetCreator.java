package de.spraener.nxtgen.pojo;

import de.spraener.nxtgen.ProtectionStrategieDefaultImpl;
import de.spraener.nxtgen.oom.StereotypeHelper;
import de.spraener.nxtgen.oom.model.MClass;
import de.spraener.nxtgen.oom.model.MDependency;
import de.spraener.nxtgen.oom.model.OOModel;
import de.spraener.nxtgen.target.CodeBlockSnippet;
import de.spraener.nxtgen.target.CodeTarget;
import de.spraener.nxtgen.target.SingleLineSnippet;
import de.spraener.nxtgen.target.java.JavaSections;

public class PoJoCodeTargetCreator {
    public static final String POJO_ASPECT = "pojo-frame";
    public static final String DEFAULT_CONSTRUCTOR = "pojo-default-constructor";
    public static final String ATTRIBUTE_ASPECT = "pojo-attribute";
    public static final String ASSOCIATION = "pojo-association";
    MClass pojo;

    public PoJoCodeTargetCreator(MClass pojo) {
        this.pojo = pojo;
    }

    public CodeTarget createPoJoTarget() {
        CodeTarget target = JavaSections.createJavaCodeTarget("//"+ ProtectionStrategieDefaultImpl.GENERATED_LINE);
        target.inContext(POJO_ASPECT, pojo, t->{
            target.getSection(JavaSections.HEADER).add(new SingleLineSnippet("package "+pojo.getPackage().getName()+";"));
            target.getSection(JavaSections.CLASS_DECLARATION)
                    .add(new CodeBlockSnippet("public class "+pojo.getName()+" "));
            if( pojo.getInheritsFrom()!=null ) {
                MClass superClass = pojo.getInheritsFrom().getMClass(pojo.getModel());
                target.getSection(JavaSections.EXTENDS)
                        .add(new CodeBlockSnippet("extends "+superClass.getName()));
                target.getSection(JavaSections.IMPORTS)
                        .add(new SingleLineSnippet("import "+superClass.getFQName()+";"));
            }
            if( pojo.getDependencies()!=null ) {
                for(MDependency dependency : pojo.getDependencies() ) {
                    if(StereotypeHelper.hasStereotye(dependency, "Implements")) {
                        MClass depTarget = (MClass)((OOModel)pojo.getModel()).findClassByName(dependency.getTarget());
                        addImplements(target, depTarget);
                    }
                }
            }
            createConstructor(target, pojo);
            new PoJoAttributesCreator().accept(target, pojo);
            new PoJoAssociationCreator().accept(target, pojo);
        });
        return target;
    }

    protected void createConstructor(CodeTarget target, MClass pojo) {
        target.inContext(DEFAULT_CONSTRUCTOR, pojo, t -> {
            StringBuilder sb = new StringBuilder();
            sb.append( "    public "+pojo.getName()+"() {\n");
            sb.append( "    }\n");
            t.getSection(JavaSections.CONSTRUCTORS)
                    .add(new CodeBlockSnippet(sb.toString()));
        });
    }

    protected void addImplements(CodeTarget target, MClass depTarget) {
        target.getSection(JavaSections.IMPLEMENTS)
                .add(new SingleLineSnippet(depTarget.getName()));
        if( !depTarget.getPackage().getFQName().equals(pojo.getPackage().getFQName()) ) {
            target.getSection(JavaSections.IMPLEMENTS)
                    .add(new SingleLineSnippet("import "+depTarget.getFQName()+";"));
        }
    }

}
