package de.spraener.nxtgen;

import de.spraener.nxtgen.cartridges.EvaluationRequest;
import de.spraener.nxtgen.invocation.NextGenInvocation;
import de.spraener.nxtgen.model.Model;
import de.spraener.nxtgen.model.ModelElement;
import de.spraener.nxtgen.model.impl.ModelElementImpl;
import de.spraener.nxtgen.model.impl.StereotypeImpl;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.io.File;
import java.lang.reflect.Field;
import java.nio.file.Path;
import java.util.List;
import java.util.Set;
import java.util.concurrent.atomic.AtomicReference;

import org.assertj.core.api.Assertions;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.inOrder;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/**
 * Unit tests for {@link NextGen} covering the unit-testable behaviors:
 * working directory handling, cartridge registration/lookup and name filtering,
 * model loader resolution, transformation/generator dispatch in {@code run()},
 * empty-root-dir callbacks, sub-run scheduling and the evaluateBy* dispatch.
 * <p>
 * Every test runs inside a {@link NextGenStateGuard} so no global state leaks
 * between tests. Model URIs are chosen such that the ServiceLoader-discovered
 * {@code OOMModelLoader} (from cgv19-oom on the test classpath) cannot handle them.
 */
public class NextGenTest {

    private static final String MODEL_URI = "test-model"; // not ".oom", not http → OOMModelLoader ignores it

    private NextGenStateGuard guard;

    @TempDir
    Path tempDir;

    @BeforeEach
    public void setUp() {
        guard = new NextGenStateGuard();
    }

    @AfterEach
    public void tearDown() {
        if (guard != null) {
            guard.close();
        }
    }

    // === working directory handling ===

    @Test
    public void constructorDefaultsWorkingDirToCurrentDirectory() {
        guard.clearWorkingDir();

        new NextGen("any-uri");

        assertThat(NextGen.getWorkingDir()).isEqualTo(new File(".").getAbsolutePath());
    }

    @Test
    public void setWorkingDirAcceptsExistingDirectory() {
        NextGen.setWorkingDir(tempDir.toString());

        assertThat(NextGen.getWorkingDir()).isEqualTo(tempDir.toString());
    }

    @Test
    public void setWorkingDirRejectsNonExistentPath() {
        String bogus = tempDir.resolve("does-not-exist").toString();

        Assertions.assertThatThrownBy(() -> NextGen.setWorkingDir(bogus))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining(bogus);

        assertThat(NextGen.getWorkingDir()).isNull();
    }

    @Test
    public void setWorkingDirRejectsFileInsteadOfDirectory() throws Exception {
        Path file = java.nio.file.Files.createTempFile(tempDir, "file", ".txt");

        Assertions.assertThatThrownBy(() -> NextGen.setWorkingDir(file.toString()))
                .isInstanceOf(IllegalArgumentException.class);

        assertThat(NextGen.getWorkingDir()).isNull();
    }

    // === cartridge registration + name lookup ===

    @Test
    public void loadCartridgesIncludesRegisteredInstancesFirst() {
        Cartridge alpha = mock(Cartridge.class);
        when(alpha.getName()).thenReturn("alpha");
        Cartridge beta = mock(Cartridge.class);
        when(beta.getName()).thenReturn("beta");
        NextGen.addCartridge(alpha);
        NextGen.addCartridge(beta);

        List<Cartridge> loaded = NextGen.loadCartridges();

        assertThat(loaded).contains(alpha, beta);
        // registered instances come first; ServiceLoader-discovered ones are appended after
        assertThat(loaded.get(0)).isSameAs(alpha);
        assertThat(loaded.get(1)).isSameAs(beta);
    }

