package de.spraener.nxtgen.target;

import com.github.mustachejava.DefaultMustacheFactory;
import com.github.mustachejava.Mustache;
import com.github.mustachejava.MustacheFactory;
import de.spraener.nxtgen.model.ModelElement;

import java.io.*;
import java.util.HashMap;
import java.util.Map;

public class CodeBlockSnippet extends CodeSnippet {

    private final String codeBlock;

    public CodeBlockSnippet( String codeBlock ) {
        this(CodeTargetContext.getActiveContext().getAspect(), CodeTargetContext.getActiveContext().getModelElement(), codeBlock);
    }

    public CodeBlockSnippet(Object aspect, ModelElement me, String codeBlock ) {
        super(aspect, me);
        this.codeBlock = codeBlock;
    }

    public static CodeBlockSnippet fromMustacheTemplate( Object aspect, ModelElement e, String mustacheTemplate, String... valueSpecs ) {
        MustacheFactory mf = new DefaultMustacheFactory();
        Mustache m = mf.compile(new StringReader(mustacheTemplate), ""+aspect+"."+e.getName());
        Map<String,String> scope  = new HashMap<>();
        for( String valueSpec : valueSpecs ) {
            String[] keyValue = valueSpec.split("=");
            scope.put(keyValue[0], keyValue[1]);
        }
        StringWriter writer = new StringWriter();
        m.execute(writer, scope);
        return new CodeBlockSnippet(aspect, e, writer.toString());
    }
    @Override
    public void evaluate(StringBuilder sb) {
        if(emptyContent()) {
            return;
        }
        sb.append(this.codeBlock);
    }

    private boolean emptyContent() {
        if( codeBlock==null ) {
            return true;
        }
        for( char c : this.codeBlock.toCharArray() ) {
            if( !Character.isWhitespace(c) ) {
                return false;
            }
        }
        return true;
    }
}
