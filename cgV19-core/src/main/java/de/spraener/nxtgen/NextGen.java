package de.spraener.nxtgen;

import de.spraener.nxtgen.model.Model;
import de.spraener.nxtgen.model.ModelElement;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.ServiceLoader;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * The begining of all the hacks...
 * <p>
 * NextGen is started with a simple parameter, the URI of a Model-File.
 * <p>
 * It starts to locate (with the ServiceLoader-Machanism) a implementation of ModelLoader
 * and asks each of them if it can handel a model with the given URI. Typically there is
 * only one ModelLoader, that can handle the given model URI.
 * <p>
 * After that it loads all Cartridges (again ServiceLoader).
 * <p>
 * Each Cartridge is called with a new instance M of the model laoded by the ModelLoader
 * After that the Transformations of the Cartridge are startet. This results in an
 * enhanced Model M'.
 * <p>
 * After that the Cartridge has to map Generators to ModelElements on the model M'.
 * <p>
 * If that mapping is done, NextGen will start the generation for each mapped ModelElement.
 * <p>
 * The result of a generation call is a CodeBlock. CodeBlocks can contain Java, TypsScript,XML,
 * YAML... whatever your application needs.
 * <p>
 * Each CodeBlock is than given to the Cartridge to write the content into your project.
 */
public class NextGen implements Runnable {
    private static final Logger LOGGER = Logger.getLogger("NextGen");
    private static ProtectionStrategie protectionStrategie = null;
    private String modelURI;
    private final String workingDir;


    private NextGen(String modelURI) {
        this.modelURI = modelURI;
        this.workingDir = new File(".").getAbsolutePath();
    }

    public static ProtectionStrategie getProtectionStrategie() {
        if( protectionStrategie==null) {
            ServiceLoader<ProtectionStrategie> protectionServices = ServiceLoader.load(ProtectionStrategie.class);
            if (!protectionServices.iterator().hasNext()) {
                LOGGER.info("No ProtectionStrategie found. Using default.");
                protectionStrategie = new ProtectionStrategieDefaultImpl();
            } else {
                LOGGER.info("Using ProtectionStrategie '" + protectionStrategie.getClass().getName() + "'.");
                protectionStrategie = protectionServices.iterator().next();
            }
        }
        return protectionStrategie;
    }

    /**
     * Locate all implementations of the ModelLoader Interface with the Java ServiceLoader
     * mechanism. Each found ModelLoader is requested if it can handel a model
     * from the given Model-URI.
     *
     * @return A (maybe empty) list of ModelLoader Implementations
     */
    private List<ModelLoader> locateModelLoader() {
        List<ModelLoader> result = new ArrayList<>();
        LOGGER.info("Loading servcices for Interface " + ModelLoader.class.getName());
        ServiceLoader<ModelLoader> loaderServices = ServiceLoader.load(ModelLoader.class);
        if (!loaderServices.iterator().hasNext()) {
            LOGGER.info("No ModelLoader located. Generation finished.");
            return result;
        }
        loaderServices.forEach(loader -> {
            if (loader.canHandle(modelURI)) {
                result.add(loader);
            }
        });
        return result;
    }

    private List<Cartridge> loadCartridges() {
        final List<Cartridge> result = new ArrayList<>();
        ServiceLoader<Cartridge> loaderServices = ServiceLoader.load(Cartridge.class);
        loaderServices.forEach(result::add);
        StringBuilder sb = new StringBuilder();
        for( Cartridge c : result ) {
            if( sb.length()>0) {
                sb.append(", ");
            }
            sb.append(c.getName());
        }
        LOGGER.info(() -> "found " + result.size() + " cartridges ["+sb+"] as service.");
        return result;
    }

    public void run() {
        try {
            LOGGER.info(() -> "starting codegen in working dir "+getWorkingDir()+" on mode file " + modelURI);
            for (Cartridge c : loadCartridges()) {
                List<Model> models = loadModels(this.modelURI);
                for (Model m : models) {
                    runTransformations(m, c);
                    for (CodeBlock cb : runCodeGenerators(c, m)) {
                        cb.writeOutput(getWorkingDir());
                    }
                }
            }
        } catch (Exception e) {
            throw new NxtGenRuntimeException(e);
        }
    }

    private String getWorkingDir() {
        return this.workingDir;
    }

    private List<Model> loadModels(String modelURI) {
        List<Model> models = new ArrayList();
        for( ModelLoader loader : locateModelLoader() ) {
            if( loader.canHandle(modelURI) ) {
                LOGGER.info(() -> "loading model with loader " + loader.getClass().getName());
                Model m = loader.loadModel(modelURI);
                models.add(m);
            }
        }
        if (models.isEmpty()) {
            LOGGER.info(() -> "no loader could handel model " + modelURI + ". Terminating");
            throw new NxtGenRuntimeException("Unable to find a model loader for the given model uri: "+ modelURI + ". Check your classpath");
        }
        return models;
    }

    private List<CodeBlock> runCodeGenerators(Cartridge cartridge, Model model) {
        List<CodeBlock> result = new ArrayList<>();
        List<CodeGeneratorMapping> mappings = cartridge.mapGenerators(model);
        if (mappings != null) {
            mappings.forEach(m ->
                result.add(m.getCodeGen().resolve(m.getGeneratorBaseELement(), ""))
            );
        }
        return result;
    }

    private void runTransformations(Model model, Cartridge cartridge) {
        List<Transformation> transformations = cartridge.getTransformations();
        if (transformations != null) {
            transformations.forEach(t -> {
                LOGGER.info(()->"Running transformation "+t.getClass().getName());

                List<ModelElement> allElements = model.getModelElements();
                for( ModelElement e : allElements ) {
                    t.doTransformation(e);
                }
            });
        }
    }

    public static void main(String[] args) {
        if (args.length < 1) {
            LOGGER.severe("usage: codegen <ModelURI>");
            return;
        }
        try {
            NextGen cg = new NextGen(args[0]);
            cg.run();
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, e.getMessage(), e);
        }
    }
}
