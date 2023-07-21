package de.spraener.nxtgen.blueprint;


import de.spraener.nxtgen.*;
import de.spraener.nxtgen.incubator.BlueprintCompiler;
import de.spraener.nxtgen.model.Model;
import de.spraener.nxtgen.model.ModelElement;
import de.spraener.nxtgen.model.Stereotype;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class BlueprintDirectoryBasedCartridge implements Cartridge, ModelLoader {
    private BlueprintCompiler bpc;
    private String name;

    public BlueprintDirectoryBasedCartridge(File blueprintDir) {
        this.name = blueprintDir.getName();
        BlueprintSupplierAndLister bpSupplierAndLister = new BlueprintSupplierAndLister(blueprintDir);
        this.bpc = new BlueprintCompiler(this.name, bpSupplierAndLister.getFileList(), bpSupplierAndLister);
    }

    @Override
    public boolean canHandle(String modelURI) {
        if (modelURI.endsWith(".props") || modelURI.endsWith(".properties")) {
            return true;
        }
        if (modelURI.endsWith(".yml") || modelURI.endsWith(".yaml")) {
            return true;
        }
        return false;
    }

    @Override
    public Model loadModel(String modelURI) {
        File f = new File(modelURI);
        if( !f.exists() ) {
            try {
                f.createNewFile();
            } catch( IOException xc ) {
                throw new NxtGenRuntimeException(xc);
            }
        }
        if (modelURI.endsWith(".props") || modelURI.endsWith(".properties")) {
            return loadPropertiesModel(modelURI);
        } else if (modelURI.endsWith(".yml") || modelURI.endsWith(".yaml")) {
            return loadYamlModel(modelURI);
        }
        return null;
    }

    private Model loadYamlModel(String modelURI) {
        try {
            return new YamlModelLoader(getName(), bpc, modelURI).loadModel(new FileInputStream(modelURI));
        } catch (IOException xc) {
            throw new NxtGenRuntimeException(xc);
        }
    }

    private Model loadPropertiesModel(String modelURI) {
        try {
            return new PropertyModelLoader(getName(), bpc, modelURI).loadModel(new FileInputStream(modelURI));
        } catch (IOException xc) {
            throw new NxtGenRuntimeException(xc);
        }
    }

    @Override
    public ModelElementFactory getModelElementFactory() {
        return ModelLoader.super.getModelElementFactory();
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public List<Transformation> getTransformations() {
        return new ArrayList<>();
    }

    @Override
    public List<CodeGeneratorMapping> mapGenerators(Model m) {
        List<CodeGeneratorMapping> mappingList = new ArrayList<>();
        for (ModelElement me : m.getModelElements()) {
            if (hasStereotype(me, this.name)) {
                mappingList.add(CodeGeneratorMapping.create(
                        me,
                        new BlueprintGeneratorImpl(this.bpc)
                ));
            }
        }
        return mappingList;
    }

    private boolean hasStereotype(ModelElement me, String name) {
        for (Stereotype st : me.getStereotypes()) {
            if (st.getName().equals(name)) {
                return true;
            }
        }
        return false;
    }
}
