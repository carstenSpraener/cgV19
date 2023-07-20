package de.spraener.nxtgen.blueprint;


import de.spraener.nxtgen.*;
import de.spraener.nxtgen.annotations.CGV19Blueprint;
import de.spraener.nxtgen.incubator.BlueprintCompiler;
import de.spraener.nxtgen.incubator.BlueprintGeneratorWrapper;
import de.spraener.nxtgen.model.Model;
import de.spraener.nxtgen.model.ModelElement;
import de.spraener.nxtgen.model.Stereotype;
import de.spraener.nxtgen.model.impl.ModelElementImpl;
import de.spraener.nxtgen.model.impl.ModelImpl;
import de.spraener.nxtgen.model.impl.StereotypeImpl;
import de.spraener.nxtgen.oom.StereotypeHelper;

import java.io.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Properties;

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
        return true;
    }

    @Override
    public Model loadModel(String modelURI) {
        try {
            Properties model = new Properties();
            File f = new File("./" + modelURI);
            if( f.exists() ) {
                model.load(new FileInputStream(f));
            }
            List<String> requiredValues = bpc.getRequiredValues();
            boolean modified = false;
            for (String key : requiredValues) {
                if (!model.containsKey(key)) {
                    model.setProperty(key, requestValueFromUser(key));
                    modified = true;
                }
            }
            if( modified ) {
                try (PrintWriter pw = new PrintWriter("./"+modelURI) ) {
                    for( String propName : model.stringPropertyNames() ) {
                        pw.printf("%s = %s%n", propName, model.get(propName));
                    }
                    pw.flush();
                }
            }
            Model m = new ModelImpl();
            ModelElement me = m.createModelElement();
            me.setModel(m);
            ((ModelImpl)m).addModelElement(me);
            me.getStereotypes().add(new StereotypeImpl(getName()));
            for (String key : model.stringPropertyNames()) {
                me.setProperty(key, model.getProperty(key));
            }

            return m;
        } catch( IOException xc ) {
            throw new RuntimeException(xc);
        }
    }

    private String requestValueFromUser(String key) {
        System.out.println("Please give value for '"+key+"': ");
        String line = System.console().readLine();
        return line;
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
        for( ModelElement me : m.getModelElements() ) {
            if( hasStereotype(me, this.name) ) {
                mappingList.add( CodeGeneratorMapping.create(
                        me,
                        new BlueprintGeneratorImpl(this.bpc)
                        ));
            }
        }
        return mappingList;
    }

    private boolean hasStereotype(ModelElement me, String name) {
        for( Stereotype st : me.getStereotypes()) {
            if( st.getName().equals(name) ) {
                return true;
            }
        }
        return false;
    }
}
