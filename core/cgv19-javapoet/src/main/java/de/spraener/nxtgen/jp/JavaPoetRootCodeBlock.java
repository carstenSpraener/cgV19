package de.spraener.nxtgen.jp;

import com.squareup.javapoet.JavaFile;
import com.squareup.javapoet.TypeSpec;
import de.spraener.nxtgen.ProtectionStrategie;
import de.spraener.nxtgen.ProtectionStrategieDefaultImpl;
import de.spraener.nxtgen.java.JavaCodeBlock;
import de.spraener.nxtgen.oom.model.MClass;

import java.io.IOException;

/**
 * This is the RootCodeBlock produced when a cartridge maps a MClass to an JavaPoetCodeGenerator.
 *
 */
public class JavaPoetRootCodeBlock extends JavaCodeBlock {
    private TypeSpec.Builder typeSpecBuilder;
    private MClass myClass;

    public JavaPoetRootCodeBlock(String srcDir, TypeSpec.Builder typeSpec, MClass mc) {
        super(srcDir, mc.getPackage()!=null?mc.getPackage().getFQName() : "", mc.getName());
        this.myClass = mc;
        this.typeSpecBuilder = typeSpec;
    }

    /**
     * Overwrite the original toCode-Method. It calls the "toString" method on the
     * TypeSpec of this CodeBlock.
     *
     * @return String containing the generated code
     */
    @Override
    public String toCode() {
        try {
            StringBuilder sb = new StringBuilder();
            sb.append(super.toCode());
            JavaFile.builder(myClass.getPackage().getFQName(), typeSpecBuilder.build())
                    .skipJavaLangImports(true)
                    .build()
                    .writeTo(sb);
            return sb.toString();
        } catch( IOException ioXC ) {
            throw new RuntimeException(ioXC);
        }
    }
}
