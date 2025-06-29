package de.spreaner.nxtgen.annoproc;

import de.spraener.nxtgen.Cartridge;
import de.spraener.nxtgen.NextGen;
import de.spraener.nxtgen.annotations.CGV19Cartridge;

import javax.annotation.processing.AbstractProcessor;
import javax.annotation.processing.Messager;
import javax.annotation.processing.ProcessingEnvironment;
import javax.annotation.processing.RoundEnvironment;
import javax.lang.model.element.Element;
import javax.lang.model.element.TypeElement;
import javax.tools.Diagnostic;
import java.io.File;
import java.util.*;

public class Cgv19AP extends AbstractProcessor {
    private static final String AP_CONFIG_KEY = "cgv19.AP.config";

    private NextGen cg;
    private CGV19Config config;
    private Messager messager;
    private boolean codeGenerated = false;

    @Override
    public void init(ProcessingEnvironment processingEnv) {
        super.init(processingEnv);
        this.messager = processingEnv.getMessager();

        CGV19RuntimeAPImpl cgv19RT = new CGV19RuntimeAPImpl(this,processingEnv);

        String configPath = processingEnv.getOptions().get(AP_CONFIG_KEY);
        if (configPath != null) {
            config = new CGV19Config(messager, configPath);
            cgv19RT.setWorkingDir(config.getWorkingDir());
        } else {
            cgv19RT.setWorkingDir(new File(".").getParent());
        }
        cg = NextGen.getInstance("cgv19:AnnotationProcessor");
        cg.setCgv19Runtime(cgv19RT);
    }

    @Override
    public boolean process(Set<? extends TypeElement> annotations, RoundEnvironment roundEnv) {
        this.codeGenerated = false;
        AnnotationModelBuilder modelBuilder = config.createModelBuilder();
        for (TypeElement te : annotations) {
            for (Element e : roundEnv.getElementsAnnotatedWith(te)) {
                modelBuilder.handleElement(e);
            }
        }
        cg.setCartridgeSupllier(cg::loadCartridges);
        cg.setModelloaderSupplier(() -> List.of(new AnnotationModelLoader(modelBuilder.getModel())));
        cg.run();
        return this.codeGenerated;
    }

    @Override
    public Set<String> getSupportedAnnotationTypes() {
        // TODO: How to get a list of AnnotationTypes used in the project?
        //     * configuration-file
        //     * via the Cartridges? (actual favorit)
        Set<String> annoTypes = new HashSet<>();
        List<Cartridge> cartridges = NextGen.getInstance("cgv19:AnnotationProcessor").loadCartridges();
        for (Cartridge c : cartridges) {
            annoTypes.addAll(c.getAnnotationTypes());
        }
        return annoTypes;
    }

    public void codeWasGenerated() {
        this.codeGenerated = true;
    }
}
