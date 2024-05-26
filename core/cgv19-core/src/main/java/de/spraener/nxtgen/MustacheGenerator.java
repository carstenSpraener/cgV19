package de.spraener.nxtgen;

import com.github.mustachejava.DefaultMustacheFactory;
import com.github.mustachejava.Mustache;
import com.github.mustachejava.MustacheFactory;
import de.spraener.nxtgen.model.ModelElement;

import java.io.*;
import java.util.HashMap;
import java.util.Map;
import java.util.function.BiConsumer;

public class MustacheGenerator implements CodeGenerator {
    private String mustacheResourcePath;
    private String outputFile;
    private BiConsumer<ModelElement, Map<String,Object>>[] buildScriptMapConsumer;

    public MustacheGenerator(String mustacheResourcePath, String outputFile, BiConsumer<ModelElement, Map<String,Object>>... buildScriptMapConsumer) {
        this.mustacheResourcePath = mustacheResourcePath;
        this.outputFile = outputFile;
        this.buildScriptMapConsumer = buildScriptMapConsumer;
    }

    @Override
    public CodeBlock resolve(ModelElement modelElement, String templateName) {
            MustacheFactory mf = new DefaultMustacheFactory();
            Mustache  mustache;
        try {
            try {
                mustache = mf.compile(this.mustacheResourcePath);
            } catch( Exception e ) {
            mustache = mf.compile(
                    new InputStreamReader(getClass().getResourceAsStream(this.mustacheResourcePath)),
                    this.mustacheResourcePath
            );
            }
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            PrintWriter pw = new PrintWriter(baos);
            Map<String, Object> scope = new HashMap<>();
            if (this.buildScriptMapConsumer != null) {
                for (BiConsumer<ModelElement, Map<String, Object>> consumer : this.buildScriptMapConsumer) {
                    consumer.accept(modelElement, scope);
                }
            }
            mustache.execute(pw, scope).flush();
            ensurePathExists(this.outputFile);
            return new SimpleFileWriterCodeBlock(new String(baos.toByteArray()), this.outputFile);
        } catch( IOException ioXC ) {
            throw new RuntimeException(ioXC);
        }
    }

    private void ensurePathExists(String outputFile) {
        File of = new File(outputFile);
        of.getParentFile().mkdirs();
    }
}
