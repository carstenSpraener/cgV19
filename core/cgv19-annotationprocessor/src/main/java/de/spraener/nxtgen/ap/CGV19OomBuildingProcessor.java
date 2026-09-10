package de.spraener.nxtgen.ap;

import javax.annotation.processing.AbstractProcessor;
import javax.annotation.processing.RoundEnvironment;
import javax.lang.model.SourceVersion;
import javax.lang.model.element.Element;
import javax.lang.model.element.PackageElement;
import javax.lang.model.element.TypeElement;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import de.spraener.nxtgen.oom.model.MAbstractModelElement;
import de.spraener.nxtgen.oom.model.OOModel;

/**
 * Annotation processor that translates annotated Java model types into an
 * in-memory OOM ({@link OOModel}). No file is generated; the resulting model
 * is exposed via {@link #getOOM()} for direct use (e.g. by the
 * AnnotationProcessorModelLoader).
 * <p>
 * Without an explicit annotation list (no-arg constructor, e.g. when discovered
 * via the ServiceLoader) the processor registers for all annotations ("*") and
 * only builds a model if an annotation meta-annotated with
 * {@link de.spraener.nxtgen.ap.metameta.Stereotype} is present.
 */
public class CGV19OomBuildingProcessor extends AbstractProcessor {
    private final Set<Class> supportedAnnotations;
    private OOModel oom;

    public CGV19OomBuildingProcessor() {
        this(null);
    }

    public CGV19OomBuildingProcessor(Set<Class> supportedAnnotations) {
        this.supportedAnnotations = supportedAnnotations;
    }

    private boolean usesWildcard() {
        return supportedAnnotations == null || supportedAnnotations.isEmpty();
    }

    @Override
    public Set<String> getSupportedAnnotationTypes() {
        if (usesWildcard()) {
            return Set.of("*");
        }
        return supportedAnnotations
                .stream().map(c -> c.getCanonicalName())
                .collect(Collectors.toSet());
    }

    @Override
    public SourceVersion getSupportedSourceVersion() {
        return SourceVersion.latestSupported();
    }

    @Override
    public boolean process(Set<? extends TypeElement> annotations, RoundEnvironment roundEnv) {
        if (roundEnv.processingOver()) {
            return false;
        }

        // Wildcard-Modus: nur aktiv, wenn eine @Stereotype-markierte Annotation vorliegt
        if (usesWildcard() && !hasStereotypeAnnotation(annotations)) {
            return false;
        }

        oom = buildOom(roundEnv);
        return true;
    }

    private boolean hasStereotypeAnnotation(Set<? extends TypeElement> annotations) {
        for (TypeElement annotation : annotations) {
            if (StereotypeMapper.isStereotypeAnnotation(annotation)) {
                return true;
            }
        }
        return false;
    }

    public OOModel getOOM() {
        return oom;
    }

    private OOModel buildOom(RoundEnvironment roundEnv) {
        OOModel oom = new OOModel();
        OomModelVisitor visitor = new OomModelVisitor(oom, computeRootPackage(roundEnv));
        for (Element root : roundEnv.getRootElements()) {
            root.accept(visitor, null);
        }
        return oom;
    }

    /**
     * Das Root-Package des OOM ist der gemeinsame Präfix aller Packages der
     * kompilierten Typen. Feld-Typen unterhalb dieses Packages werden als
     * Assoziationen modelliert, alle anderen als normale Attribute.
     */
    private String computeRootPackage(RoundEnvironment roundEnv) {
        List<String> pkgNames = new ArrayList<>();
        for (Element root : roundEnv.getRootElements()) {
            if (!(root instanceof TypeElement te)) {
                continue;
            }
            String pkgName = rootPackageOf(te);
            if (!pkgNames.contains(pkgName)) {
                pkgNames.add(pkgName);
            }
        }
        String prefix = null;
        for (String pkgName : pkgNames) {
            prefix = prefix == null ? pkgName : commonPackagePrefix(prefix, pkgName);
        }
        return prefix == null ? "" : prefix;
    }

    private String rootPackageOf(TypeElement te) {
        if (te.getEnclosingElement() instanceof PackageElement pkg && !pkg.isUnnamed()) {
            return pkg.getQualifiedName().toString();
        }
        return "";
    }

    private String commonPackagePrefix(String a, String b) {
        if (a.isEmpty() || b.isEmpty()) {
            return "";
        }
        String[] segmentsA = a.split("\\.");
        String[] segmentsB = b.split("\\.");
        int i = 0;
        while (i < segmentsA.length && i < segmentsB.length && segmentsA[i].equals(segmentsB[i])) {
            i++;
        }
        if (i == 0) {
            return "";
        }
        return String.join(".", Arrays.copyOf(segmentsA, i));
    }

    private void handleRecursive(OOModel oom, MAbstractModelElement parent, Element e) {
        ElementHandler eh = locateElementHandler(e);
        if( eh==null) {
            return;
        }
        eh.handleElement(oom, parent, e);
    }

    private ElementHandler locateElementHandler(Element child) {
        return ElementHandlerRegistry.instance().getElementHander(child);
    }
}
