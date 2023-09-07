package de.spraener.nxtgen.laravel.tools;

import com.github.mustachejava.DefaultMustacheFactory;
import com.github.mustachejava.Mustache;
import com.github.mustachejava.MustacheFactory;

import java.io.StringReader;
import java.io.StringWriter;
import java.util.HashMap;
import java.util.Map;

public class MustacheSnippet {
    private static MustacheFactory mf = new DefaultMustacheFactory();
    private Mustache m;

    public MustacheSnippet(String mustacheTemplate) {
        this.m = mf.compile(new StringReader(mustacheTemplate), "");
    }

    public String evaluateWith(String... valueSpecs) {
        Map<String, String> scope = new HashMap<>();
        for (String valueSpec : valueSpecs) {
            String[] keyValue = valueSpec.split("=");
            if( keyValue.length==2 ) {
                scope.put(keyValue[0], keyValue[1]);
            } else {
                scope.put(keyValue[0], "");
            }
        }
        StringWriter writer = new StringWriter();
        m.execute(writer, scope);
        return writer.toString();
    }
}
