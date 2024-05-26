package de.spraener.nxtgen.cartridge.meta;

import de.spraener.nxtgen.*;
import de.spraener.nxtgen.model.Model;
import de.spraener.nxtgen.model.ModelElement;
import de.spraener.nxtgen.model.impl.StereotypeImpl;
import de.spraener.nxtgen.oom.OOModelBuilder;
import de.spraener.nxtgen.oom.model.OOModel;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.util.*;
import java.util.function.Consumer;
import java.util.stream.Collectors;

public class CartridgeServiceLocatorTest {
    public static final String WORKING_DIR = "./build/tmp/test-cartridge";

    @BeforeEach
    public void setUp() {
        File f = new File(WORKING_DIR);
        f.mkdirs();
    }

    @AfterEach
    public void tearDown() {
        File wd = new File(WORKING_DIR);
        deleteDir(wd);
    }

    private void deleteDir(File dir) {
        for (File child : dir.listFiles()) {
            if (child.isDirectory()) {
                deleteDir(child);
            }
            child.delete();
        }
    }

    private List<String> listFiles(File rootDir, List<String> toList) {
        for (File child : rootDir.listFiles()) {
            if (child.isDirectory()) {
                listFiles(child, toList);
            } else {
                toList.add(child.getAbsolutePath());
            }
        }
        return toList;
    }

    private List<String> listGeneratedContent() {
        int prefixLength = new File(WORKING_DIR).getAbsolutePath().length() + 1;
        List<String> contentList = listFiles(new File(WORKING_DIR), new ArrayList<>())
                .stream()
                .map(fileName -> fileName.substring(prefixLength))
                .collect(Collectors.toList());
        Collections.sort(contentList);
        return contentList;
    }


    @Test
    public void testCreationOfServiceLocator() {
        OOModel model = createCartridgeModel();
        NextGen.setWorkingDir(WORKING_DIR);
        NextGen.runCartridgeWithName(MetaCartridge.NAME);
        MetaCartridge uut = new MetaCartridge();
        final Map<String, CodeBlock> generatedContentMap = new HashMap<>();
        runCartridge(uut, model);
        List<String> fileNames = listGeneratedContent();
        Assertions.assertThat(fileNames)
                .contains("src/main/resources/META-INF/services/de.spraener.nxtgen.Cartridge");
    }

    private void runCartridge(Cartridge uut, Model m) {
        runCartridge(uut, m, cb -> {
            cb.writeOutput(NextGen.getWorkingDir());
        });
    }

    private void runCartridge(Cartridge uut, Model m, Consumer<CodeBlock> codeBlockConsumer) {
        for (Transformation t : uut.getTransformations()) {
            for (ModelElement me : m.getModelElements()) {
                t.doTransformation(me);
            }
        }
        for (CodeGeneratorMapping mapping : uut.mapGenerators(m)) {
            try {
                CodeBlock cb = mapping.getCodeGen().resolve(mapping.getGeneratorBaseELement(), "");
                codeBlockConsumer.accept(cb);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    private OOModel createCartridgeModel() {
        OOModel model = OOModelBuilder.createModel(
                m -> OOModelBuilder.createPackage(m, "tst.model",
                        p -> OOModelBuilder.createMClass(p, "TestCartridge",
                                c -> c.getStereotypes().add(new StereotypeImpl(MetaStereotypes.CGV19CARTRIDGE.getName()))
                        )
                )
        );
        return model;
    }
}