    @Test
    public void runExecutesOnlyCartridgesMatchingNameFilter() {
        NextGen.setWorkingDir(tempDir.toString());

        Cartridge alpha = mock(Cartridge.class);
        when(alpha.getName()).thenReturn("alpha");
        when(alpha.mapGenerators(any(Model.class))).thenReturn(List.of());
        Cartridge beta = mock(Cartridge.class);
        when(beta.getName()).thenReturn("beta");
        when(beta.mapGenerators(any(Model.class))).thenReturn(List.of());

        Model model = mock(Model.class);
        NextGen.addCartridge(alpha);
        NextGen.addCartridge(beta);
        NextGen.addModelLoader(loaderFor(model));
        NextGen.runCartridgeWithName("alpha");

        new NextGen(MODEL_URI).run();

        verify(alpha).mapGenerators(model);
        verify(beta, never()).mapGenerators(any(Model.class));
    }

    @Test
    public void runExecutesAllCartridgesWhenFilterEmpty() {
        NextGen.setWorkingDir(tempDir.toString());

        Cartridge alpha = mock(Cartridge.class);
        when(alpha.getName()).thenReturn("alpha");
        when(alpha.mapGenerators(any(Model.class))).thenReturn(List.of());

        Model model = mock(Model.class);
        NextGen.addCartridge(alpha);
        NextGen.addModelLoader(loaderFor(model));

        new NextGen(MODEL_URI).run();

        verify(alpha).mapGenerators(model);
    }

    // === model loader resolution + run() dispatch ===

    @Test
    public void runLoadsModelAndWiresElementsToModel() {
        NextGen.setWorkingDir(tempDir.toString());

        ModelElementImpl element = new ModelElementImpl();
        element.setName("e1");
        // deliberately NOT added via model.addModelElement, so only NextGen can wire it:
        Model model = mock(Model.class);
        when(model.getModelElements()).thenReturn(List.of((ModelElement) element));

        NextGen.addCartridge(cartridgeNamed("alpha"));
        NextGen.runCartridgeWithName("alpha");
        NextGen.addModelLoader(loaderFor(model));

        new NextGen(MODEL_URI).run();

        Assertions.assertThat(NextGen.getActiveLoader()).isNull(); // active loader is cleared after loading
        assertThat(element.getModel()).isSameAs(model);
    }

    @Test
    public void runThrowsWhenNoLoaderCanHandle() {
        NextGen.setWorkingDir(tempDir.toString());

        NextGen.addCartridge(cartridgeNamed("alpha"));
        NextGen.runCartridgeWithName("alpha");
        // no model loader registered; OOMModelLoader cannot handle "test-model"

        Assertions.assertThatThrownBy(() -> new NextGen(MODEL_URI).run())
                .isInstanceOf(NxtGenRuntimeException.class)
                .hasMessageContaining("Unable to find a model loader");

        Assertions.assertThat(NextGen.getActiveLoader()).isNull();
    }

    @Test
    public void activeLoaderIsSetDuringLoadAndClearedAfterwards() {
        NextGen.setWorkingDir(tempDir.toString());

        Model model = mock(Model.class);
        AtomicReference<ModelLoader> activeDuringLoad = new AtomicReference<>();

        ModelLoader loader = mock(ModelLoader.class);
        when(loader.canHandle(MODEL_URI)).thenReturn(true);
        when(loader.loadModel(MODEL_URI)).thenAnswer(i -> {
            activeDuringLoad.set(NextGen.getActiveLoader());
            return model;
        });

        NextGen.addCartridge(cartridgeNamed("alpha"));
        NextGen.runCartridgeWithName("alpha");
        NextGen.addModelLoader(loader);

        new NextGen(MODEL_URI).run();

        assertThat(activeDuringLoad.get()).isSameAs(loader);
        assertThat(NextGen.getActiveLoader()).isNull();
    }

    @Test
    public void runAppliesTransformationsToAllModelElements() {
        NextGen.setWorkingDir(tempDir.toString());

        Cartridge cartridge = cartridgeNamed("alpha");
        Transformation t1 = mock(Transformation.class);
        Transformation t2 = mock(Transformation.class);
        when(cartridge.getTransformations()).thenReturn(List.of(t1, t2));

        ModelElement e1 = new ModelElementImpl();
        ModelElement e2 = new ModelElementImpl();
        Model model = mock(Model.class);
        when(model.getModelElements()).thenReturn(List.of(e1, e2));

        NextGen.addCartridge(cartridge);
        NextGen.runCartridgeWithName("alpha");
        NextGen.addModelLoader(loaderFor(model));

        new NextGen(MODEL_URI).run();

        // transformations run in list order, each on every model element
        org.mockito.InOrder inOrder = inOrder(t1, t2);
        inOrder.verify(t1).doTransformation(e1);
        inOrder.verify(t1).doTransformation(e2);
        inOrder.verify(t2).doTransformation(e1);
        inOrder.verify(t2).doTransformation(e2);
    }

