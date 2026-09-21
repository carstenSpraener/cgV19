package de.spraener.nxtgen;

import de.spraener.nxtgen.invocation.NextGenInvocation;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Snapshot/restore guard for the global static state of {@link NextGen}.
 * <p>
 * NextGen keeps several pieces of mutable global state (registered cartridges,
 * model loaders, the cartridge name filter, scheduled sub-runs, the inSubRun flag
 * and the working directory) without any public reset API. Tests that mutate this
 * state must wrap their work in a guard so they cannot pollute other tests:
 * <pre>
 * try (NextGenStateGuard ignored = new NextGenStateGuard()) {
 *     // mutate NextGen's static state freely
 * }
 * </pre>
 * or, with JUnit lifecycle methods:
 * <pre>
 * private NextGenStateGuard guard;
 * &#64;BeforeEach void setUp() { guard = new NextGenStateGuard(); }
 * &#64;AfterEach  void tearDown() { guard.close(); }
 * </pre>
 * The constructor snapshots the current state; {@link #close()} restores it,
 * including the active-loader ThreadLocal (reset to null).
 */
public class NextGenStateGuard implements AutoCloseable {

    private final String previousWorkingDir;
    private final List<Cartridge> previousCartridges = new ArrayList<>();
    private final List<ModelLoader> previousModelLoaders = new ArrayList<>();
    private final Set<String> previousCartridgeNames = new HashSet<>();
    private final List<NextGenInvocation> previousScheduledSubRuns = new ArrayList<>();
    private final boolean previousInSubRun;

    public NextGenStateGuard() {
        this.previousWorkingDir = readStaticString("workingDir");
        snapshotStaticList("cartridgeList", previousCartridges);
        snapshotStaticList("modelLoaderList", previousModelLoaders);
        snapshotStaticSet("cartridgeNames", previousCartridgeNames);
        snapshotStaticList("scheduledSubRuns", previousScheduledSubRuns);
        this.previousInSubRun = readStaticBoolean("inSubRun");
    }

    @Override
    public void close() {
        restoreStaticList("cartridgeList", previousCartridges);
        restoreStaticList("modelLoaderList", previousModelLoaders);
        restoreStaticSet("cartridgeNames", previousCartridgeNames);
        restoreStaticList("scheduledSubRuns", previousScheduledSubRuns);
        writeStaticBoolean("inSubRun", previousInSubRun);
        restoreWorkingDir(previousWorkingDir);
        NextGen.setActiveLoader(null);
    }

    /**
     * Test helper: resets the static working directory to {@code null}.
     * <p>
     * {@link NextGen#setWorkingDir(String)} validates the path and cannot express
     * "no working directory", so this is needed to deterministically test the
     * constructor's defaulting behavior. The guard restores the previous value on close.
     */
    public void clearWorkingDir() {
        restoreWorkingDir(null);
    }

    // --- Reflection helpers: NextGen has no public reset API for its static state ---

    private static String readStaticString(String fieldName) {
        try {
            Field f = NextGen.class.getDeclaredField(fieldName);
            f.setAccessible(true);
            return (String) f.get(null);
        } catch (ReflectiveOperationException e) {
            throw new IllegalStateException("Cannot read NextGen." + fieldName, e);
        }
    }

    @SuppressWarnings("unchecked")
    private static <T> void snapshotStaticList(String fieldName, List<T> target) {
        try {
            Field f = NextGen.class.getDeclaredField(fieldName);
            f.setAccessible(true);
            target.clear();
            target.addAll((List<T>) f.get(null));
        } catch (ReflectiveOperationException e) {
            throw new IllegalStateException("Cannot snapshot NextGen." + fieldName, e);
        }
    }

    @SuppressWarnings("unchecked")
    private static <T> void restoreStaticList(String fieldName, List<T> previous) {
        try {
            Field f = NextGen.class.getDeclaredField(fieldName);
            f.setAccessible(true);
            List<T> current = (List<T>) f.get(null);
            current.clear();
            current.addAll(previous);
        } catch (ReflectiveOperationException e) {
            throw new IllegalStateException("Cannot restore NextGen." + fieldName, e);
        }
    }

    @SuppressWarnings("unchecked")
    private static <T> void snapshotStaticSet(String fieldName, Set<T> target) {
        try {
            Field f = NextGen.class.getDeclaredField(fieldName);
            f.setAccessible(true);
            target.clear();
            target.addAll((Set<T>) f.get(null));
        } catch (ReflectiveOperationException e) {
            throw new IllegalStateException("Cannot snapshot NextGen." + fieldName, e);
        }
    }

    @SuppressWarnings("unchecked")
    private static <T> void restoreStaticSet(String fieldName, Set<T> previous) {
        try {
            Field f = NextGen.class.getDeclaredField(fieldName);
            f.setAccessible(true);
            Set<T> current = (Set<T>) f.get(null);
            current.clear();
            current.addAll(previous);
        } catch (ReflectiveOperationException e) {
            throw new IllegalStateException("Cannot restore NextGen." + fieldName, e);
        }
    }

    private static boolean readStaticBoolean(String fieldName) {
        try {
            Field f = NextGen.class.getDeclaredField(fieldName);
            f.setAccessible(true);
            return f.getBoolean(null);
        } catch (ReflectiveOperationException e) {
            throw new IllegalStateException("Cannot read NextGen." + fieldName, e);
        }
    }

    private static void writeStaticBoolean(String fieldName, boolean value) {
        try {
            Field f = NextGen.class.getDeclaredField(fieldName);
            f.setAccessible(true);
            f.set(null, value);
        } catch (ReflectiveOperationException e) {
            throw new IllegalStateException("Cannot write NextGen." + fieldName, e);
        }
    }

    private static void restoreWorkingDir(String previous) {
        try {
            Field f = NextGen.class.getDeclaredField("workingDir");
            f.setAccessible(true);
            // setWorkingDir() validates existence and cannot restore a null value, so use reflection.
            f.set(null, previous);
        } catch (ReflectiveOperationException e) {
            throw new IllegalStateException("Cannot restore NextGen.workingDir", e);
        }
    }
}
