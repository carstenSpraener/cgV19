package de.spraener.nxtgen.cartridges;

import de.spraener.nxtgen.Cartridge;
import de.spraener.nxtgen.CodeGeneratorMapping;
import de.spraener.nxtgen.Transformation;
import de.spraener.nxtgen.annotations.*;
import de.spraener.nxtgen.incubator.BlueprintGeneratorWrapper;
import de.spraener.nxtgen.model.Model;
import de.spraener.nxtgen.model.ModelElement;
import de.spraener.nxtgen.visitor.ElementVisitor;
import de.spraener.nxtgen.visitor.VisitorRegistry;
import org.reflections.Reflections;
import org.reflections.util.ConfigurationBuilder;
import org.reflections.util.FilterBuilder;

import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.logging.Logger;

public class AnnotatedCartridgeImpl implements Cartridge {
    private static final Logger LOGGER = Logger.getLogger(AnnotatedCartridgeImpl.class.getName());
    private String name;
    private List<Transformation> transformationList = new ArrayList<>();
    private List<GeneratorWrapper> generatorWrapperList = new ArrayList<>();

    /** Holds {@code @ElementVisitor} methods discovered on component classes. */
    private final List<ElementVisitorMethod> elementVisitors = new ArrayList<>();

    public AnnotatedCartridgeImpl() {
        init(getClass());
    }

    public AnnotatedCartridgeImpl(Class<?> cartridgeClass) {
        init(cartridgeClass);
    }

    private void init(Class<?> clazz) {
        String pkgName = clazz.getPackage().getName();
        Reflections reflections = new Reflections(
                new ConfigurationBuilder()
                        .forPackage(pkgName)
                        .filterInputsBy(new FilterBuilder().includePackage(pkgName)));
        Set<Class<?>> componentClasses = reflections.getTypesAnnotatedWith(CGV19Component.class);
        for( Class<?> componentClass : componentClasses ) {
            for( Method m  : componentClass.getMethods() ) {
                if( m.isAnnotationPresent(CGV19Generator.class) ) {
                    this.generatorWrapperList.add(new GeneratorWrapper(componentClass, m));
                }
                if( m.isAnnotationPresent(CGV19MustacheGenerator.class) ) {
                    this.generatorWrapperList.add(new MustacheGeneratorWrapper(componentClass, m));
                }
                if( m.isAnnotationPresent(CGV19Blueprint.class) ) {
                    this.generatorWrapperList.add(new BlueprintGeneratorWrapper(componentClass, m));
                }
                if( m.isAnnotationPresent(CGV19Transformation.class) ) {
                    this.transformationList.add(new TransformationWrapper(componentClass, m));
                }
            }
            registerElementVisitors(componentClass);
        }
        Set<Class<?>> transformationClasses = reflections.getTypesAnnotatedWith(CGV19Transformation.class, false);
        for( Class<?> t : transformationClasses ) {
            if(Modifier.isAbstract(t.getModifiers()) ) {
                continue;
            }
            if( !t.isAnnotationPresent(CGV19Transformation.class) ) {
                continue;
            }
            this.transformationList.add( new TransformationWrapper(t));
        }
        Set<Class<?>> generatorClasses = reflections.getTypesAnnotatedWith(CGV19Generator.class, false);
        for( Class<?> g : generatorClasses ) {
            if(Modifier.isAbstract(g.getModifiers()) ) {
                continue;
            }
            if( !g.isAnnotationPresent(CGV19Generator.class) ) {
                continue;
            }
            this.generatorWrapperList.add(new GeneratorWrapper(g));
        }
        if( clazz.isAnnotationPresent(CGV19Cartridge.class)) {
            this.name = clazz.getAnnotation(CGV19Cartridge.class).value();
        } else {
            this.name = "UNDEFINED";
        }
    }

    /**
     * Scan a single component class for {@code @ElementVisitor} methods, instantiate the
     * component once (shared as the invocation owner), and register each method with the
     * global {@link VisitorRegistry}. Also keeps a local copy in {@link #elementVisitors}.
     */
    void registerElementVisitors(Class<?> componentClass) {
        Object owner = null;
        for (Method m : componentClass.getMethods()) {
            ElementVisitor ev = m.getAnnotation(ElementVisitor.class);
            if (ev == null) {
                continue;
            }
            if (owner == null) {
                owner = instantiateComponent(componentClass);
            }
            if (owner == null) {
                LOGGER.warning("Cannot instantiate " + componentClass.getName()
                        + " to register @ElementVisitor method " + m);
                continue;
            }
            VisitorRegistry.register(owner, m, ev.operatesOn(), ev.phase(), ev.order());
            elementVisitors.add(new ElementVisitorMethod(owner, m, ev));
        }
    }

    private Object instantiateComponent(Class<?> componentClass) {
        try {
            return componentClass.getDeclaredConstructor().newInstance();
        } catch (ReflectiveOperationException ex) {
            LOGGER.warning("Failed to instantiate component " + componentClass.getName() + ": " + ex);
            return null;
        }
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public List<Transformation> getTransformations() {
        return this.transformationList;
    }

    @Override
    public List<CodeGeneratorMapping> mapGenerators(Model m) {
        List<CodeGeneratorMapping> mappingList = new ArrayList<>();
        for(ModelElement e : m.getModelElements() ) {
            for( GeneratorWrapper gw : this.generatorWrapperList ) {
                if( gw.matches(e) ) {
                    CodeGeneratorMapping mapping = CodeGeneratorMapping.create(e, gw);
                    mapping.setStereotype(gw.requiredStereotype());
                    mappingList.add(mapping);
                }
            }
        }
        return mappingList;
    }

    public List<GeneratorWrapper> getGeneratorWrappers() {
        return this.generatorWrapperList;
    }

    /** The {@code @ElementVisitor} methods discovered on component classes. */
    public List<ElementVisitorMethod> getElementVisitors() { return this.elementVisitors; }

    /** Simple holder for a visitor method, its invocation owner and annotation data. */
    public static class ElementVisitorMethod {
        private final Object owner;
        private final Method method;
        private final ElementVisitor annotation;

        public ElementVisitorMethod(Object owner, Method method, ElementVisitor annotation) {
            this.owner = owner;
            this.method = method;
            this.annotation = annotation;
        }

        public Object getOwner() { return owner; }
        public Method getMethod() { return method; }
        public ElementVisitor getAnnotation() { return annotation; }
    }
}