    @Test
    public void runWritesGeneratedCodeBlocksToWorkingDir() {
        NextGen.setWorkingDir(tempDir.toString());

        ModelElement element = new ModelElementImpl();
        CodeGenerator codeGen = mock(CodeGenerator.class);
        CodeBlock codeBlock = mock(CodeBlock.class);
        when(codeGen.resolve(any(ModelElement.class), anyString())).thenReturn(codeBlock);

        Cartridge cartridge = cartridgeNamed("alpha");
        when(cartridge.mapGenerators(any(Model.class)))
                .thenReturn(List.of(CodeGeneratorMapping.create(element, codeGen)));

        Model model = mock(Model.class);
        when(model.getModelElements()).thenReturn(List.of(element));

        NextGen.addCartridge(cartridge);
        NextGen.runCartridgeWithName("alpha");
        NextGen.addModelLoader(loaderFor(model));

        new NextGen(MODEL_URI).run();

        verify(codeBlock).writeOutput(tempDir.toString());
    }

    // === empty root dir callbacks ===

    @Test
    public void onEmptyRootDirAndAfterEmptyDirCallbacksInvokedForEmptyDir() {
        NextGen.setWorkingDir(tempDir.toString()); // @TempDir starts empty

        CallbackCartridge cartridge = new CallbackCartridge();
        Model model = mock(Model.class);

        NextGen.addCartridge(cartridge);
        NextGen.runCartridgeWithName(CallbackCartridge.NAME);
        NextGen.addModelLoader(loaderFor(model));

        NextGen nextGen = new NextGen(MODEL_URI);
        nextGen.run();

        assertThat(cartridge.onEmptyCalled).isTrue();
        assertThat(cartridge.seenNextGen).isSameAs(nextGen);
        assertThat(cartridge.seenRootDir.getAbsolutePath())
                .isEqualTo(new File(NextGen.getWorkingDir()).getAbsolutePath());
        assertThat(cartridge.seenModel).isSameAs(model);
    }

    @Test
    public void emptyDirCallbacksSkippedWhenRootDirNotEmpty() throws Exception {
        NextGen.setWorkingDir(tempDir.toString());
        java.nio.file.Files.createFile(tempDir.resolve("some-file.txt"));

        CallbackCartridge cartridge = new CallbackCartridge();
        Model model = mock(Model.class);

        NextGen.addCartridge(cartridge);
        NextGen.runCartridgeWithName(CallbackCartridge.NAME);
        NextGen.addModelLoader(loaderFor(model));

        new NextGen(MODEL_URI).run();

        assertThat(cartridge.onEmptyCalled).isFalse();
        assertThat(cartridge.seenModel).isNull();
    }

    // === evaluateByGiven ===

    @Test
    public void evaluateByGivenReturnsErrorForUnknownCartridge() {
        String result = NextGen.evaluateByGiven("no-such-cartridge", request());

        assertThat(result)
                .isEqualTo("EVALUATION_ERROR: There is no cartridge with name 'no-such-cartridge' on the classpath\n");
    }

    @Test
    public void evaluateByGivenDelegatesToNamedCartridge() {
        Cartridge evaluator = mock(Cartridge.class);
        when(evaluator.getName()).thenReturn("evaluator");
        when(evaluator.evaluate(any(EvaluationRequest.class))).thenReturn("some code");
        NextGen.addCartridge(evaluator);

        EvaluationRequest request = request();

        String result = NextGen.evaluateByGiven("evaluator", request);

        assertThat(result).isEqualTo("some code");
        verify(evaluator).evaluate(request);
    }

