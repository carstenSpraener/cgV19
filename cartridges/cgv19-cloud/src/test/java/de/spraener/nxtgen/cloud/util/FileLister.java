package de.spraener.nxtgen.cloud.util;

import org.assertj.core.api.Assertions;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class FileLister {
    private File root;
    private String rootPath;

    public FileLister(String path) {
        this.rootPath = path;
        this.root = new File(path);
    }

    public List<String> getAllContainedFiles() {
        List<String> fileList = new ArrayList<>();
        listFiles(fileList, root);
        Collections.sort(fileList);
        return fileList;
    }

    private void listFiles(List<String> fileList, File dir) {
        if( dir.isFile() || dir.listFiles()==null || dir.listFiles().length==0 ) {
            fileList.add(dir.getAbsolutePath().substring(this.rootPath.length()+1));
        } else {
            for( File f : dir.listFiles() ) {
                listFiles(fileList, f);
            }
        }
    }

    public void asserContentEquals(String[] expectedFilePaths) {
        List<String> expected = new ArrayList<>();
        expected.addAll(Arrays.asList(expectedFilePaths));
        List<String> actual = getAllContainedFiles();
        Assertions.assertThat(actual)
                .containsAll(expected);
        Assertions.assertThat(expected)
                .containsAll(actual);
    }
}
