package de.spraener.nxtgen.cartridges;

import de.spraener.nxtgen.Cartridge;
import de.spraener.nxtgen.CodeGeneratorMapping;
import de.spraener.nxtgen.Transformation;
import de.spraener.nxtgen.annotations.CGV19Cartridge;
import de.spraener.nxtgen.annotations.CGV19Component;
import de.spraener.nxtgen.annotations.CGV19Generator;
import de.spraener.nxtgen.annotations.CGV19Transformation;
import de.spraener.nxtgen.model.Model;
import de.spraener.nxtgen.model.ModelElement;
import org.reflections.Reflections;
import org.reflections.util.ConfigurationBuilder;
import org.reflections.util.FilterBuilder;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.logging.Logger;

public class AnnotatedCartridgeImpl implements Cartridge {
    private static final Logger LOGGER = Logger.getLogger(AnnotatedCartridgeImpl.class.getName());
    private String name;
    private List<Transformation> transformationList = new ArrayList<>();
    private List<GeneratorWrapper> generatorWrapperList = new ArrayList<>();

    public AnnotatedCartridgeImpl() {
        String pkgName = getClass().getPackage().getName();
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
                if( m.isAnnotationPresent(CGV19Transformation.class) ) {
                    this.transformationList.add(new TransformationWrapper(componentClass, m));
                }
            }
        }
        Set<Class<?>> transformationClasses = reflections.getTypesAnnotatedWith(CGV19Transformation.class);
        for( Class<?> t : transformationClasses ) {
            this.transformationList.add( new TransformationWrapper(t));
        }
        Set<Class<?>> generatorClasses = reflections.getTypesAnnotatedWith(CGV19Generator.class);
        for( Class<?> g : generatorClasses ) {
            this.generatorWrapperList.add(new GeneratorWrapper(g));
        }
        this.name = getClass().getAnnotation(CGV19Cartridge.class).value();
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
                    mappingList.add(CodeGeneratorMapping.create(e, gw));
                }
            }
        }
        return mappingList;
    }

    public List<GeneratorWrapper> getGeneratorWrappers() {
        return this.generatorWrapperList;
    }
}
