package de.spreaner.nxtgen.annoproc;

import de.spraener.nxtgen.Cartridge;

import javax.annotation.processing.Messager;
import javax.tools.Diagnostic;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

public class CGV19Config {

    private static final String PROP_MODEL_BUILDER = "model.builder";
    private static final String PROP_CARTRIDGE = "cartridge.";
    private static final String PROP_WORKING_DIR = "working.dir";

    private List<Cartridge> cartridgeList = new ArrayList<>();
    private String workingDir = null;
    private String modelBuilderClassName = AnnotationModelBuilderDefaultImpl.class.getName();
    private Messager messager;

    CGV19Config(Messager messager, String configPath) {
        this.messager = messager;
        messager.printMessage(Diagnostic.Kind.NOTE, "CGV19-AP: Reading config from "+configPath);
        try {
            Properties props = new Properties();
            File pFile = new File(configPath);
            if( !pFile.exists() ) {
                return;
            }
            this.workingDir = pFile.getParentFile().getAbsolutePath();

            props.load(new FileInputStream(configPath));
            String modelBuilderClassName = props.getProperty(PROP_MODEL_BUILDER);
            if (modelBuilderClassName != null) {
                this.modelBuilderClassName = modelBuilderClassName;
            }
            props.keySet().stream().filter(
                    k -> k.toString().startsWith(PROP_CARTRIDGE))
                .forEach(
                k -> {
                    try {
                        String cName = (String) props.get(k.toString());
                        cartridgeList.add((Cartridge) Class.forName(cName).getDeclaredConstructor().newInstance());
                    } catch (Exception e) {
                        messager.printMessage(Diagnostic.Kind.ERROR, "CGV19-AP Configuration error in cartridges: " + e.getMessage());
                    }
                }
            );
            if( props.get(PROP_WORKING_DIR)!=null) {
                this.workingDir = props.get(PROP_WORKING_DIR).toString();
            }
        } catch(IOException xc) {
            messager.printMessage(Diagnostic.Kind.ERROR,"CGV19-AP Configuration error: "+xc.getMessage());
        }
    }

    public AnnotationModelBuilder createModelBuilder() {
        try {
            return  (AnnotationModelBuilder) Class.forName(modelBuilderClassName).getDeclaredConstructor().newInstance();
        } catch (Exception e) {
            messager.printMessage(Diagnostic.Kind.ERROR, "CGV19-AP Configuration error in model.builder: " + e.getMessage());
            return null;
        }
    }

    public List<Cartridge> getCartridgeList() {
        return cartridgeList;
    }

    public String getWorkingDir() {
        return workingDir;
    }
}

