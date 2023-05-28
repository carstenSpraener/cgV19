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
    public static final Logger LOGGER = Logger.getLogger("NextGen");
    private static ProtectionStrategie protectionStrategie = null;
    private String modelURI;
    private static String workingDir;
    private static ThreadLocal<ModelLoader> activeLoader = new ThreadLocal<>();


    private NextGen(String modelURI) {
        this.modelURI = modelURI;
        if( this.workingDir == null ) {
            this.workingDir = new File(".").getAbsolutePath();
        }
    }

    public static void setWorkingDir(String workingDir) {
        File f = new File(workingDir);
        if( !f.exists() || !f.isDirectory() ) {
            throw new IllegalArgumentException("assigned working directory '"+workingDir+"' does not exists or is not a directory");
        }
        NextGen.workingDir = workingDir;
    }

    public static ProtectionStrategie getProtectionStrategie() {
        if( protectionStrategie==null) {
            ServiceLoader<ProtectionStrategie> protectionServices = ServiceLoader.load(ProtectionStrategie.class);
            if (!protectionServices.iterator().hasNext()) {
                LOGGER.fine("No ProtectionStrategie found. Using default.");
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
        LOGGER.fine("Loading servcices for Interface " + ModelLoader.class.getName());
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
            LOGGER.info(() -> "starting codegen in working dir "+getWorkingDir()+" on model file " + modelURI);
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

    public static String getWorkingDir() {
        return workingDir;
    }

    private List<Model> loadModels(String modelURI) {
        List<Model> models = new ArrayList();
        for( ModelLoader loader : locateModelLoader() ) {
            if( loader.canHandle(modelURI) ) {
                LOGGER.fine(() -> "loading model with loader " + loader.getClass().getName());
                try {
                    setActiveLoader(loader);
                    Model m = loader.loadModel(modelURI);
                    for (ModelElement e : m.getModelElements()) {
                        e.setModel(m);
                    }
                    models.add(m);
                } finally {
                    removeActiveLoader();
                }
            }
        }
        if (models.isEmpty()) {
            LOGGER.warning(() -> "no loader could handel model " + modelURI + ". Terminating");
            throw new NxtGenRuntimeException("Unable to find a model loader for the given model uri: "+ modelURI + ". Check your classpath");
        }
        return models;
    }

    private static void removeActiveLoader() {
        activeLoader.set(null);
    }

    public static void setActiveLoader(ModelLoader loader) {
        activeLoader.set(loader);
    }

    public static ModelLoader getActiveLoader() {
        return activeLoader.get();
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
                LOGGER.fine(()->"Running transformation "+t.getClass().getName());

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
