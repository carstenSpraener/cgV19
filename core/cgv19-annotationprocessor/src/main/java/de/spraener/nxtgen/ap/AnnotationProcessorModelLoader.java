package de.spraener.nxtgen.ap;

import de.spraener.nxtgen.ModelLoader;
import de.spraener.nxtgen.model.Model;

import javax.tools.*;
import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class AnnotationProcessorModelLoader implements ModelLoader {

    public static final String PROTOCOL = "java-ap://";
    private Set<Class> supportedAnnotations = new HashSet<>();

    public AnnotationProcessorModelLoader withAnnotations(Class... annotations) {
        for( Class annotation : annotations ) {
            supportedAnnotations.add(annotation);
        }
        return this;
    }

    @Override
    public boolean canHandle(String url) {
        return url != null && url.startsWith(PROTOCOL);
    }

    @Override
    public Model loadModel(String url) {
        try {
            String pathSpec = url.substring(PROTOCOL.length());
            Path sourceDir = Paths.get(pathSpec);

            if (!Files.isDirectory(sourceDir)) {
                throw new IllegalStateException("Model source directory does not exist: " + sourceDir.toAbsolutePath());
            }

            JavaCompiler compiler = ToolProvider.getSystemJavaCompiler();
            if (compiler == null) {
                throw new IllegalStateException("No system Java compiler found. Please run on a JDK.");
            }

            try (StandardJavaFileManager fileManager = compiler.getStandardFileManager(null, null, null)) {
                List<File> sourceFiles = Files.walk(sourceDir)
                        .filter(p -> p.toString().endsWith(".java"))
                        .map(Path::toFile)
                        .toList();

                if (sourceFiles.isEmpty()) {
                    throw new IllegalStateException("No Java source files found in " + sourceDir.toAbsolutePath());
                }

                Iterable<? extends JavaFileObject> compilationUnits =
                        fileManager.getJavaFileObjectsFromFiles(sourceFiles);

                List<String> options = List.of("-proc:only");

                JavaCompiler.CompilationTask task = compiler.getTask(
                        null, fileManager, null, options, null, compilationUnits
                );

                CGV19OomBuildingProcessor processor = new CGV19OomBuildingProcessor(supportedAnnotations);
                task.setProcessors(List.of(processor));

                boolean success = task.call();
                if (!success) {
                    throw new IllegalStateException("Compilation of model sources failed: " + sourceDir.toAbsolutePath());
                }

                Model model = processor.getOOM();
                if (model == null) {
                    throw new IllegalStateException(
                            "No model built - no supported annotations found in " + sourceDir.toAbsolutePath());
                }
                return model;
            }
        } catch (Exception e) {
            throw new RuntimeException("Fehler beim Laden des Modells via AnnotationProcessorModelLoader", e);
        }
    }

}
