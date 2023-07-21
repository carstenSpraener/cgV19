package de.spraener.nxtgen.blueprint;

import de.spraener.nxtgen.incubator.BlueprintSupplier;

import java.io.*;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class BlueprintSupplierAndLister implements BlueprintSupplier {
    private File rootDir;

    public BlueprintSupplierAndLister(File rootDir) {
        this.rootDir = rootDir;
    }

    public List<String> getFileList() {
        List<String> fileList = new ArrayList<>();
        fillFileList(fileList, ".", rootDir);
        Collections.sort(fileList);
        return fileList;
    }

    private void fillFileList(List<String> fileList, String parentPath, File dir) {
        for( File f : dir.listFiles() ) {
            if( f.isDirectory() ) {
                fillFileList(fileList, parentPath+"/"+f.getName(), f);
            } else {
                fileList.add(parentPath+"/"+f.getName());
            }
        }
    }

    @Override
    public String getContent(String path) {
        try (
                BufferedReader br = new BufferedReader(new FileReader(this.rootDir.getAbsolutePath()+"/"+path));
        ) {
            String line;
            StringBuilder contentBuilder = new StringBuilder();
            while ((line = br.readLine()) != null) {
                contentBuilder.append(line).append("\n");
            }
            return contentBuilder.toString();
        } catch( IOException xc ) {
            throw new RuntimeException(xc);
        }
    }

    @Override
    public InputStreamReader getInputStream(String resource) {
        try {
            return new FileReader(this.rootDir.getAbsolutePath() + "/" + resource);
        } catch( IOException xc) {
            throw new RuntimeException(xc);
        }
    }
}
