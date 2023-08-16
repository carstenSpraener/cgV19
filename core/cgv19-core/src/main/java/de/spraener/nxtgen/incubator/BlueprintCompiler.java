package de.spraener.nxtgen.incubator;

import com.github.mustachejava.DefaultMustacheFactory;
import com.github.mustachejava.Mustache;
import com.github.mustachejava.MustacheFactory;
import de.spraener.nxtgen.NextGen;

import java.io.*;
import java.nio.charset.Charset;
import java.util.*;
import java.util.logging.Level;

/**
 * <p>A BlueprintCompiler copies a whole subtree from the resource classpath to the
 * given output path. Each file is processed by mustache with the scope defined in
 * the BlueprintCompiler. </p>
 * <p>
 * A sub path in the blueprint can hold variable references them self. For example
 * a path like "{{appname}}/Dockerfile" will be translated to "myapp/Dockerfile" if
 * the value of 'appname' is 'myapp' in the current scope.
 * </p>
 *
 */
public class BlueprintCompiler {
    private List<String> fileList = null;
    private BlueprintSupplier blueprintSupplier = null;
    private Map<String, String> scope = new HashMap<>();
    MustacheFactory factory = new DefaultMustacheFactory();
    private String name;
    /**
     * Initiates the Blueprint-Compiler with the resources under the given
     * resourceRoot-Path.
     *
     * @param resourceRoot
     */
    public BlueprintCompiler(String resourceRoot) {
        this.name = resourceRoot;
        BlueprintResourceLister bprl = new BlueprintResourceLister(resourceRoot);
        this.fileList = bprl.listFiles();
        this.blueprintSupplier = bprl;
        initScope();
    }

    public BlueprintCompiler(String name, List<String> fileList, BlueprintSupplier blueprintSupplier) {
        this.fileList = fileList;
        this.blueprintSupplier = blueprintSupplier;
        initScope();
    }
    public Map<String, String> getScope() {
        return this.scope;
    }

    private void initScope() {
        try {
            for (String resource : this.fileList) {
                for (String varName : listVarNames(resource)) {
                    scope.put(varName, "undefined");
                }
            }
        } catch( RuntimeException rtx ) {
            NextGen.LOGGER.log(Level.SEVERE, "Error in init of Blueprint: "+this.name, rtx);
            throw rtx;
        }
    }

    private Set<String> listVarNames(String resource) {
        Set<String> varList = new HashSet<>();
        findMustaches(resource, varList);
        findMustaches(this.blueprintSupplier.getContent(resource), varList);
        return varList;
    }

    private Set<String> findMustaches(String template, Set<String> varSet) {
        int idx=0;
        while( (idx=template.indexOf("{{", idx))!=-1 ) {
            int endIdx = template.indexOf("}}", idx+2);
            String varName = template.substring(idx+2, endIdx);
            varSet.add(varName);
            idx = endIdx+2;
        }
        return varSet;
    }

    public void evaluateTo(String outDir) {
        for( String fromResource : this.fileList ) {
            String toResource = outDir + "/" + toOutputFilePath(fromResource);
            File outFile = new File(toResource);
            if( NextGen.getProtectionStrategie().isProtected(outFile) ) {
                continue;
            }
            String content = toOutputContent(fromResource);
            outFile.getParentFile().mkdirs();
            try (PrintWriter pw = new PrintWriter(toResource) ) {
                pw.println(content);
                pw.flush();
            } catch( IOException xc ) {
                throw new RuntimeException(xc);
            }
        }
    }

    private String toOutputContent(String resource) {
        try(
                InputStreamReader isr = this.blueprintSupplier.getInputStream(resource);
        ) {
            Mustache mustache = factory.compile(isr, resource);
            try (ByteArrayOutputStream baos = new ByteArrayOutputStream();
                PrintWriter pw = new PrintWriter(baos);
            ) {
                mustache.execute(pw, this.scope);
                pw.flush();
                return new String(baos.toByteArray(), Charset.defaultCharset());
            }
        } catch( IOException xc ) {
            throw new RuntimeException(xc);
        }
    }

    private String toOutputFilePath(String filePathTemplate) {
        String outputFileName =  filePathTemplate;
        for( String varName : listVarNames(filePathTemplate) ) {
            outputFileName = outputFileName.replaceAll("\\{\\{"+varName+"\\}\\}", this.scope.get(varName));
        }
        if( outputFileName.endsWith(".mustache") ) {
            outputFileName = outputFileName.substring(0, outputFileName.length()-9);
        }
        return outputFileName;
    }

    public String getName() {
        return name;
    }

    public List<String> getRequiredValues() {
        List<String> valueList = new ArrayList<>();
        for( String key : this.scope.keySet() ) {
            valueList.add(key);
        }
        Collections.sort(valueList);
        return valueList;
    }
}
