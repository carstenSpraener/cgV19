package de.spraener.nxtgen;

import de.spraener.nxtgen.invocation.NextGenInvocation;
import de.spraener.nxtgen.model.Model;
import de.spraener.nxtgen.model.ModelElement;
import de.spraener.nxtgen.model.Stereotype;

import java.io.File;
import java.util.*;
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
    private static Set<String> cartridgeNames = new HashSet<>();
    private static List<Cartridge> cartridgeList = new ArrayList<>();
    private static List<ModelLoader> modelLoaderList = new ArrayList<>();

    private static List<NextGenInvocation> scheduledSubRuns = new ArrayList<>();
    private static boolean inSubRun = false;

    private NextGen(String modelURI) {
        this.modelURI = modelURI;
        if (this.workingDir == null) {
            this.workingDir = new File(".").getAbsolutePath();
        }
    }

    public static synchronized void scheduleInvocation(NextGenInvocation invocation) {
        scheduledSubRuns.add(invocation);
    }

    public static void setWorkingDir(String workingDir) {
        File f = new File(workingDir);
        if (!f.exists() || !f.isDirectory()) {
            throw new IllegalArgumentException("assigned working directory '" + workingDir + "' does not exists or is not a directory");
        }
        NextGen.workingDir = workingDir;
    }

    public static ProtectionStrategie getProtectionStrategie() {
        if (protectionStrategie == null) {
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

    public static void runCartridgeWithName(String cartridgeName) {
        cartridgeNames.add(cartridgeName);
    }

    public static void addCartridge(Cartridge c) {
        cartridgeList.add(c);
    }

    public static void addModelLoader(ModelLoader ml) {
        modelLoaderList.add(ml);
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
        result.addAll(modelLoaderList);
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

    public static List<Cartridge> loadCartridges() {
        final List<Cartridge> result = new ArrayList<>();
        result.addAll(cartridgeList);
        ServiceLoader<Cartridge> loaderServices = ServiceLoader.load(Cartridge.class);
        loaderServices.forEach(result::add);
        StringBuilder sb = new StringBuilder();
        for (Cartridge c : result) {
            if (sb.length() > 0) {
                sb.append(", ");
            }
            sb.append(c.getName());
        }
        LOGGER.info(() -> "found " + result.size() + " cartridges [" + sb + "].");
        return result;
    }

    public void run() {
        try {
            String fqWorkingDir = new File(getWorkingDir()).getAbsolutePath();
            LOGGER.info(() -> "starting codegen in working dir " + fqWorkingDir + " on model file " + modelURI);
            for (Cartridge c : loadCartridges()) {
                if (cartridgeNames.isEmpty() || cartridgeNames.contains(c.getName())) {
                    List<Model> models = loadModels(this.modelURI);
                    for (Model m : models) {
                        runTransformations(m, c);
                        for (CodeBlock cb : runCodeGenerators(c, m)) {
                            cb.writeOutput(getWorkingDir());
                        }
                    }
                }
            }
        } catch (Exception e) {
            throw new NxtGenRuntimeException(e);
        }

        if (!inSubRun) {
            inSubRun = true;
            while(!scheduledSubRuns.isEmpty()) {
                cartridgeNames.clear();
                scheduledSubRuns.remove(0).run();
            }
        }
    }

    public static String getWorkingDir() {
        return workingDir;
    }

    private List<Model> loadModels(String modelURI) {
        List<Model> models = new ArrayList();
        for (ModelLoader loader : locateModelLoader()) {
            if (loader.canHandle(modelURI)) {
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
            LOGGER.severe(() -> "no loader could handel model " + modelURI + ". Terminating");
            throw new NxtGenRuntimeException("Unable to find a model loader for the given model uri: " + modelURI + ". Check your classpath");
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
                LOGGER.fine(() -> "Running transformation " + t.getClass().getName());

                List<ModelElement> allElements = model.getModelElements();
                for (ModelElement e : allElements) {
                    t.doTransformation(e);
                }
            });
        }
    }

    /**
     * <p>A Cartridge wants to make use of another cartridge to generate some output. If the given
     * cartridge name can be resolved and the cartridge supports evaluation, the method calls the
     * desired cartridge and let it evaluate the given aspect.
     * </p>
     * <p>
     * The first usecas of this method wath to generate a docker-compose file from the cloud cartridge.
     * The cloud cartridge generates sub models for each cloud module and let another cartridge generate
     * the code inside this module. Later the cloud cartridge needs to generate a docker-compose file which
     * contains a service block for each cloud module. The generation of this service block is delegated
     * back to the module cartridge via this method.
     * </p>
     * <p>
     *     Without this method the cloud cartridge has to know how to generate a docker-compose service block
     *     for each included cartridge. This method avoids this dependency.
     * </p>
     * @param cartridgeName the name of a cartridge. Should be on the classpath
     * @param model The model to be used for evaluation
     * @param me the model element that should be used for evaluation
     * @param aspect an optional (can be null) parameter to narrow the needed evaluation. For example "docker-compose"
     *
     * @return a String with the output of the evaluation.
     */
    public static String evaluate(String cartridgeName, Model model, ModelElement me, Stereotype sType, String aspect) {
        Cartridge cartridge = loadCartridges().stream().filter(c->c.getName().equals(cartridgeName)).findFirst().orElse(null);
        if( cartridge == null ) {
            return "EVALUATION_ERROR: There is no cartridge with name '"+cartridgeName+"' on the classpath\n";
        }
        return cartridge.evaluate(model, me, sType, aspect);
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
