package de.spraener.nxtgen.jp;

import com.squareup.javapoet.TypeSpec;
import de.spraener.nxtgen.java.JavaCodeBlock;
import de.spraener.nxtgen.oom.model.MClass;

public class JavaPoetRootCodeBlock extends JavaCodeBlock {
    private TypeSpec.Builder typeSpecBuilder;

    public JavaPoetRootCodeBlock(String srcDir, TypeSpec.Builder typeSpec, MClass mc) {
        super(srcDir, mc.getPackage()!=null?mc.getPackage().getFQName() : "", mc.getName());
        this.typeSpecBuilder = typeSpec;
    }

    @Override
    public String toCode() {
        return this.typeSpecBuilder.build().toString();
    }
}
