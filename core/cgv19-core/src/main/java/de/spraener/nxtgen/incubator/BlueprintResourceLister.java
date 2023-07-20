package de.spraener.nxtgen.incubator;

import org.apache.commons.io.IOUtils;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

/**
 * Responsibility:
 * <p>
 * List all Files and directories in a resource path.
 */
public class BlueprintResourceLister implements BlueprintSupplier {
    private static final Logger LOGGER = Logger.getLogger(BlueprintResourceLister.class.getName());
    private String rsrcPath;
    private List<String> fileList = null;

    public BlueprintResourceLister(String rsrcPath) {
        this.rsrcPath = rsrcPath;
    }

    public List<String> listFiles() {
        if( this.fileList == null ) {
            this.fileList = new ArrayList<>();
            try {
                getResourceFiles(fileList, this.rsrcPath, "");
            } catch (IOException xc) {
                LOGGER.severe(() -> "Error while listing blueprint files under " + this.rsrcPath + ": " + xc.getMessage());
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

    @Override
    public String getContent(String resourceName) {
        try (InputStreamReader isr = getInputStream(resourceName)) {
            return IOUtils.toString(isr);
        } catch (IOException xc) {
            throw new RuntimeException(xc);
        }
    }

    @Override
    public InputStreamReader getInputStream(String resource) {
        return new InputStreamReader(getClass().getResourceAsStream(this.rsrcPath + "/" + resource));
    }
}