    // === evaluateByAny dispatch ===

    @Test
    public void evaluateByAnyReturnsEmptyBlockWhenNoCartridgeHandles() {
        CodeBlock result = NextGen.evaluateByAny(request());

        assertThat(result).isInstanceOf(SimpleStringCodeBlock.class);
        assertThat(((SimpleStringCodeBlock) result).toCode().trim()).isEmpty();
    }

    @Test
    public void evaluateByAnyUsesFirstHandlingCartridge() {
        EvaluationRequest request = request();

        Cartridge first = mock(Cartridge.class);
        when(first.getName()).thenReturn("first");
        when(first.canHandle(request)).thenReturn(true);
        CodeBlock firstBlock = new SimpleStringCodeBlock("one");
        when(first.subEvaluate(request)).thenReturn(firstBlock);

        Cartridge second = mock(Cartridge.class);
        when(second.getName()).thenReturn("second");
        when(second.canHandle(request)).thenReturn(true);
        CodeBlock secondBlock = new SimpleStringCodeBlock("two");
        when(second.subEvaluate(request)).thenReturn(secondBlock);

        NextGen.addCartridge(first);
        NextGen.addCartridge(second);

        CodeBlock result = NextGen.evaluateByAny(request);

        assertThat(result).isSameAs(firstBlock);
        verify(second, never()).subEvaluate(any(EvaluationRequest.class));
    }

    @Test
    public void evaluateByAnyIgnoresNonHandlingCartridges() {
        EvaluationRequest request = request();

        Cartridge handling = mock(Cartridge.class);
        when(handling.getName()).thenReturn("handling");
        when(handling.canHandle(request)).thenReturn(true);
        CodeBlock block = new SimpleStringCodeBlock("handled");
        when(handling.subEvaluate(request)).thenReturn(block);

        Cartridge nonHandling = mock(Cartridge.class);
        when(nonHandling.getName()).thenReturn("non-handling");
        when(nonHandling.canHandle(request)).thenReturn(false);

        NextGen.addCartridge(handling);
        NextGen.addCartridge(nonHandling);

        CodeBlock result = NextGen.evaluateByAny(request);

        assertThat(result).isSameAs(block);
        verify(nonHandling, never()).subEvaluate(any(EvaluationRequest.class));
    }

    // === sub-run scheduling ===

    @Test
    public void scheduledSubRunsExecuteInFifoOrderAfterMainRun() {
        NextGen.setWorkingDir(tempDir.toString());

        NextGen.addCartridge(cartridgeNamed("alpha"));
        NextGen.runCartridgeWithName("alpha");
        NextGen.addModelLoader(loaderFor(mock(Model.class)));

        NextGenInvocation sub1 = mock(NextGenInvocation.class);
        NextGenInvocation sub2 = mock(NextGenInvocation.class);
        NextGen.scheduleInvocation(sub1);
        NextGen.scheduleInvocation(sub2);

        new NextGen(MODEL_URI).run();

        org.mockito.InOrder inOrder = inOrder(sub1, sub2);
        inOrder.verify(sub1).run();
        inOrder.verify(sub2).run();

        assertThat(readScheduledSubRuns()).isEmpty(); // queue is drained
    }

    @Test
    public void subRunSchedulingClearsCartridgeNameFilter() {
        NextGen.setWorkingDir(tempDir.toString());

        NextGen.addCartridge(cartridgeNamed("alpha"));
        NextGen.runCartridgeWithName("alpha"); // filter for the main run
        NextGen.addModelLoader(loaderFor(mock(Model.class)));

        NextGen.scheduleInvocation(mock(NextGenInvocation.class));

        new NextGen(MODEL_URI).run();

        // the sub-run loop clears the name filter before each sub-run
        assertThat(readCartridgeNames()).isEmpty();
    }

    // === executeCommand ===

    @Test
    public void executeCommandRunsVarargsCommand() throws Exception {
        new NextGen("x").executeCommand(tempDir.toFile(), null, "echo", "hello");
        // no exception = exit code 0
    }

