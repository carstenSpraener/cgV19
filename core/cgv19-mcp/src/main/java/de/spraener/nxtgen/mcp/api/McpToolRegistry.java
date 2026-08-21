package de.spraener.nxtgen.mcp.api;

import java.io.File;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.net.URI;
import java.net.URL;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;

/**
 * Scans the classpath for @McpTool annotated methods
 * and creates McpToolDescriptor instances.
 */
public class McpToolRegistry {

    /**
     * Scans the given package for @McpTool annotated methods.
     *
     * @param basePackage the package to scan
     * @return discovered tool descriptors
     */
    public McpToolDescriptor[] scan(String basePackage) {
        List<McpToolDescriptor> descriptors = new ArrayList<>();

        for (String className : findClassNames(basePackage)) {
            Class<?> clazz;
            try {
                clazz = Class.forName(className);
            } catch (Throwable t) {
                continue;
            }
            if (clazz.isInterface() || Modifier.isAbstract(clazz.getModifiers())) {
                continue;
            }

            try {
                Object instance = clazz.getDeclaredConstructor().newInstance();

                for (Method method : clazz.getDeclaredMethods()) {
                    McpTool annotation = method.getAnnotation(McpTool.class);
                    if (annotation != null) {
                        method.setAccessible(true);
                        descriptors.add(new McpToolDescriptor(
                                annotation.name(),
                                annotation.description(),
                                annotation.schema(),
                                ctx -> invokeMethod(instance, method, ctx)
                        ));
                    }
                }
            } catch (Exception e) {
                // Skip classes that can't be instantiated
            }
        }

        return descriptors.toArray(new McpToolDescriptor[0]);
    }

    /**
     * Finds the names of all top-level classes in the given package by inspecting
     * the classpath (directories and jars).
     */
    private Set<String> findClassNames(String basePackage) {
        Set<String> classNames = new LinkedHashSet<>();
        String packagePath = basePackage.replace('.', '/');
        try {
            ClassLoader cl = Thread.currentThread().getContextClassLoader();
            if (cl == null) {
                cl = McpToolRegistry.class.getClassLoader();
            }
            Enumeration<URL> resources = cl.getResources(packagePath);
            while (resources.hasMoreElements()) {
                URL url = resources.nextElement();
                if ("file".equals(url.getProtocol())) {
                    collectFromDirectory(new File(url.toURI()), basePackage, classNames);
                } else if ("jar".equals(url.getProtocol())) {
                    collectFromJar(url, packagePath, classNames);
                }
            }
        } catch (Exception e) {
            // Ignore scanning problems; return what was found so far
        }
        return classNames;
    }

    private void collectFromDirectory(File dir, String basePackage, Set<String> classNames) {
        File[] files = dir.listFiles((d, name) -> name.endsWith(".class") && !name.contains("$"));
        if (files == null) {
            return;
        }
        for (File f : files) {
            String name = f.getName();
            classNames.add(basePackage + "." + name.substring(0, name.length() - ".class".length()));
        }
    }

    private void collectFromJar(URL url, String packagePath, Set<String> classNames) {
        try {
            String jarUri = url.toURI().getSchemeSpecificPart();
            int separator = jarUri.indexOf("!");
            if (separator > 0) {
                jarUri = jarUri.substring(0, separator);
            }
            File jarFile = new File(URI.create(jarUri));
            try (JarFile jar = new JarFile(jarFile)) {
                Enumeration<JarEntry> entries = jar.entries();
                String prefix = packagePath + "/";
                while (entries.hasMoreElements()) {
                    JarEntry entry = entries.nextElement();
                    String name = entry.getName();
                    if (name.startsWith(prefix) && name.endsWith(".class") && !name.contains("$")) {
                        classNames.add(name.substring(0, name.length() - ".class".length()).replace('/', '.'));
                    }
                }
            }
        } catch (Exception e) {
            // Ignore unreadable jars
        }
    }

    private McpToolResult invokeMethod(Object instance, Method method, McpToolContext ctx) {
        try {
            Object result = method.invoke(instance, ctx);
            if (result instanceof McpToolResult toolResult) {
                return toolResult;
            }
            return McpToolResult.ok(result != null ? result.toString() : "");
        } catch (Exception e) {
            return McpToolResult.error("Tool execution failed: " + e.getMessage());
        }
    }
}
