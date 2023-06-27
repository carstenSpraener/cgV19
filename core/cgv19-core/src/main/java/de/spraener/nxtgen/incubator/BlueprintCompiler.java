package de.spraener.nxtgen.incubator;

import com.github.mustachejava.DefaultMustacheFactory;
import com.github.mustachejava.Mustache;
import com.github.mustachejava.MustacheFactory;
import org.apache.commons.io.IOUtils;

import java.io.*;
import java.nio.charset.Charset;
import java.util.*;
import java.util.logging.Logger;

/**
 * A BlueprintCompiler copies a whole subtree from the resource classpath to the
 * given output path. Each file is processed by mustache with the scope defined in
 * the BlueprintCompiler. <p/>
 * A sub path in the blueprint can hold variable references them self. For example
 * a path like "{{appname}}/Dockerfile" will be translated to "myapp/Dockerfile" if
 * the value of 'appname' is 'myapp' in the current scope.
 * <p/>
 *
 */
public class BlueprintCompiler {
    private static final Logger LOGGER = Logger.getLogger(BlueprintCompiler.class.getName());
    private String resourceRoot;
    private List<String> fileList = null;
    private Map<String, String> scope = new HashMap<>();
    MustacheFactory factory = new DefaultMustacheFactory();

    /**
     * Initiates the Blueprint-Compiler with the resources under the given
     * resourceRoot-Path.
     *
     * @param resourceRoot
     */
    public BlueprintCompiler(String resourceRoot) {
        this.resourceRoot = resourceRoot;
        initScope();
    }

    public Map<String, String> getScope() {
        return this.scope;
    }

    private void initScope() {
        for( String resource : getFileList() ) {
            for( String varName : listVarNames(resource) ) {
                scope.put(varName, "undefined");
            }
        }
    }

    private Set<String> listVarNames(String resource) {
        Set<String> varList = new HashSet<>();
        findMustaches(resource, varList);
        findMustaches(readContent(resource), varList);
        return varList;
    }

    private String readContent(String resourceName ) {
        try( InputStreamReader isr = new InputStreamReader(getClass().getResourceAsStream(this.resourceRoot+"/"+resourceName))) {
            return IOUtils.toString(isr);
        } catch( IOException xc ) {
            throw new RuntimeException(xc);
        }
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

    /**
     * Returns a List of all files in the blueprint.
     *
     * @return unevaluated List for files in the blueprint.
     */
    public List<String> getFileList() {
        if( this.fileList == null ) {
            this.fileList = new ArrayList<>();
            try {
                getResourceFiles(fileList, this.resourceRoot, "");
            } catch (IOException xc) {
                LOGGER.severe(() -> "Error while listing blueprint files under " + this.resourceRoot + ": " + xc.getMessage());
            }
        }
        return fileList;
    }

    private List<String> getResourceFiles(List<String> filenames, String root, String path) throws IOException {
        try (InputStream in = getResourceAsStream(root+path);
             BufferedReader br = new BufferedReader(new InputStreamReader(in))
        ) {
            String resource;
            while ((resource = br.readLine()) != null) {
                InputStream subStream = getClass().getResourceAsStream(root+path+"/"+resource+"/.");
                if (subStream != null) {
                    getResourceFiles(filenames, root, path + "/" + resource);
                } else {
                    String name = path + "/" + resource;
                    if( name.startsWith("/")) {
                        name = name.substring(1);
                    }
                    filenames.add(name);
                }
            }
        }
        return filenames;
    }

    private InputStream getResourceAsStream(String resource) {
        final InputStream in = getClass().getClassLoader().getResourceAsStream(resource);
        return in == null ? getClass().getResourceAsStream(resource) : in;
    }

    public void evaluteTo(String outDir) {
        for( String fromResource : this.fileList ) {
            String toResource = outDir + "/" + toOutputFilePath(fromResource);
            String content = toOutputContent(fromResource);
            File outFile = new File(toResource);
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
        try( InputStreamReader isr = new InputStreamReader(getClass().getResourceAsStream(this.resourceRoot+"/"+resource))) {
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

    public String getResourceRoot() {
        return resourceRoot;
    }
}