    @Test
    public void executeCommandSplitsStringArguments() throws Exception {
        new NextGen("x").executeCommand(tempDir.toFile(), null, "echo hello");
        // no exception = exit code 0
    }

    @Test
    public void executeCommandAppliesProcessBuilderModifier() throws Exception {
        AtomicReference<ProcessBuilder> captured = new AtomicReference<>();

        new NextGen("x").executeCommand(tempDir.toFile(), captured::set, "echo", "hi");

        assertThat(captured.get().directory()).isEqualTo(tempDir.toAbsolutePath().toFile());
    }

    @Test
    public void executeCommandThrowsOnNonZeroExit() throws Exception {
        Assertions.assertThatThrownBy(
                () -> new NextGen("x").executeCommand(tempDir.toFile(), null, "false"))
                .isInstanceOf(RuntimeException.class)
                .hasMessageContaining("Could not execute command");
    }

    // === protection strategie ===

    @Test
    public void protectionStrategieIsCached() {
        ProtectionStrategie first = NextGen.getProtectionStrategie();

        assertThat(first).isNotNull();
        assertThat(NextGen.getProtectionStrategie()).isSameAs(first);
    }

    // === main() ===

    @Test
    public void mainWithoutArgumentsDoesNothing() {
        Assertions.assertThatCode(() -> NextGen.main(new String[0])).doesNotThrowAnyException();
    }

    // === helpers ===

    private static ModelLoader loaderFor(Model model) {
        ModelLoader loader = mock(ModelLoader.class);
        when(loader.canHandle(MODEL_URI)).thenReturn(true);
        when(loader.loadModel(MODEL_URI)).thenReturn(model);
        return loader;
    }

    private static Cartridge cartridgeNamed(String name) {
        Cartridge cartridge = mock(Cartridge.class);
        when(cartridge.getName()).thenReturn(name);
        return cartridge;
    }

    private static EvaluationRequest request() {
        ModelElementImpl me = new ModelElementImpl();
        me.setName("request-element");
        return new EvaluationRequest(me, new StereotypeImpl("X"), "aspect", "subAspect");
    }

    /** Reads the private static scheduledSubRuns list (no public accessor exists). */
    @SuppressWarnings("unchecked")
    private static List<NextGenInvocation> readScheduledSubRuns() {
        try {
            Field f = NextGen.class.getDeclaredField("scheduledSubRuns");
            f.setAccessible(true);
            return (List<NextGenInvocation>) f.get(null);
        } catch (ReflectiveOperationException e) {
            throw new IllegalStateException("Cannot read NextGen.scheduledSubRuns", e);
        }
    }

    /** Reads the private static cartridgeNames set (no public accessor exists). */
    @SuppressWarnings("unchecked")
    private static Set<String> readCartridgeNames() {
        try {
            Field f = NextGen.class.getDeclaredField("cartridgeNames");
            f.setAccessible(true);
            return (Set<String>) f.get(null);
        } catch (ReflectiveOperationException e) {
            throw new IllegalStateException("Cannot read NextGen.cartridgeNames", e);
        }
    }

    /**
     * Real (non-mock) cartridge implementing the empty-root-dir callbacks,
     * so the tests observe plain boolean/field state instead of mock interactions.
     */
    static class CallbackCartridge implements Cartridge, OnEmptyRootDir, AfterEmptyDir {
        static final String NAME = "callback-cartridge";

        boolean onEmptyCalled;
        NextGen seenNextGen;
        File seenRootDir;
        Model seenModel;

        @Override
        public String getName() {
            return NAME;
        }

        @Override
        public List<Transformation> getTransformations() {
            return null;
        }

        @Override
        public List<CodeGeneratorMapping> mapGenerators(Model m) {
            return List.of();
        }

        @Override
        public void onEmptyRootDir(NextGen nextGen, File rootDir) {
            this.onEmptyCalled = true;
            this.seenNextGen = nextGen;
            this.seenRootDir = rootDir;
        }

        @Override
        public void afterEmptyRootDir(NextGen nextGen, File rootDir, Model m) {
            this.seenModel = m;
        }
    }
}
